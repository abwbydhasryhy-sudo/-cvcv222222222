package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.audiofx.AudioEffect
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.PresetReverb
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin

enum class SoundEffectMode(val titleArabic: String, val descriptionArabic: String) {
  PURE_STUDIO("استوديو نقي ومتوازن", "صوت خام بأعلى دقة ونقاوة دون أي معالجة إضافية"),
  MIHRAB_ECHO("صدى المحراب والمسجد الكبير", "محاكاة صوتية رحبة لترداد التلاوة في رحاب المسجد والمحراب"),
  VOCAL_CLARITY("وضوح الترتيل والتجويد", "تعزيز ترددات الحروف ومخارج النطق لتفخيم وترقيق دقيق"),
  DEEP_WARMTH("الدفء والخشوع العميق", "تعزيز الترددات الخفيضة لإضفاء هيبة وخشوع يلامس القلوب"),
  GOLDEN_RECITER("عبق القراء القدامى", "محاكاة الدفء الصوتي والجمال الكلاسيكي لكبار القراء الأوائل")
}

class SoundEffectsHelper {

  private var equalizer: Equalizer? = null
  private var bassBoost: BassBoost? = null
  private var presetReverb: PresetReverb? = null

  var currentMode: SoundEffectMode = SoundEffectMode.PURE_STUDIO
    private set

  var bassBoostStrength: Short = 0 // 0 to 1000
    private set

  fun attachToSession(audioSessionId: Int) {
    if (audioSessionId <= 0) return
    releaseEffects()

    val supportedTypes = try {
      AudioEffect.queryEffects()?.map { it.type } ?: emptyList()
    } catch (_: Throwable) {
      emptyList()
    }

    // Equalizer
    if (supportedTypes.contains(AudioEffect.EFFECT_TYPE_EQUALIZER)) {
      try {
        equalizer = Equalizer(0, audioSessionId).apply {
          enabled = true
        }
      } catch (e: Exception) {
        Log.w("SoundEffectsHelper", "Equalizer not available on this device: ${e.message}")
      }
    }

    // Bass Boost
    if (supportedTypes.contains(AudioEffect.EFFECT_TYPE_BASS_BOOST)) {
      try {
        bassBoost = BassBoost(0, audioSessionId).apply {
          enabled = true
        }
      } catch (e: Exception) {
        Log.w("SoundEffectsHelper", "BassBoost not available on this device: ${e.message}")
      }
    }

    // Preset Reverb
    if (supportedTypes.contains(AudioEffect.EFFECT_TYPE_PRESET_REVERB)) {
      try {
        presetReverb = PresetReverb(0, audioSessionId).apply {
          enabled = true
        }
      } catch (e: Exception) {
        Log.w("SoundEffectsHelper", "PresetReverb not available on this device: ${e.message}")
      }
    }

    try {
      applyEffectMode(currentMode)
    } catch (e: Exception) {
      Log.w("SoundEffectsHelper", "Failed to apply mode: ${e.message}")
    }
  }

  fun applyEffectMode(mode: SoundEffectMode) {
    currentMode = mode
    val eq = equalizer ?: return
    val bb = bassBoost
    val rev = presetReverb

    try {
      when (mode) {
        SoundEffectMode.PURE_STUDIO -> {
          // Flat
          val numBands = eq.numberOfBands
          for (i in 0 until numBands) {
            eq.setBandLevel(i.toShort(), 0)
          }
          bb?.setStrength(0)
          rev?.preset = PresetReverb.PRESET_NONE
        }

        SoundEffectMode.MIHRAB_ECHO -> {
          // Boost mid-highs and cathedral/large hall reverb
          val numBands = eq.numberOfBands
          if (numBands > 2) {
            eq.setBandLevel(0, 100)
            eq.setBandLevel((numBands / 2).toShort(), 200)
            eq.setBandLevel((numBands - 1).toShort(), 300)
          }
          bb?.setStrength(300)
          rev?.preset = PresetReverb.PRESET_LARGEROOM
        }

        SoundEffectMode.VOCAL_CLARITY -> {
          // Vocal presence (around 1kHz - 4kHz boosted)
          val numBands = eq.numberOfBands
          if (numBands > 2) {
            eq.setBandLevel(0, (-100).toShort()) // slightly cut boomy bass
            eq.setBandLevel((numBands / 2).toShort(), 400) // vocal core
            eq.setBandLevel((numBands - 1).toShort(), 500) // crisp consonants
          }
          bb?.setStrength(100)
          rev?.preset = PresetReverb.PRESET_SMALLROOM
        }

        SoundEffectMode.DEEP_WARMTH -> {
          // Warm low-end boost
          val numBands = eq.numberOfBands
          if (numBands > 2) {
            eq.setBandLevel(0, 500)
            eq.setBandLevel(1, 300)
            eq.setBandLevel((numBands - 1).toShort(), 0)
          }
          bb?.setStrength(600)
          rev?.preset = PresetReverb.PRESET_MEDIUMROOM
        }

        SoundEffectMode.GOLDEN_RECITER -> {
          // Boost mids and add a slight vintage warmth
          val numBands = eq.numberOfBands
          if (numBands > 2) {
            eq.setBandLevel(0, 300)
            eq.setBandLevel((numBands / 2).toShort(), 600)
            eq.setBandLevel((numBands - 1).toShort(), (-200).toShort())
          }
          bb?.setStrength(400)
          rev?.preset = PresetReverb.PRESET_SMALLROOM
        }
      }
    } catch (e: Exception) {
      Log.w("SoundEffectsHelper", "Error applying effect mode: ${e.message}")
    }
  }

  fun setBassBoost(strength: Short) {
    bassBoostStrength = strength.coerceIn(0, 1000)
    try {
      bassBoost?.setStrength(bassBoostStrength)
    } catch (_: Exception) {}
  }

  fun releaseEffects() {
    try {
      equalizer?.release()
      bassBoost?.release()
      presetReverb?.release()
    } catch (_: Exception) {}
    equalizer = null
    bassBoost = null
    presetReverb = null
  }

  /**
   * Generates a serene, spiritual pentatonic prayer chime in real-time using AudioTrack.
   * Plays a sequence of harmonic frequencies (F4, A4, C5, F5, A5) with gentle natural decay.
   * Completely offline, instantaneous, and melodic for the "الصلاة على النبي ﷺ" reminder.
   */
  suspend fun playSpiritualSalawatChime() = withContext(Dispatchers.Default) {
    try {
      val sampleRate = 44100
      val durationSec = 3.2
      val numSamples = (durationSec * sampleRate).toInt()
      val sample = DoubleArray(numSamples)
      val generatedSnd = ByteArray(2 * numSamples)

      // Peaceful harmonic frequencies (Hz) for an uplifting spiritual bell chime
      val notes = listOf(
        Pair(349.23, 0.0),  // F4 at 0.0s
        Pair(440.00, 0.35), // A4 at 0.35s
        Pair(523.25, 0.70), // C5 at 0.70s
        Pair(698.46, 1.05), // F5 at 1.05s
        Pair(880.00, 1.40)  // A5 at 1.40s
      )

      for (i in 0 until numSamples) {
        val t = i.toDouble() / sampleRate
        var mixed = 0.0
        for ((freq, startTime) in notes) {
          if (t >= startTime) {
            val noteTime = t - startTime
            // Exponential gentle decay
            val envelope = Math.exp(-2.2 * noteTime)
            // Fundamental + octave shimmer
            val fundamental = sin(2.0 * Math.PI * freq * noteTime)
            val harmonic = 0.3 * sin(4.0 * Math.PI * freq * noteTime)
            mixed += (fundamental + harmonic) * envelope
          }
        }
        sample[i] = mixed.coerceIn(-1.0, 1.0)
      }

      // Convert to 16-bit PCM format
      var idx = 0
      for (dVal in sample) {
        val valShort = (dVal * 32767).toInt().toShort()
        generatedSnd[idx++] = (valShort.toInt() and 0x00ff).toByte()
        generatedSnd[idx++] = ((valShort.toInt() and 0xff00) ushr 8).toByte()
      }

      val audioTrack = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        )
        .setBufferSizeInBytes(generatedSnd.size)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      audioTrack.write(generatedSnd, 0, generatedSnd.size)
      audioTrack.play()

      // Allow playback to complete
      kotlinx.coroutines.delay(3500)
      audioTrack.release()
    } catch (e: Exception) {
      Log.w("SoundEffectsHelper", "Error generating spiritual chime: ${e.message}")
    }
  }
}
