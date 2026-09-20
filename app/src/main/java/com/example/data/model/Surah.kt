package com.example.data.model

data class Surah(
  val number: Int,
  val nameArabic: String,
  val nameEnglish: String,
  val revelationType: String, // "مكية" / "مدنية"
  val totalAyahs: Int,
  val juzNumber: Int,
  val hizbNumber: Int,
  val pageNumber: Int,
  val durationMinutes: Int,
  val sizeMb: Double,
  val audioUrl: String = ""
)
