package com.example.data.model

data class Reciter(
  val id: String,
  val nameArabic: String,
  val nameEnglish: String,
  val style: String, // مرتل كامل / ترتيل نادر / مجود / قصر المنفصل
  val rewaya: String, // حفص عن عاصم / ورش عن نافع
  val quality: String, // "320kbps" / "128kbps"
  val totalSurahs: Int = 114,
  val packSizeBytes: Long = 1800000000L, // ~1.8 GB
  val packSizeFormatted: String = "1.8 جيجابايت",
  val isVerified: Boolean = true,
  val isFavorite: Boolean = false,
  val serverSubfolder: String = "ar.alafasy",
  val bio: String = "",
  val serverUrlPrefix: String = "https://server8.mp3quran.net/afs/"
) {
  fun getSurahAudioUrl(surahNumber: Int): String {
    val formattedNumber = String.format("%03d", surahNumber)
    return "$serverUrlPrefix$formattedNumber.mp3"
  }
}
