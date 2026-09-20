package com.example.data.model

data class Ayah(
  val surahNumber: Int,
  val ayahNumber: Int,
  val textArabic: String,
  val translationEnglish: String,
  val tafsirMuyassar: String,
  val irabGrammar: String = "",
  val audioUrl: String = ""
)
