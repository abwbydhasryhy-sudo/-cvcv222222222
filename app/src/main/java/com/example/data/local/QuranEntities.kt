package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val surahNumber: Int,
  val ayahNumber: Int,
  val surahName: String,
  val timestamp: Long = System.currentTimeMillis(),
  val note: String = "",
  val isLastRead: Boolean = false
)

@Entity(tableName = "downloaded_surahs")
data class DownloadedSurahEntity(
  @PrimaryKey val surahNumber: Int,
  val surahName: String,
  val reciterId: String,
  val reciterName: String,
  val localPath: String,
  val sizeMb: Double,
  val downloadProgress: Int, // 0 to 100
  val isCompleted: Boolean,
  val audioQuality: String = "320kbps",
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_playlists")
data class FavoritePlaylistEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val reciterName: String,
  val surahNumbers: String, // Comma separated e.g. "1,18,36,67"
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class SettingEntity(
  @PrimaryKey val key: String,
  val value: String
)
