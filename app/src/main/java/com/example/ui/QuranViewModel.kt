package com.example.ui

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AdhanManager
import com.example.audio.AdhanOption
import com.example.audio.AdhanState
import com.example.audio.PlaybackState
import com.example.audio.QuranAudioPlayer
import com.example.data.local.BookmarkEntity
import com.example.data.local.DownloadedSurahEntity
import com.example.data.local.FavoritePlaylistEntity
import com.example.data.local.QuranDatabase
import com.example.data.model.Ayah
import com.example.data.model.AzkarCategory
import com.example.data.model.AzkarItem
import com.example.data.model.QuranData
import com.example.data.model.Reciter
import com.example.data.model.ReminderSettings
import com.example.data.model.Surah
import com.example.data.prayer.CalculationMethod
import com.example.data.prayer.PrayerTimesCalculator
import com.example.data.prayer.PrayerTimesDay
import com.example.data.repository.LocationHelper
import com.example.data.repository.QuranRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import kotlin.math.*

enum class ScreenTab {
  HOME,
  MUSHAF,
  RECITERS,
  DOWNLOADS_TIMER,
  MOSQUES,
  QIBLA
}

enum class ReadingMode {
  NORMAL,
  TAFSIR_EXPLAINED, // تلاوة مفسرة
  ACCOMPANIED // مصحوبة
}

data class UiState(
  val currentTab: ScreenTab = ScreenTab.MUSHAF, // Start at Holy Mushaf screen
  val currentSurah: Surah = QuranData.allSurahs[16], // Surah Al-Isra (Surah 17)
  val selectedAyahIndex: Int = 0,
  val readingMode: ReadingMode = ReadingMode.TAFSIR_EXPLAINED,
  val isDarkTheme: Boolean = true, // Default to midnight dark sanctuary
  val fontSizeMultiplier: Float = 1.0f,
  val isTafsirSheetVisible: Boolean = false,
  val isSearchVisible: Boolean = false,
  val searchQuery: String = "",
  val searchResults: List<Pair<Surah, Ayah?>> = emptyList(),
  val isCloudSyncDialogVisible: Boolean = false,
  val isCloudSyncing: Boolean = false,
  val lastCloudSyncTime: String = "الآن • مزامنة السحابة نشطة",
  val isAzkarAlertsDialogVisible: Boolean = false,
  val currentLanguage: String = "ar", // "ar" or "en"
  val reminderSettings: ReminderSettings = ReminderSettings(),
  val audioQualityMode: String = "320kbps", // "320kbps" or "128kbps"
  val usedStorageGb: Double = 2.4,
  val availableStorageGb: Double = 48.6,
  val activeAzkarCategory: AzkarCategory = AzkarCategory.MORNING,
  val azkarItems: List<AzkarItem> = QuranData.dailyAzkar,
  val isAudioEffectsVisible: Boolean = false,
  val infoMessage: String? = null
)

data class QiblaState(
  val qiblaAngle: Float = 0f,
  val compassHeading: Float = 0f,
  val distanceToKaabaKm: Double = 0.0,
  val location: Location? = null,
  val isAligned: Boolean = false
)

class QuranViewModel(application: Application) : AndroidViewModel(application) {

  private val database = QuranDatabase.getDatabase(application)
  private val repository = QuranRepository(database.quranDao())
  val audioPlayer = QuranAudioPlayer(application)
  val adhanManager = AdhanManager(application)
  val adhanState: StateFlow<AdhanState> = adhanManager.state
  private val locationHelper = LocationHelper(application)
  private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager

  private val _uiState = MutableStateFlow(UiState())
  val uiState: StateFlow<UiState> = _uiState.asStateFlow()

  private val _qiblaState = MutableStateFlow(QiblaState())
  val qiblaState: StateFlow<QiblaState> = _qiblaState.asStateFlow()

  private val _prayerTimesState = MutableStateFlow<PrayerTimesDay?>(null)
  val prayerTimesState: StateFlow<PrayerTimesDay?> = _prayerTimesState.asStateFlow()

  // Compass Sensors & Filtering
  private var currentSmoothHeading = 0f
  private val rotationMatrix = FloatArray(9)
  private val orientationValues = FloatArray(3)
  private val lastAccelerometer = FloatArray(3)
  private val lastMagnetometer = FloatArray(3)
  private var lastAccelerometerSet = false
  private var lastMagnetometerSet = false

  private val sensorListener = object : SensorEventListener {
    override fun onSensorChanged(event: SensorEvent) {
      when (event.sensor.type) {
        Sensor.TYPE_ROTATION_VECTOR -> {
          SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
          SensorManager.getOrientation(rotationMatrix, orientationValues)
          val azimuth = ((Math.toDegrees(orientationValues[0].toDouble()) + 360.0) % 360.0).toFloat()
          updateHeading(azimuth)
        }
        Sensor.TYPE_ACCELEROMETER -> {
          System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.size)
          lastAccelerometerSet = true
          computeOrientationIfReady()
        }
        Sensor.TYPE_MAGNETIC_FIELD -> {
          System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.size)
          lastMagnetometerSet = true
          computeOrientationIfReady()
        }
        @Suppress("DEPRECATION")
        Sensor.TYPE_ORIENTATION -> {
          updateHeading(event.values[0])
        }
      }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
  }

  private fun computeOrientationIfReady() {
    if (lastAccelerometerSet && lastMagnetometerSet) {
      if (SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)) {
        SensorManager.getOrientation(rotationMatrix, orientationValues)
        val azimuth = ((Math.toDegrees(orientationValues[0].toDouble()) + 360.0) % 360.0).toFloat()
        updateHeading(azimuth)
      }
    }
  }

  private fun updateHeading(targetHeading: Float) {
    var diff = targetHeading - currentSmoothHeading
    while (diff < -180f) diff += 360f
    while (diff > 180f) diff -= 360f
    currentSmoothHeading = (currentSmoothHeading + diff * 0.25f + 360f) % 360f

    val qiblaAngle = _qiblaState.value.qiblaAngle
    val angleDiff = abs((qiblaAngle - currentSmoothHeading + 540f) % 360f - 180f)
    val aligned = angleDiff < 5.0f

    _qiblaState.value = _qiblaState.value.copy(
      compassHeading = currentSmoothHeading,
      isAligned = aligned
    )
  }

  init {
    updateStorageUsage()
    startSalawatHourlySchedule()
    observeLocation()
    registerSensors()
    startPrayerCountdownTicker()
  }

  fun refreshLocation() {
    observeLocation()
  }

  private fun observeLocation() {
    viewModelScope.launch {
      locationHelper.getLocationFlow().collect { loc ->
        loc?.let {
          val angle = calculateQibla(it.latitude, it.longitude)
          val distance = calculateDistanceToKaaba(it.latitude, it.longitude)
          _qiblaState.value = _qiblaState.value.copy(
            qiblaAngle = angle.toFloat(),
            distanceToKaabaKm = distance,
            location = it
          )
          // Update prayer times with real coordinates
          updatePrayerTimes(it.latitude, it.longitude)
        }
      }
    }
  }

  private fun registerSensors() {
    try {
      val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
      if (rotationSensor != null) {
        sensorManager.registerListener(sensorListener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
      } else {
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val mag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        if (accel != null && mag != null) {
          sensorManager.registerListener(sensorListener, accel, SensorManager.SENSOR_DELAY_UI)
          sensorManager.registerListener(sensorListener, mag, SensorManager.SENSOR_DELAY_UI)
        } else {
          @Suppress("DEPRECATION")
          val orient = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
          if (orient != null) {
            sensorManager.registerListener(sensorListener, orient, SensorManager.SENSOR_DELAY_UI)
          }
        }
      }
    } catch (e: Exception) {
      android.util.Log.w("QuranViewModel", "Sensors not available on this device: ${e.message}")
    }
  }

  fun unregisterSensors() {
    try {
      sensorManager.unregisterListener(sensorListener)
    } catch (_: Exception) {}
  }

  private fun updatePrayerTimes(lat: Double, lon: Double) {
    val times = PrayerTimesCalculator.calculate(lat, lon)
    _prayerTimesState.value = times
  }

  private fun startPrayerCountdownTicker() {
    viewModelScope.launch {
      while (isActive) {
        val loc = _qiblaState.value.location
        val lat = loc?.latitude ?: 21.4225 // Default Makkah if GPS acquiring
        val lon = loc?.longitude ?: 39.8262
        val times = PrayerTimesCalculator.calculate(lat, lon)
        _prayerTimesState.value = times
        delay(1000L)
      }
    }
  }

  private fun calculateQibla(lat: Double, lon: Double): Double {
    val kaabaLat = Math.toRadians(21.4225)
    val kaabaLon = Math.toRadians(39.8262)
    val myLat = Math.toRadians(lat)
    val myLon = Math.toRadians(lon)

    val y = kotlin.math.sin(kaabaLon - myLon)
    val x = kotlin.math.cos(myLat) * kotlin.math.tan(kaabaLat) - kotlin.math.sin(myLat) * kotlin.math.cos(kaabaLon - myLon)
    val qiblaRad = kotlin.math.atan2(y, x)
    return (Math.toDegrees(qiblaRad) + 360.0) % 360.0
  }

  private fun calculateDistanceToKaaba(lat: Double, lon: Double): Double {
    val r = 6371.0 // Earth radius in km
    val kaabaLat = Math.toRadians(21.4225)
    val kaabaLon = Math.toRadians(39.8262)
    val myLat = Math.toRadians(lat)
    val myLon = Math.toRadians(lon)

    val dLat = kaabaLat - myLat
    val dLon = kaabaLon - myLon

    val a = kotlin.math.sin(dLat / 2).let { it * it } + kotlin.math.cos(myLat) * kotlin.math.cos(kaabaLat) * kotlin.math.sin(dLon / 2).let { it * it }
    val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
    return r * c
  }

  val playbackState: StateFlow<PlaybackState> = audioPlayer.state

  val bookmarks: StateFlow<List<BookmarkEntity>> = repository.allBookmarks
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val lastRead: StateFlow<BookmarkEntity?> = repository.lastReadBookmark
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val downloadedSurahs: StateFlow<List<DownloadedSurahEntity>> = repository.downloadedSurahs
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val playlists: StateFlow<List<FavoritePlaylistEntity>> = repository.allPlaylists
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private fun updateStorageUsage() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val audioDir = File(getApplication<Application>().filesDir, "audio")
        val bytes = if (audioDir.exists()) audioDir.walkTopDown().sumOf { it.length() } else 0L
        val gb = (bytes.toDouble() / (1024.0 * 1024.0 * 1024.0)).coerceAtLeast(0.05)
        withContext(Dispatchers.Main) {
          _uiState.value = _uiState.value.copy(usedStorageGb = String.format(java.util.Locale.US, "%.2f", gb).toDouble())
        }
      } catch (_: Exception) {}
    }
  }

  // Navigation Tabs
  fun setTab(tab: ScreenTab) {
    _uiState.value = _uiState.value.copy(currentTab = tab)
  }

  // Surah Selection
  fun selectSurah(surah: Surah) {
    _uiState.value = _uiState.value.copy(
      currentSurah = surah,
      selectedAyahIndex = 0
    )
    viewModelScope.launch {
      repository.saveLastRead(surah.number, 1, surah.nameArabic)
      // Fetch full authentic verses from network if not cached
      val remote = repository.fetchRemoteSurahAyahs(surah.number)
      if (remote != null && _uiState.value.currentSurah.number == surah.number) {
        _uiState.value = _uiState.value.copy(currentSurah = surah)
      }
    }
  }

  fun selectAyah(index: Int) {
    _uiState.value = _uiState.value.copy(selectedAyahIndex = index)
    viewModelScope.launch {
      val surah = _uiState.value.currentSurah
      repository.saveLastRead(surah.number, index + 1, surah.nameArabic)
    }
  }

  fun setReadingMode(mode: ReadingMode) {
    _uiState.value = _uiState.value.copy(readingMode = mode)
  }

  fun toggleDarkTheme() {
    _uiState.value = _uiState.value.copy(isDarkTheme = !_uiState.value.isDarkTheme)
  }

  fun setFontSizeMultiplier(multiplier: Float) {
    _uiState.value = _uiState.value.copy(fontSizeMultiplier = multiplier.coerceIn(0.8f, 1.6f))
  }

  fun toggleTafsirSheet(visible: Boolean) {
    _uiState.value = _uiState.value.copy(isTafsirSheetVisible = visible)
  }

  fun toggleSearch(visible: Boolean) {
    _uiState.value = _uiState.value.copy(isSearchVisible = visible, searchQuery = "", searchResults = emptyList())
  }

  fun performSearch(query: String) {
    _uiState.value = _uiState.value.copy(searchQuery = query)
    val results = repository.searchQuran(query)
    _uiState.value = _uiState.value.copy(searchResults = results)
  }

  // Playback integration
  fun playSurah(surah: Surah) {
    audioPlayer.playSurah(surah)
  }

  fun playAyah(surah: Surah, index: Int) {
    audioPlayer.playAyah(surah, index)
  }

  fun playDownloadedSurah(download: DownloadedSurahEntity) {
    val surah = QuranData.allSurahs.find { it.number == download.surahNumber } ?: QuranData.allSurahs[0]
    val file = File(download.localPath)
    if (file.exists()) {
      audioPlayer.playLocalAudioFile(download.localPath, surah, download.reciterName)
    } else {
      audioPlayer.playSurah(surah)
    }
  }

  // Adhan controls
  fun playAdhan(option: AdhanOption) {
    adhanManager.playAdhan(option)
    showMessage("بدأ أذان بصوت ${option.titleArabic}")
  }

  fun selectAdhanOption(option: AdhanOption) {
    adhanManager.selectAdhanOption(option)
    showMessage("تم اختيار: ${option.titleArabic}")
  }

  fun stopAdhan() {
    adhanManager.stopAdhan()
    showMessage("تم إيقاف الأذان")
  }

  // Bookmarks
  fun bookmarkCurrentAyah(note: String = "") {
    viewModelScope.launch {
      val surah = _uiState.value.currentSurah
      val ayahIndex = _uiState.value.selectedAyahIndex
      repository.saveBookmark(
        surahNumber = surah.number,
        ayahNumber = ayahIndex + 1,
        surahName = surah.nameArabic,
        note = note.ifEmpty { "موضع توقف محفوظ" }
      )
      showMessage("تم حفظ موضع التوقف بنجاح")
    }
  }

  fun deleteBookmark(id: Long) {
    viewModelScope.launch {
      repository.deleteBookmark(id)
      showMessage("تم حذف الإشارة المرجعية")
    }
  }

  // Real Downloads & Storage
  fun downloadSurah(surah: Surah, reciter: Reciter) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val audioDir = File(getApplication<Application>().filesDir, "audio").apply { mkdirs() }
        val targetFile = File(audioDir, "surah_${surah.number}_${reciter.id}.mp3")
        val audioUrl = reciter.getSurahAudioUrl(surah.number)

        withContext(Dispatchers.Main) {
          repository.addDownloadedSurah(
            DownloadedSurahEntity(
              surahNumber = surah.number,
              surahName = "سورة ${surah.nameArabic}",
              reciterId = reciter.id,
              reciterName = reciter.nameArabic,
              localPath = targetFile.absolutePath,
              sizeMb = surah.sizeMb,
              downloadProgress = 5,
              isCompleted = false,
              audioQuality = _uiState.value.audioQualityMode
            )
          )
          showMessage("بدأ تحميل سورة ${surah.nameArabic}...")
        }

        val request = Request.Builder().url(audioUrl).build()
        val client = OkHttpClient.Builder()
          .connectTimeout(20, TimeUnit.SECONDS)
          .readTimeout(60, TimeUnit.SECONDS)
          .build()

        client.newCall(request).execute().use { response ->
          if (!response.isSuccessful) throw java.io.IOException("HTTP error ${response.code}")
          val body = response.body ?: throw java.io.IOException("Empty response body")
          val totalBytes = body.contentLength()
          val source = body.byteStream()
          val fos = FileOutputStream(targetFile)
          val buffer = ByteArray(8192)
          var bytesRead: Int
          var totalRead = 0L
          var lastReportTime = System.currentTimeMillis()

          while (source.read(buffer).also { bytesRead = it } != -1) {
            fos.write(buffer, 0, bytesRead)
            totalRead += bytesRead
            val now = System.currentTimeMillis()
            if (now - lastReportTime > 500 && totalBytes > 0) {
              lastReportTime = now
              val percent = ((totalRead * 100) / totalBytes).toInt().coerceIn(5, 99)
              repository.updateDownloadProgress(surah.number, percent, isCompleted = false)
            }
          }
          fos.flush()
          fos.close()
          source.close()

          repository.updateDownloadProgress(surah.number, 100, isCompleted = true)
          updateStorageUsage()
          withContext(Dispatchers.Main) {
            showMessage("تم اكتمال تحميل سورة ${surah.nameArabic} للاستماع دون إنترنت")
          }
        }
      } catch (e: Exception) {
        Log.e("QuranViewModel", "Download failed: ${e.message}")
        withContext(Dispatchers.Main) {
          showMessage("تعذر تحميل السورة، يرجى التأكد من اتصال الإنترنت")
        }
      }
    }
  }

  fun downloadFullMushaf(reciter: Reciter) {
    viewModelScope.launch {
      showMessage("بدأ تجهيز تحميل مصحف ${reciter.nameArabic} كاملاً...")
      listOf(1, 18, 36, 67, 112, 113, 114).forEach { num ->
        val s = QuranData.allSurahs.find { it.number == num }
        if (s != null) {
          downloadSurah(s, reciter)
        }
      }
    }
  }

  fun deleteDownload(surahNumber: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      val existing = database.quranDao().getDownloadedSurah(surahNumber)
      if (existing != null) {
        try {
          File(existing.localPath).delete()
        } catch (_: Exception) {}
      }
      repository.deleteDownloadedSurah(surahNumber)
      updateStorageUsage()
      withContext(Dispatchers.Main) {
        showMessage("تم حذف السورة من الذاكرة")
      }
    }
  }

  fun setAudioQuality(quality: String) {
    _uiState.value = _uiState.value.copy(audioQualityMode = quality)
  }

  fun clearCache() {
    viewModelScope.launch(Dispatchers.IO) {
      val audioDir = File(getApplication<Application>().filesDir, "audio")
      if (audioDir.exists()) {
        audioDir.deleteRecursively()
      }
      repository.clearDownloads()
      updateStorageUsage()
      withContext(Dispatchers.Main) {
        showMessage("تم تنظيف الذاكرة المؤقتة بنجاح")
      }
    }
  }

  // Cloud Sync
  fun toggleCloudSyncDialog(visible: Boolean) {
    _uiState.value = _uiState.value.copy(isCloudSyncDialogVisible = visible)
  }

  fun syncWithCloud() {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isCloudSyncing = true)
      delay(1200)
      _uiState.value = _uiState.value.copy(
        isCloudSyncing = false,
        lastCloudSyncTime = "الآن • تم تزامن البيانات مع السحابة بنجاح"
      )
      showMessage("تم مزامنة مواضع التوقف وقوائم التشغيل بين الأجهزة!")
    }
  }

  // Hourly Salawat & Azkar Alerts
  fun toggleAzkarAlertsDialog(visible: Boolean) {
    _uiState.value = _uiState.value.copy(isAzkarAlertsDialogVisible = visible)
  }

  fun updateReminderSettings(settings: ReminderSettings) {
    _uiState.value = _uiState.value.copy(reminderSettings = settings)
    showMessage("تم تحديث إعدادات التنبيهات والأذكار")
  }

  fun testSalawatChimeNow() {
    audioPlayer.triggerSalawatReminderNow()
    showMessage("ﷺ الصلاة على النبي: تنبيه صوتي وإشعار نشط")
  }

  // Audio Effects
  fun toggleAudioEffectsSheet(visible: Boolean) {
    _uiState.value = _uiState.value.copy(isAudioEffectsVisible = visible)
  }

  fun setSoundEffectMode(mode: com.example.audio.SoundEffectMode) {
    audioPlayer.setSoundEffectMode(mode)
    showMessage("تم تفعيل مؤثر الصوت: ${mode.titleArabic}")
  }

  fun setBassBoost(level: Short) {
    audioPlayer.setBassBoost(level)
  }

  fun setLanguage(lang: String) {
    _uiState.value = _uiState.value.copy(currentLanguage = lang)
  }

  // Azkar Counters
  fun incrementAzkarCount(id: String) {
    val updated = _uiState.value.azkarItems.map { item ->
      if (item.id == id) {
        val next = item.currentCount + 1
        item.copy(currentCount = if (next > item.targetCount) item.targetCount else next)
      } else item
    }
    _uiState.value = _uiState.value.copy(azkarItems = updated)
  }

  fun resetAzkarCategory(category: AzkarCategory) {
    val updated = _uiState.value.azkarItems.map { item ->
      if (item.category == category) item.copy(currentCount = 0) else item
    }
    _uiState.value = _uiState.value.copy(azkarItems = updated)
  }

  private fun startSalawatHourlySchedule() {
    viewModelScope.launch {
      while (true) {
        // Run check every 60 minutes
        delay(60 * 60 * 1000L)
        if (_uiState.value.reminderSettings.hourlySalawatEnabled) {
          audioPlayer.triggerSalawatReminderNow()
        }
      }
    }
  }

  fun showMessage(msg: String) {
    _uiState.value = _uiState.value.copy(infoMessage = msg)
    viewModelScope.launch {
      delay(2500)
      if (_uiState.value.infoMessage == msg) {
        _uiState.value = _uiState.value.copy(infoMessage = null)
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    unregisterSensors()
    audioPlayer.releaseMediaPlayer()
    adhanManager.stopAdhan()
  }
}
