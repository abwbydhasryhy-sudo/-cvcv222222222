package com.example.data.repository

import com.example.data.local.BookmarkEntity
import com.example.data.local.DownloadedSurahEntity
import com.example.data.local.FavoritePlaylistEntity
import com.example.data.local.QuranDao
import com.example.data.local.SettingEntity
import com.example.data.model.Ayah
import com.example.data.model.QuranData
import com.example.data.model.Reciter
import com.example.data.model.ReminderSettings
import com.example.data.model.Surah
import com.example.data.remote.QuranApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class QuranRepository(private val quranDao: QuranDao) {

  private val apiClient = QuranApiClient()
  private val surahAyahsCache = mutableMapOf<Int, List<Ayah>>()

  // Quran text and metadata
  fun getAllSurahs(): List<Surah> = QuranData.allSurahs

  fun getSurahByNumber(number: Int): Surah {
    return QuranData.allSurahs.find { it.number == number } ?: QuranData.allSurahs[0]
  }

  fun getAyahsForSurah(surahNumber: Int): List<Ayah> {
    return surahAyahsCache[surahNumber] ?: QuranData.getAyahsForSurah(surahNumber)
  }

  suspend fun fetchRemoteSurahAyahs(surahNumber: Int): List<Ayah>? {
    val remote = apiClient.fetchSurahAyahs(surahNumber)
    if (remote != null && remote.isNotEmpty()) {
      surahAyahsCache[surahNumber] = remote
      return remote
    }
    return null
  }

  fun searchQuran(query: String): List<Pair<Surah, Ayah?>> {
    val clean = query.trim().lowercase()
    if (clean.isEmpty()) return emptyList()

    val results = mutableListOf<Pair<Surah, Ayah?>>()
    for (surah in QuranData.allSurahs) {
      if (surah.nameArabic.contains(clean) || surah.nameEnglish.lowercase().contains(clean)) {
        results.add(Pair(surah, null))
      }
    }

    // Search in ayahs of key Surahs
    val allLoadedAyahs = QuranData.alIsraAyahs + QuranData.alKahfAyahs + QuranData.alFatihahAyahs +
      QuranData.anNabaAyahs + QuranData.alIkhlasAyahs + QuranData.alFalaqAyahs + QuranData.anNasAyahs +
      QuranData.alKawtharAyahs + QuranData.alAsrAyahs + QuranData.anNasrAyahs + QuranData.alKafirunAyahs +
      QuranData.alQadrAyahs + QuranData.alMulkAyahs + QuranData.yaSinAyahs + QuranData.arRahmanAyahs
    for (ayah in allLoadedAyahs) {
      if (ayah.textArabic.contains(clean) || ayah.tafsirMuyassar.contains(clean) || ayah.translationEnglish.lowercase().contains(clean)) {
        val surah = getSurahByNumber(ayah.surahNumber)
        results.add(Pair(surah, ayah))
      }
    }
    return results.distinctBy { "${it.first.number}_${it.second?.ayahNumber ?: 0}" }
  }

  // Bookmarks
  val allBookmarks: Flow<List<BookmarkEntity>> = quranDao.getAllBookmarks()
  val lastReadBookmark: Flow<BookmarkEntity?> = quranDao.getLastReadBookmark()

  suspend fun saveBookmark(surahNumber: Int, ayahNumber: Int, surahName: String, note: String = "") {
    quranDao.insertBookmark(
      BookmarkEntity(
        surahNumber = surahNumber,
        ayahNumber = ayahNumber,
        surahName = surahName,
        note = note,
        isLastRead = false
      )
    )
  }

  suspend fun saveLastRead(surahNumber: Int, ayahNumber: Int, surahName: String) {
    quranDao.clearLastRead()
    quranDao.insertBookmark(
      BookmarkEntity(
        surahNumber = surahNumber,
        ayahNumber = ayahNumber,
        surahName = surahName,
        isLastRead = true
      )
    )
  }

  suspend fun deleteBookmark(id: Long) {
    quranDao.deleteBookmarkById(id)
  }

  // Downloaded Surahs
  val downloadedSurahs: Flow<List<DownloadedSurahEntity>> = quranDao.getAllDownloadedSurahs()

  suspend fun addDownloadedSurah(download: DownloadedSurahEntity) {
    quranDao.insertDownloadedSurah(download)
  }

  suspend fun updateDownloadProgress(surahNumber: Int, progress: Int, isCompleted: Boolean) {
    val existing = quranDao.getDownloadedSurah(surahNumber)
    if (existing != null) {
      quranDao.updateDownloadedSurah(
        existing.copy(
          downloadProgress = progress,
          isCompleted = isCompleted
        )
      )
    }
  }

  suspend fun deleteDownloadedSurah(surahNumber: Int) {
    quranDao.deleteDownloadedSurah(surahNumber)
  }

  suspend fun clearDownloads() {
    quranDao.clearAllDownloadedSurahs()
  }

  // Playlists
  val allPlaylists: Flow<List<FavoritePlaylistEntity>> = quranDao.getAllPlaylists()

  suspend fun createPlaylist(title: String, reciterName: String, surahNumbers: List<Int>): Long {
    return quranDao.insertPlaylist(
      FavoritePlaylistEntity(
        title = title,
        reciterName = reciterName,
        surahNumbers = surahNumbers.joinToString(",")
      )
    )
  }

  suspend fun deletePlaylist(id: Long) {
    quranDao.deletePlaylist(id)
  }

  // Reciters
  fun getAllReciters(): List<Reciter> = QuranData.reciters

  // Settings
  suspend fun saveSetting(key: String, value: String) {
    quranDao.setSetting(SettingEntity(key, value))
  }

  suspend fun getSetting(key: String): String? {
    return quranDao.getSetting(key)
  }
}
