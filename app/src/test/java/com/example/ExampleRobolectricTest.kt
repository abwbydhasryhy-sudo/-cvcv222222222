package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("نور الفرقان", appName)
  }

  @Test
  fun `verify quran surahs and reciters loaded`() {
    val surahs = com.example.data.model.QuranData.allSurahs
    assertEquals(114, surahs.size)
    val alIsra = surahs.find { it.number == 17 }
    assertEquals("الإسراء", alIsra?.nameArabic)
    val reciters = com.example.data.model.QuranData.reciters
    assertEquals(26, reciters.size)
    // Check that every reciter has a valid server URL prefix and generates correct audio URL
    reciters.forEach { reciter ->
      val audioUrl = reciter.getSurahAudioUrl(1)
      org.junit.Assert.assertTrue("Valid MP3 url for ${reciter.nameEnglish}: $audioUrl", audioUrl.endsWith("001.mp3"))
      org.junit.Assert.assertTrue("Valid prefix for ${reciter.nameEnglish}", audioUrl.startsWith("https://"))
    }
  }

  @Test
  fun `verify sound effects helper and modes`() {
    kotlinx.coroutines.runBlocking {
      val soundEffectsHelper = com.example.audio.SoundEffectsHelper()
      val modes = com.example.audio.SoundEffectMode.values()
      assertEquals(5, modes.size)
      // Verify chime generator can produce byte samples
      soundEffectsHelper.playSpiritualSalawatChime()
    }
  }

  @Test
  fun `verify quran audio player initialization with exoplayer`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val player = com.example.audio.QuranAudioPlayer(context)
    val state = player.state.value
    org.junit.Assert.assertNotNull(state)
    org.junit.Assert.assertEquals(17, state.currentSurah.number)
    player.releaseMediaPlayer()
  }
}
