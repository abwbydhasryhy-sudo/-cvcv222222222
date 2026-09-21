package com.example

import com.example.data.prayer.CalculationMethod
import com.example.data.prayer.PrayerTimesCalculator
import org.junit.Assert.*
import org.junit.Test
import java.util.Date
import java.util.TimeZone

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testPrayerTimesCalculationForMakkah() {
    val makkahLat = 21.4225
    val makkahLon = 39.8262
    val tz = TimeZone.getTimeZone("Asia/Riyadh")
    val times = PrayerTimesCalculator.calculate(
      latitude = makkahLat,
      longitude = makkahLon,
      date = Date(),
      timeZone = tz,
      method = CalculationMethod.UMM_AL_QURA
    )

    assertNotNull(times)
    assertTrue(times.fajr.formattedTime.isNotEmpty())
    assertTrue(times.dhuhr.formattedTime.isNotEmpty())
    assertTrue(times.asr.formattedTime.isNotEmpty())
    assertTrue(times.maghrib.formattedTime.isNotEmpty())
    assertTrue(times.isha.formattedTime.isNotEmpty())
    assertEquals(6, times.allPrayers().size)
  }
}

