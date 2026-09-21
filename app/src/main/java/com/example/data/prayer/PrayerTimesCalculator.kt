package com.example.data.prayer

import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import kotlin.math.*

enum class PrayerType(val titleArabic: String, val titleEnglish: String) {
    FAJR("الفجر", "Fajr"),
    SUNRISE("الشروق", "Sunrise"),
    DHUHR("الظهر", "Dhuhr"),
    ASR("العصر", "Asr"),
    MAGHRIB("المغرب", "Maghrib"),
    ISHA("العشاء", "Isha")
}

data class PrayerTime(
    val type: PrayerType,
    val timeMillis: Long,
    val formattedTime: String,
    val isNext: Boolean = false,
    val isPassed: Boolean = false
)

data class PrayerTimesDay(
    val fajr: PrayerTime,
    val sunrise: PrayerTime,
    val dhuhr: PrayerTime,
    val asr: PrayerTime,
    val maghrib: PrayerTime,
    val isha: PrayerTime,
    val nextPrayer: PrayerTime,
    val remainingMillisToNext: Long,
    val locationName: String = "موقعي الحالي",
    val hijriDateFormatted: String = ""
) {
    fun allPrayers(): List<PrayerTime> = listOf(fajr, sunrise, dhuhr, asr, maghrib, isha)
}

enum class CalculationMethod(val titleArabic: String, val fajrAngle: Double, val ishaAngle: Double, val ishaMinutesAfterMaghrib: Int? = null) {
    UMM_AL_QURA("أم القرى (مكة المكرمة)", 18.5, 0.0, 90),
    MUSLIM_WORLD_LEAGUE("رابطة العالم الإسلامي", 18.0, 17.0),
    EGYPTIAN("الهيئة المصرية العامة للمساحة", 19.5, 17.5),
    KARACHI("جامعة العلوم الإسلامية بكراتشي", 18.0, 18.0),
    NORTH_AMERICA("أمريكا الشمالية (ISNA)", 15.0, 15.0)
}

object PrayerTimesCalculator {

    fun calculate(
        latitude: Double,
        longitude: Double,
        date: Date = Date(),
        timeZone: TimeZone = TimeZone.getDefault(),
        method: CalculationMethod = CalculationMethod.UMM_AL_QURA
    ): PrayerTimesDay {
        val cal = Calendar.getInstance(timeZone).apply { time = date }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val julianDate = toJulianDay(year, month, day)
        val d = julianDate - 2451545.0

        // Mean anomaly of the Sun
        val g = fixAngle(357.529 + 0.98560028 * d)
        // Mean longitude of the Sun
        val q = fixAngle(280.459 + 0.98564736 * d)
        // Geocentric apparent ecliptic longitude
        val l = fixAngle(q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g)))
        // Obliquity of the ecliptic
        val e = 23.439 - 0.00000036 * d

        // Sun's Right Ascension
        var ra = Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(l)), cos(Math.toRadians(l)))) / 15.0
        ra = fixHour(ra)

        // Sun's Declination
        val sinD = sin(Math.toRadians(e)) * sin(Math.toRadians(l))
        val dec = Math.toDegrees(asin(sinD))

        // Equation of Time (in hours)
        val eqT = q / 15.0 - ra

        // Local timezone offset in hours
        val timeZoneOffsetHours = timeZone.getOffset(cal.timeInMillis).toDouble() / (1000.0 * 60.0 * 60.0)

        // Noon time in hours
        val noon = fixHour(12.0 + timeZoneOffsetHours - (longitude / 15.0) - eqT)

        // Sunrise and Sunset angle (-0.833 degrees for atmospheric refraction and solar disc)
        val sunAlt = -0.8333
        val sunriseHourAngle = calculateHourAngle(latitude, dec, sunAlt)
        val sunriseTime = noon - sunriseHourAngle
        val sunsetTime = noon + sunriseHourAngle

        // Fajr
        val fajrHourAngle = calculateHourAngle(latitude, dec, -method.fajrAngle)
        val fajrTime = noon - fajrHourAngle

        // Asr (Shafi'i: shadow = 1)
        val asrAlt = Math.toDegrees(atan(1.0 / (1.0 + tan(Math.toRadians(abs(latitude - dec))))))
        val asrHourAngle = calculateHourAngle(latitude, dec, asrAlt)
        val asrTime = noon + asrHourAngle

        // Maghrib
        val maghribTime = sunsetTime

        // Isha
        val ishaTime = if (method.ishaMinutesAfterMaghrib != null) {
            maghribTime + (method.ishaMinutesAfterMaghrib / 60.0)
        } else {
            val ishaHourAngle = calculateHourAngle(latitude, dec, -method.ishaAngle)
            noon + ishaHourAngle
        }

        // Convert decimal hours to Calendar millis
        val baseCal = Calendar.getInstance(timeZone).apply {
            time = date
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        fun toMillis(decimalHours: Double): Long {
            val h = decimalHours.toInt()
            val m = ((decimalHours - h) * 60).toInt()
            val s = ((((decimalHours - h) * 60) - m) * 60).toInt()
            val c = baseCal.clone() as Calendar
            c.set(Calendar.HOUR_OF_DAY, h % 24)
            c.set(Calendar.MINUTE, m.coerceIn(0, 59))
            c.set(Calendar.SECOND, s.coerceIn(0, 59))
            return c.timeInMillis
        }

        val fajrMillis = toMillis(fajrTime)
        val sunriseMillis = toMillis(sunriseTime)
        val dhuhrMillis = toMillis(noon)
        val asrMillis = toMillis(asrTime)
        val maghribMillis = toMillis(maghribTime)
        val ishaMillis = toMillis(ishaTime)

        val now = System.currentTimeMillis()

        // Build prayer items
        val fajrPt = PrayerTime(PrayerType.FAJR, fajrMillis, formatArabicTime(fajrMillis), isPassed = now > fajrMillis)
        val sunrisePt = PrayerTime(PrayerType.SUNRISE, sunriseMillis, formatArabicTime(sunriseMillis), isPassed = now > sunriseMillis)
        val dhuhrPt = PrayerTime(PrayerType.DHUHR, dhuhrMillis, formatArabicTime(dhuhrMillis), isPassed = now > dhuhrMillis)
        val asrPt = PrayerTime(PrayerType.ASR, asrMillis, formatArabicTime(asrMillis), isPassed = now > asrMillis)
        val maghribPt = PrayerTime(PrayerType.MAGHRIB, maghribMillis, formatArabicTime(maghribMillis), isPassed = now > maghribMillis)
        val ishaPt = PrayerTime(PrayerType.ISHA, ishaMillis, formatArabicTime(ishaMillis), isPassed = now > ishaMillis)

        val prayers = listOf(fajrPt, sunrisePt, dhuhrPt, asrPt, maghribPt, ishaPt)

        // Find next prayer
        val next = prayers.firstOrNull { it.timeMillis > now } ?: fajrPt.copy(
            timeMillis = fajrMillis + 24 * 60 * 60 * 1000L,
            isPassed = false
        )

        val remaining = (next.timeMillis - now).coerceAtLeast(0L)

        val hijriDate = calculateApproxHijriDate(cal)

        return PrayerTimesDay(
            fajr = fajrPt.copy(isNext = next.type == PrayerType.FAJR),
            sunrise = sunrisePt.copy(isNext = next.type == PrayerType.SUNRISE),
            dhuhr = dhuhrPt.copy(isNext = next.type == PrayerType.DHUHR),
            asr = asrPt.copy(isNext = next.type == PrayerType.ASR),
            maghrib = maghribPt.copy(isNext = next.type == PrayerType.MAGHRIB),
            isha = ishaPt.copy(isNext = next.type == PrayerType.ISHA),
            nextPrayer = next,
            remainingMillisToNext = remaining,
            hijriDateFormatted = hijriDate
        )
    }

    private fun calculateHourAngle(lat: Double, dec: Double, alt: Double): Double {
        val latRad = Math.toRadians(lat)
        val decRad = Math.toRadians(dec)
        val altRad = Math.toRadians(alt)
        val cosH = (sin(altRad) - sin(latRad) * sin(decRad)) / (cos(latRad) * cos(decRad))
        val clampedCosH = cosH.coerceIn(-1.0, 1.0)
        return Math.toDegrees(acos(clampedCosH)) / 15.0
    }

    private fun toJulianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2.0 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun fixAngle(angle: Double): Double {
        var a = angle - 360.0 * floor(angle / 360.0)
        if (a < 0) a += 360.0
        return a
    }

    private fun fixHour(hour: Double): Double {
        var h = hour - 24.0 * floor(hour / 24.0)
        if (h < 0) h += 24.0
        return h
    }

    fun formatArabicTime(millis: Long): String {
        val c = Calendar.getInstance().apply { timeInMillis = millis }
        var hour = c.get(Calendar.HOUR)
        if (hour == 0) hour = 12
        val minute = c.get(Calendar.MINUTE)
        val amPm = if (c.get(Calendar.AM_PM) == Calendar.AM) "ص" else "م"
        return String.format("%02d:%02d %s", hour, minute, amPm)
    }

    fun formatCountdown(remainingMillis: Long): String {
        val totalSeconds = remainingMillis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    private fun calculateApproxHijriDate(cal: Calendar): String {
        // High-precision arithmetic Kuweit algorithm for Hijri conversion
        val jd = toJulianDay(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
        val l = (jd - 1948440 + 10632).toInt()
        val n = ((l - 1) / 10631).toInt()
        val l2 = l - 10631 * n + 354
        val j = (((10985 - l2) / 5316).toInt()) * (((50 * l2) / 17719).toInt()) + ((l2 / 5670).toInt()) * (((43 * l2) / 15238).toInt())
        val l3 = l2 - (((30 - j) / 15).toInt()) * (((17719 * j) / 50).toInt()) - ((j / 16).toInt()) * (((15238 * j) / 43).toInt()) + 29
        val m = ((24 * l3) / 709).toInt()
        val d = l3 - ((709 * m) / 24).toInt()
        val y = (30 * n + j - 30).toInt()

        val hijriMonths = listOf(
            "محرم", "صفر", "ربيع الأول", "ربيع الآخر",
            "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
            "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
        )
        val monthName = hijriMonths.getOrElse((m - 1).coerceIn(0, 11)) { "رمضان" }
        return "$d $monthName $y هـ"
    }
}
