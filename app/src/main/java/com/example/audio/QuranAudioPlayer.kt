package com.example.audio

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes as AndroidAudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.video.VideoRendererEventListener
import com.example.data.model.Ayah
import com.example.data.model.QuranData
import com.example.data.model.Reciter
import com.example.data.model.Surah
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlaybackState(
  val isPlaying: Boolean = false,
  val isLoading: Boolean = false,
  val currentSurah: Surah = QuranData.allSurahs[16], // Default Al-Isra (Surah 17)
  val currentAyahIndex: Int = 0,
  val currentReciter: Reciter = QuranData.reciters[1], // Sheikh Mishary Al-Afasy
  val currentPositionSeconds: Int = 0,
  val totalDurationSeconds: Int = 45 * 60,
  val playbackSpeed: Float = 1.0f,
  val isLooping: Boolean = false,
  val soundEffectMode: SoundEffectMode = SoundEffectMode.PURE_STUDIO,
  val bassBoostLevel: Short = 0,
  // Zen Timer States
  val isTimerActive: Boolean = true,
  val timerRemainingSeconds: Int = 28 * 60 + 35, // 28:35
  val timerPreset: String = "30", // "15", "30", "45", "end"
  val smartStopOnSurahEnd: Boolean = true,
  val fadeOutEnabled: Boolean = true,
  val darkSukunScreenEnabled: Boolean = true
)

@OptIn(UnstableApi::class)
class QuranAudioPlayer(private val context: Context) {

  private val _state = MutableStateFlow(PlaybackState())
  val state: StateFlow<PlaybackState> = _state.asStateFlow()

  private val scope = CoroutineScope(Dispatchers.Main + Job())
  private var tickerJob: Job? = null
  private var timerJob: Job? = null
  private var loadJob: Job? = null

  private var exoPlayer: ExoPlayer? = null
  private var salawatPlayer: MediaPlayer? = null
  private var currentLoadedUrl: String? = null

  val soundEffectsHelper = SoundEffectsHelper()

  init {
    createNotificationChannels()
    startTimerTicker()
    startPlaybackTicker()
  }

  private fun getOrCreateExoPlayer(): ExoPlayer {
    exoPlayer?.let { return it }

    val audioAttributes = AudioAttributes.Builder()
      .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
      .setUsage(C.USAGE_MEDIA)
      .build()

    val renderersFactory = object : DefaultRenderersFactory(context) {
      override fun buildVideoRenderers(
        context: Context,
        extensionRendererMode: Int,
        mediaCodecSelector: MediaCodecSelector,
        enableDecoderFallback: Boolean,
        eventHandler: Handler,
        eventListener: VideoRendererEventListener,
        allowedVideoJoiningTimeMs: Long,
        out: ArrayList<Renderer>
      ) {
        // Pure Audio Application: Omit video renderers to avoid querying hardware video decoders and graphics system resources
      }
    }.apply {
      setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
      setEnableDecoderFallback(true)
    }

    val player = ExoPlayer.Builder(context, renderersFactory)
      .setAudioAttributes(audioAttributes, /* handleAudioFocus = */ true)
      .build()
      .apply {
        repeatMode = Player.REPEAT_MODE_OFF

        addListener(object : Player.Listener {
          override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
              Player.STATE_BUFFERING -> {
                _state.value = _state.value.copy(isLoading = true)
              }
              Player.STATE_READY -> {
                val durSec = (duration / 1000).toInt().coerceAtLeast(1)
                _state.value = _state.value.copy(
                  isLoading = false,
                  totalDurationSeconds = if (durSec > 1) durSec else _state.value.totalDurationSeconds
                )
                // Attach audio effects (Reverb, Equalizer, Bass Boost)
                try {
                  soundEffectsHelper.attachToSession(audioSessionId)
                  soundEffectsHelper.applyEffectMode(_state.value.soundEffectMode)
                  if (_state.value.bassBoostLevel > 0) {
                    soundEffectsHelper.setBassBoost(_state.value.bassBoostLevel)
                  }
                } catch (e: Exception) {
                  Log.w("QuranAudioPlayer", "Failed attaching audio effects to ExoPlayer: ${e.message}")
                }
              }
              Player.STATE_ENDED -> {
                _state.value = _state.value.copy(isLoading = false)
                onPlaybackCompleted()
              }
              Player.STATE_IDLE -> {
                _state.value = _state.value.copy(isLoading = false)
              }
            }
          }

          override fun onIsPlayingChanged(isPlaying: Boolean) {
            _state.value = _state.value.copy(isPlaying = isPlaying)
          }

          override fun onPlayerError(error: PlaybackException) {
            Log.w("QuranAudioPlayer", "ExoPlayer error: ${error.errorCodeName} - ${error.message}")
            _state.value = _state.value.copy(isLoading = false, isPlaying = false)
          }
        })
      }

    exoPlayer = player
    return player
  }

  private fun createNotificationChannels() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

      val salawatChannel = NotificationChannel(
        CHANNEL_SALAWAT,
        "تذكير الصلاة على النبي ﷺ",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "تنبيه صوتي ومرئي دوري للصلاة والسلام على رسول الله ﷺ"
        enableVibration(true)
      }

      val azkarChannel = NotificationChannel(
        CHANNEL_AZKAR,
        "أذكار المسلم اليومية",
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "تنبيهات أذكار الصباح والمساء والنوم"
      }

      notificationManager.createNotificationChannel(salawatChannel)
      notificationManager.createNotificationChannel(azkarChannel)
    }
  }

  fun togglePlayPause() {
    if (_state.value.isPlaying) {
      pausePlayback()
    } else {
      resumePlayback()
    }
  }

  private fun pausePlayback() {
    try {
      exoPlayer?.pause()
    } catch (e: Exception) {
      Log.w("QuranAudioPlayer", "Error pausing ExoPlayer: ${e.message}")
    }
    _state.value = _state.value.copy(isPlaying = false)
  }

  private fun resumePlayback() {
    val player = exoPlayer
    if (player != null && player.playbackState != Player.STATE_IDLE) {
      try {
        player.play()
        _state.value = _state.value.copy(isPlaying = true)
      } catch (e: Exception) {
        Log.w("QuranAudioPlayer", "Error resuming ExoPlayer: ${e.message}")
        loadAndStreamSurah(_state.value.currentSurah, _state.value.currentReciter)
      }
    } else {
      loadAndStreamSurah(_state.value.currentSurah, _state.value.currentReciter)
    }
  }

  fun playSurah(surah: Surah, reciter: Reciter? = null) {
    val targetReciter = reciter ?: _state.value.currentReciter
    _state.value = _state.value.copy(
      currentSurah = surah,
      currentAyahIndex = 0,
      currentReciter = targetReciter,
      isLoading = true,
      currentPositionSeconds = 0,
      totalDurationSeconds = surah.durationMinutes * 60
    )
    loadAndStreamSurah(surah, targetReciter)
  }

  fun playAyah(surah: Surah, ayahIndex: Int) {
    _state.value = _state.value.copy(
      currentSurah = surah,
      currentAyahIndex = ayahIndex
    )
    if (exoPlayer == null || _state.value.currentSurah.number != surah.number) {
      loadAndStreamSurah(surah, _state.value.currentReciter)
    } else {
      if (!_state.value.isPlaying) {
        resumePlayback()
      }
    }
  }

  private fun loadAndStreamSurah(surah: Surah, reciter: Reciter) {
    loadJob?.cancel()
    _state.value = _state.value.copy(
      isLoading = true,
      isPlaying = false,
      currentSurah = surah,
      currentReciter = reciter,
      currentPositionSeconds = 0,
      totalDurationSeconds = surah.durationMinutes * 60
    )

    loadJob = scope.launch(Dispatchers.Main) {
      try {
        val audioUrl = reciter.getSurahAudioUrl(surah.number)
        currentLoadedUrl = audioUrl
        val player = getOrCreateExoPlayer()

        val mediaMetadata = MediaMetadata.Builder()
          .setTitle("سورة ${surah.nameArabic}")
          .setArtist(reciter.nameArabic)
          .setDisplayTitle("سورة ${surah.nameArabic}")
          .build()

        val mediaItem = MediaItem.Builder()
          .setUri(audioUrl)
          .setMediaMetadata(mediaMetadata)
          .build()

        player.setMediaItem(mediaItem)
        player.playbackParameters = PlaybackParameters(_state.value.playbackSpeed)
        player.prepare()
        player.play()
      } catch (e: Exception) {
        Log.w("QuranAudioPlayer", "Error loading ExoPlayer stream: ${e.message}")
        _state.value = _state.value.copy(isLoading = false, isPlaying = false)
      }
    }
  }

  private fun onPlaybackCompleted() {
    if (_state.value.isLooping) {
      seekTo(0)
      resumePlayback()
      return
    }

    if (_state.value.smartStopOnSurahEnd && _state.value.isTimerActive) {
      _state.value = _state.value.copy(isPlaying = false, isTimerActive = false)
      pausePlayback()
      return
    }

    // Continuous recitation: proceed to the next Surah automatically
    val nextNumber = if (_state.value.currentSurah.number < 114) _state.value.currentSurah.number + 1 else 1
    val nextSurah = QuranData.allSurahs.find { it.number == nextNumber } ?: QuranData.allSurahs[0]
    playSurah(nextSurah)
  }

  fun skipNextAyah() {
    val ayahs = QuranData.getAyahsForSurah(_state.value.currentSurah.number)
    if (_state.value.currentAyahIndex < ayahs.size - 1) {
      _state.value = _state.value.copy(
        currentAyahIndex = _state.value.currentAyahIndex + 1
      )
    } else {
      // Continuous next surah
      val nextNumber = if (_state.value.currentSurah.number < 114) _state.value.currentSurah.number + 1 else 1
      val nextSurah = QuranData.allSurahs.find { it.number == nextNumber } ?: QuranData.allSurahs[0]
      playSurah(nextSurah)
    }
  }

  fun skipPreviousAyah() {
    if (_state.value.currentAyahIndex > 0) {
      _state.value = _state.value.copy(
        currentAyahIndex = _state.value.currentAyahIndex - 1
      )
    } else {
      val prevNumber = if (_state.value.currentSurah.number > 1) _state.value.currentSurah.number - 1 else 114
      val prevSurah = QuranData.allSurahs.find { it.number == prevNumber } ?: QuranData.allSurahs[0]
      playSurah(prevSurah)
    }
  }

  fun cyclePlaybackSpeed() {
    val speeds = listOf(0.75f, 1.0f, 1.25f, 1.5f)
    val currentIndex = speeds.indexOf(_state.value.playbackSpeed)
    val nextSpeed = speeds[(currentIndex + 1) % speeds.size]
    _state.value = _state.value.copy(playbackSpeed = nextSpeed)
    try {
      exoPlayer?.playbackParameters = PlaybackParameters(nextSpeed)
    } catch (e: Exception) {
      Log.w("QuranAudioPlayer", "Error applying speed to ExoPlayer: ${e.message}")
    }
  }

  fun seekTo(seconds: Int) {
    val target = seconds.coerceIn(0, _state.value.totalDurationSeconds)
    _state.value = _state.value.copy(currentPositionSeconds = target)
    try {
      exoPlayer?.seekTo(target * 1000L)
    } catch (e: Exception) {
      Log.w("QuranAudioPlayer", "Error seeking ExoPlayer: ${e.message}")
    }
  }

  fun setReciter(reciter: Reciter) {
    _state.value = _state.value.copy(currentReciter = reciter)
    if (_state.value.isPlaying) {
      playSurah(_state.value.currentSurah, reciter)
    }
  }

  // Audio Effects & Sound Enhancement
  fun setSoundEffectMode(mode: SoundEffectMode) {
    _state.value = _state.value.copy(soundEffectMode = mode)
    soundEffectsHelper.applyEffectMode(mode)
  }

  fun setBassBoost(level: Short) {
    _state.value = _state.value.copy(bassBoostLevel = level)
    soundEffectsHelper.setBassBoost(level)
  }

  // Zen Timer controls
  fun toggleZenTimer() {
    _state.value = _state.value.copy(isTimerActive = !_state.value.isTimerActive)
  }

  fun addFiveMinutesToTimer() {
    _state.value = _state.value.copy(
      timerRemainingSeconds = _state.value.timerRemainingSeconds + 300,
      isTimerActive = true
    )
  }

  fun setTimerPreset(preset: String) {
    val seconds = when (preset) {
      "15" -> 15 * 60
      "30" -> 30 * 60
      "45" -> 45 * 60
      "end" -> (_state.value.totalDurationSeconds - _state.value.currentPositionSeconds).coerceAtLeast(180)
      else -> 30 * 60
    }
    _state.value = _state.value.copy(
      timerPreset = preset,
      timerRemainingSeconds = seconds,
      isTimerActive = true
    )
  }

  fun toggleSmartStop(enabled: Boolean) {
    _state.value = _state.value.copy(smartStopOnSurahEnd = enabled)
  }

  fun toggleFadeOut(enabled: Boolean) {
    _state.value = _state.value.copy(fadeOutEnabled = enabled)
  }

  fun toggleDarkSukunScreen(enabled: Boolean) {
    _state.value = _state.value.copy(darkSukunScreenEnabled = enabled)
  }

  // Salawat Reminder Notification & Melodic Chime
  fun triggerSalawatReminderNow() {
    scope.launch {
      // Play harmonic spiritual chime
      soundEffectsHelper.playSpiritualSalawatChime()
      // Play real voice Salawat
      playRealVoiceSalawat()
    }

    // Show Android System Notification
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notification = NotificationCompat.Builder(context, CHANNEL_SALAWAT)
      .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
      .setContentTitle("ﷺ تذكير الصلاة على النبي")
      .setContentText("اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ وَعَلَى آلِهِ وَصَحْبِهِ أَجْمَعِينَ")
      .setStyle(
        NotificationCompat.BigTextStyle().bigText(
          "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ، كَمَا صَلَّيْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ، إِنَّكَ حَمِيدٌ مَجِيدٌ. وَبَارِكْ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ، كَمَا بَارَكْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ، فِي الْعَالَمِينَ إِنَّكَ حَمِيدٌ مَجِيدٌ."
        )
      )
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .build()

    notificationManager.notify(101, notification)
  }

  private fun playRealVoiceSalawat() {
    val salawatUrls = listOf(
      "https://archive.org/download/salawat-reminders/salawat_alafasy.mp3",
      "https://archive.org/download/salawat-reminders/salawat_sudais.mp3",
      "https://archive.org/download/salawat-reminders/salawat_dosari.mp3"
    )
    val randomUrl = salawatUrls.random()

    try {
      if (salawatPlayer == null) {
        salawatPlayer = MediaPlayer().apply {
          setAudioAttributes(
            AndroidAudioAttributes.Builder()
              .setContentType(AndroidAudioAttributes.CONTENT_TYPE_SPEECH)
              .setUsage(AndroidAudioAttributes.USAGE_NOTIFICATION)
              .build()
          )
        }
      } else {
        salawatPlayer?.reset()
      }

      salawatPlayer?.apply {
        setOnPreparedListener { it.start() }
        setOnCompletionListener { /* Keep instance for reuse */ }
        setOnErrorListener { mp, what, extra ->
          Log.e("QuranAudioPlayer", "Salawat MediaPlayer error: what=$what, extra=$extra. Falling back to chime.")
          mp.reset()
          scope.launch { soundEffectsHelper.playSpiritualSalawatChime() }
          true
        }
        setDataSource(randomUrl)
        prepareAsync()
      }
    } catch (e: Exception) {
      Log.e("QuranAudioPlayer", "Failed to play real voice Salawat: ${e.message}")
      salawatPlayer?.release()
      salawatPlayer = null
      scope.launch { soundEffectsHelper.playSpiritualSalawatChime() }
    }
  }

  fun triggerAzkarNotification(title: String, body: String) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notification = NotificationCompat.Builder(context, CHANNEL_AZKAR)
      .setSmallIcon(android.R.drawable.ic_menu_agenda)
      .setContentTitle(title)
      .setContentText(body)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setAutoCancel(true)
      .build()

    notificationManager.notify(102, notification)
  }

  private fun startPlaybackTicker() {
    tickerJob?.cancel()
    tickerJob = scope.launch {
      while (true) {
        delay(1000)
        if (_state.value.isPlaying) {
          try {
            val player = exoPlayer
            if (player != null && player.playbackState == Player.STATE_READY) {
              val current = (player.currentPosition / 1000).toInt()
              val duration = (player.duration / 1000).toInt().coerceAtLeast(1)
              _state.value = _state.value.copy(
                currentPositionSeconds = current,
                totalDurationSeconds = duration
              )
            }
          } catch (_: Exception) {}
        }
      }
    }
  }

  private fun startTimerTicker() {
    timerJob?.cancel()
    timerJob = scope.launch {
      while (true) {
        delay(1000)
        if (_state.value.isTimerActive && _state.value.timerRemainingSeconds > 0) {
          val remaining = _state.value.timerRemainingSeconds - 1
          _state.value = _state.value.copy(timerRemainingSeconds = remaining)

          // Fade out volume in the last 2 minutes if enabled
          if (_state.value.fadeOutEnabled && remaining in 1..120) {
            val vol = (remaining.toFloat() / 120f).coerceIn(0.1f, 1.0f)
            try {
              exoPlayer?.volume = vol
            } catch (_: Exception) {}
          }

          if (remaining == 0) {
            pausePlayback()
            _state.value = _state.value.copy(isTimerActive = false)
          }
        }
      }
    }
  }

  fun releaseMediaPlayer() {
    try {
      exoPlayer?.stop()
      exoPlayer?.release()
    } catch (_: Exception) {}
    exoPlayer = null
    try {
      salawatPlayer?.release()
    } catch (_: Exception) {}
    salawatPlayer = null
    soundEffectsHelper.releaseEffects()
  }

  companion object {
    const val CHANNEL_SALAWAT = "salawat_hourly_channel"
    const val CHANNEL_AZKAR = "azkar_daily_channel"
  }
}
