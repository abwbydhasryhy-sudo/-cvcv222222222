package com.example.audio

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdhanOption(
    val id: String,
    val titleArabic: String,
    val muadhin: String,
    val audioUrl: String = "",
    val assetFileName: String? = null,
    val isOffline: Boolean = false,
    val sourceUrl: String = "",
    val licenseNotice: String = ""
)

data class AdhanState(
    val isPlaying: Boolean = false,
    val currentAdhanTitle: String = "",
    val activeOption: AdhanOption = defaultAdhanOptions[0]
)

val defaultAdhanOptions = listOf(
    AdhanOption(
        id = "islam_sobhi",
        titleArabic = "أذان بصوت إسلام صبحي",
        muadhin = "القارئ إسلام صبحي",
        audioUrl = "https://openadhan.com/adhans/islam-sobhi-adhan.mp3",
        assetFileName = "adhan_islam_sobhi.mp3",
        isOffline = true,
        sourceUrl = "https://youtu.be/hKPcNh7WHoM",
        licenseNotice = "مرخّص ومتاح للاستخدام الحر المفتوح (MIT License / Open Adhan)"
    ),
    AdhanOption(
        id = "makkah",
        titleArabic = "أذان الحرم المكي الشريف",
        muadhin = "مؤذنو المسجد الحرام",
        audioUrl = "https://archive.org/download/AzanMakkah_201708/Azan%20Makkah.mp3",
        assetFileName = null,
        isOffline = false
    ),
    AdhanOption(
        id = "madinah",
        titleArabic = "أذان المسجد النبوي الشريف",
        muadhin = "مؤذنو المسجد النبوي",
        audioUrl = "https://archive.org/download/AzanMadinah_201708/Azan%20Madinah.mp3",
        assetFileName = null,
        isOffline = false
    ),
    AdhanOption(
        id = "alafasy",
        titleArabic = "أذان بصوت مشاري العفاسي",
        muadhin = "الشيخ مشاري راشد العفاسي",
        audioUrl = "https://server8.mp3quran.net/afs/adhan.mp3",
        assetFileName = null,
        isOffline = false
    ),
    AdhanOption(
        id = "abdulbasit",
        titleArabic = "أذان بصوت عبد الباسط عبد الصمد",
        muadhin = "الشيخ عبد الباسط عبد الصمد",
        audioUrl = "https://archive.org/download/Adhan_Abdulbasit/Adhan_Abdulbasit.mp3",
        assetFileName = null,
        isOffline = false
    )
)

class AdhanManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var mediaPlayer: MediaPlayer? = null

    private val _state = MutableStateFlow(AdhanState())
    val state: StateFlow<AdhanState> = _state.asStateFlow()

    init {
        createPrayerNotificationChannel()
    }

    private fun createPrayerNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_PRAYER,
                "مواقيت الصلاة والأذان",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات دخول وقت الصلاة والأذان المسموع"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun playAdhan(option: AdhanOption = _state.value.activeOption) {
        stopAdhan()
        _state.value = _state.value.copy(
            isPlaying = true,
            currentAdhanTitle = option.titleArabic,
            activeOption = option
        )

        scope.launch {
            try {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    if (!option.assetFileName.isNullOrBlank()) {
                        val afd = context.assets.openFd(option.assetFileName)
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        afd.close()
                    } else {
                        setDataSource(option.audioUrl)
                    }
                    setOnPreparedListener { mp ->
                        mp.start()
                    }
                    setOnCompletionListener {
                        _state.value = _state.value.copy(isPlaying = false)
                        release()
                        mediaPlayer = null
                    }
                    setOnErrorListener { _, what, extra ->
                        Log.e("AdhanManager", "Error playing Adhan: what=$what extra=$extra")
                        _state.value = _state.value.copy(isPlaying = false)
                        true
                    }
                    prepareAsync()
                }
            } catch (e: Exception) {
                Log.e("AdhanManager", "Failed to start Adhan: ${e.message}")
                _state.value = _state.value.copy(isPlaying = false)
            }
        }
    }

    fun selectAdhanOption(option: AdhanOption) {
        _state.value = _state.value.copy(
            activeOption = option,
            currentAdhanTitle = option.titleArabic
        )
    }

    fun stopAdhan() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        _state.value = _state.value.copy(isPlaying = false)
    }

    fun togglePlayAdhan(option: AdhanOption = _state.value.activeOption) {
        if (_state.value.isPlaying) {
            stopAdhan()
        } else {
            playAdhan(option)
        }
    }

    fun triggerPrayerNotification(prayerName: String, prayerTime: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_PRAYER)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("حان الآن موعد صلاة $prayerName")
            .setContentText("الله أكبر، الله أكبر • وقت صلاة $prayerName: $prayerTime")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "حي على الصلاة، حي على الفلاح • حان الآن وقت صلاة $prayerName حسب توقيت موقعك الحالي ($prayerTime). تقبل الله طاعتكم."
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_PRAYER, notification)
    }

    fun release() {
        stopAdhan()
    }

    companion object {
        const val CHANNEL_PRAYER = "prayer_times_adhan_channel"
        const val NOTIFICATION_ID_PRAYER = 201
    }
}
