package com.example.ui

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.data.repository.LocationHelper
import com.example.data.repository.QuranRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
  val location: Location? = null
)

class QuranViewModel(application: Application) : AndroidViewModel(application) {

  private val database = QuranDatabase.getDatabase(application)
  private val repository = QuranRepository(database.quranDao())
  val audioPlayer = QuranAudioPlayer(application)
  private val locationHelper = LocationHelper(application)
  private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager

  private val _uiState = MutableStateFlow(UiState())
  val uiState: StateFlow<UiState> = _uiState.asStateFlow()

  private val _qiblaState = MutableStateFlow(QiblaState())
  val qiblaState: StateFlow<QiblaState> = _qiblaState.asStateFlow()

  private val sensorListener = object : SensorEventListener {
    override fun onSensorChanged(event: SensorEvent) {
      if (event.sensor.type == Sensor.TYPE_ORIENTATION) {
        _qiblaState.value = _qiblaState.value.copy(compassHeading = event.values[0])
      }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
  }

  init {
    seedInitialDownloads()
    startSalawatHourlySchedule()
    observeLocation()
    registerCompass()
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
        }
      }
    }
  }

  private fun registerCompass() {
    @Suppress("DEPRECATION")
    val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
    sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_UI)
  }

  private fun calculateQibla(lat: Double, lon: Double): Double {
    val kaabaLat = Math.toRadians(21.4225)
    val kaabaLon = Math.toRadians(39.8262)
    val myLat = Math.toRadians(lat)
    val myLon = Math.toRadians(lon)

    val y = sin(kaabaLon - myLon)
    val x = cos(myLat) * tan(kaabaLat) - sin(myLat) * cos(kaabaLon - myLon)
    val qiblaRad = atan2(y, x)
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

    val a = sin(dLat / 2).pow(2) + cos(myLat) * cos(kaabaLat) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
  }

  override fun onCleared() {
    super.onCleared()
    sensorManager.unregisterListener(sensorListener)
    audioPlayer.releaseMediaPlayer()
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

  private var downloadSimulationJob: Job? = null

  private fun seedInitialDownloads() {
    viewModelScope.launch {
      // Seed pre-loaded downloaded Surahs matching screenshot 1
      repository.addDownloadedSurah(
        DownloadedSurahEntity(
          surahNumber = 18,
          surahName = "سورة الكهف",
          reciterId = "alafasy",
          reciterName = "الشيخ مشاري راشد العفاسي",
          localPath = "/offline/kahf.mp3",
          sizeMb = 42.0,
          downloadProgress = 100,
          isCompleted = true,
          audioQuality = "320kbps"
        )
      )
      repository.addDownloadedSurah(
        DownloadedSurahEntity(
          surahNumber = 19,
          surahName = "سورة مريم",
          reciterId = "abdulbasit",
          reciterName = "الشيخ عبد الباسط عبد الصمد",
          localPath = "/offline/maryam.mp3",
          sizeMb = 27.0,
          downloadProgress = 68,
          isCompleted = false,
          audioQuality = "320kbps"
        )
      )
      repository.addDownloadedSurah(
        DownloadedSurahEntity(
          surahNumber = 20,
          surahName = "سورة طه",
          reciterId = "muaiqly",
          reciterName = "الشيخ ماهر المعيقلي",
          localPath = "/offline/taha.mp3",
          sizeMb = 32.0,
          downloadProgress = 100,
          isCompleted = true,
          audioQuality = "320kbps"
        )
      )

      // Initial Last Read
      repository.saveLastRead(17, 1, "سورة الإسراء")
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

  // Downloads & Storage
  fun downloadSurah(surah: Surah, reciter: Reciter) {
    viewModelScope.launch {
      repository.addDownloadedSurah(
        DownloadedSurahEntity(
          surahNumber = surah.number,
          surahName = "سورة ${surah.nameArabic}",
          reciterId = reciter.id,
          reciterName = reciter.nameArabic,
          localPath = "/offline/${surah.number}.mp3",
          sizeMb = surah.sizeMb,
          downloadProgress = 10,
          isCompleted = false,
          audioQuality = _uiState.value.audioQualityMode
        )
      )
      showMessage("بدأ تحميل ${surah.nameArabic}...")
      simulateProgress(surah.number)
    }
  }

  fun downloadFullMushaf(reciter: Reciter) {
    viewModelScope.launch {
      showMessage("بدأت حزمة تحميل مصحف ${reciter.nameArabic} كاملاً...")
      // Simulate download
      for (i in 1..5) {
        val surah = QuranData.allSurahs[i - 1]
        repository.addDownloadedSurah(
          DownloadedSurahEntity(
            surahNumber = surah.number,
            surahName = "سورة ${surah.nameArabic}",
            reciterId = reciter.id,
            reciterName = reciter.nameArabic,
            localPath = "/offline/${surah.number}.mp3",
            sizeMb = surah.sizeMb,
            downloadProgress = 100,
            isCompleted = true,
            audioQuality = reciter.quality
          )
        )
      }
      _uiState.value = _uiState.value.copy(usedStorageGb = _uiState.value.usedStorageGb + 0.5)
      showMessage("تم تحميل السور بنجاح للاستماع دون إنترنت")
    }
  }

  private fun simulateProgress(surahNumber: Int) {
    downloadSimulationJob?.cancel()
    downloadSimulationJob = viewModelScope.launch {
      for (p in listOf(25, 48, 68, 85, 100)) {
        delay(800)
        repository.updateDownloadProgress(surahNumber, p, isCompleted = (p == 100))
      }
      _uiState.value = _uiState.value.copy(usedStorageGb = _uiState.value.usedStorageGb + 0.04)
    }
  }

  fun deleteDownload(surahNumber: Int) {
    viewModelScope.launch {
      repository.deleteDownloadedSurah(surahNumber)
      _uiState.value = _uiState.value.copy(usedStorageGb = (_uiState.value.usedStorageGb - 0.04).coerceAtLeast(0.5))
      showMessage("تم حذف السورة من الذاكرة")
    }
  }

  fun setAudioQuality(quality: String) {
    _uiState.value = _uiState.value.copy(audioQualityMode = quality)
  }

  fun clearCache() {
    viewModelScope.launch {
      repository.clearDownloads()
      _uiState.value = _uiState.value.copy(usedStorageGb = 0.4)
      showMessage("تم تنظيف الذاكرة المؤقتة بنجاح")
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
}
