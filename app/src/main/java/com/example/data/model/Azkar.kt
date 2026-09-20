package com.example.data.model

data class AzkarItem(
  val id: String,
  val category: AzkarCategory,
  val title: String,
  val text: String,
  val reward: String,
  val targetCount: Int,
  val currentCount: Int = 0
)

enum class AzkarCategory(val labelArabic: String, val labelEnglish: String) {
  MORNING("أذكار الصباح", "Morning Azkar"),
  EVENING("أذكار المساء", "Evening Azkar"),
  SLEEP("أذكار النوم والسكينة", "Sleep Azkar"),
  WAKEUP("أذكار الاستيقاظ", "Wakeup Azkar")
}

data class ReminderSettings(
  val hourlySalawatEnabled: Boolean = true,
  val hourlySalawatIntervalMinutes: Int = 60,
  val morningAzkarEnabled: Boolean = true,
  val morningAzkarTime: String = "06:00",
  val eveningAzkarEnabled: Boolean = true,
  val eveningAzkarTime: String = "17:30",
  val sleepAzkarEnabled: Boolean = true,
  val sleepAzkarTime: String = "22:30",
  val audioChimeEnabled: Boolean = true,
  val vibrationEnabled: Boolean = true
)
