package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao {
  // Bookmarks
  @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
  fun getAllBookmarks(): Flow<List<BookmarkEntity>>

  @Query("SELECT * FROM bookmarks WHERE isLastRead = 1 LIMIT 1")
  fun getLastReadBookmark(): Flow<BookmarkEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBookmark(bookmark: BookmarkEntity): Long

  @Query("DELETE FROM bookmarks WHERE isLastRead = 1")
  suspend fun clearLastRead()

  @Query("DELETE FROM bookmarks WHERE id = :id")
  suspend fun deleteBookmarkById(id: Long)

  // Downloaded Surahs
  @Query("SELECT * FROM downloaded_surahs ORDER BY timestamp DESC")
  fun getAllDownloadedSurahs(): Flow<List<DownloadedSurahEntity>>

  @Query("SELECT * FROM downloaded_surahs WHERE surahNumber = :surahNumber LIMIT 1")
  suspend fun getDownloadedSurah(surahNumber: Int): DownloadedSurahEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDownloadedSurah(surah: DownloadedSurahEntity)

  @Update
  suspend fun updateDownloadedSurah(surah: DownloadedSurahEntity)

  @Query("DELETE FROM downloaded_surahs WHERE surahNumber = :surahNumber")
  suspend fun deleteDownloadedSurah(surahNumber: Int)

  @Query("DELETE FROM downloaded_surahs")
  suspend fun clearAllDownloadedSurahs()

  // Playlists
  @Query("SELECT * FROM favorite_playlists ORDER BY createdAt DESC")
  fun getAllPlaylists(): Flow<List<FavoritePlaylistEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPlaylist(playlist: FavoritePlaylistEntity): Long

  @Query("DELETE FROM favorite_playlists WHERE id = :id")
  suspend fun deletePlaylist(id: Long)

  // Settings
  @Query("SELECT value FROM app_settings WHERE `key` = :key LIMIT 1")
  suspend fun getSetting(key: String): String?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun setSetting(setting: SettingEntity)
}
