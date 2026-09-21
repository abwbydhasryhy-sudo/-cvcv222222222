package com.example.data.remote

import android.util.Log
import com.example.data.model.Ayah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class QuranApiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun fetchSurahAyahs(surahNumber: Int): List<Ayah>? = withContext(Dispatchers.IO) {
        try {
            // Fetch authentic Uthmani text, Muyassar Tafsir, and Sahih International translation
            val url = "https://api.alquran.cloud/v1/surah/$surahNumber/editions/quran-uthmani,ar.muyassar,en.sahih"
            val request = Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w("QuranApiClient", "HTTP failed: ${response.code}")
                return@withContext null
            }

            val body = response.body?.string() ?: return@withContext null
            val root = JSONObject(body)
            if (root.optInt("code") != 200) return@withContext null

            val dataArray = root.getJSONArray("data")
            if (dataArray.length() < 3) return@withContext null

            val uthmaniObj = dataArray.getJSONObject(0)
            val tafsirObj = dataArray.getJSONObject(1)
            val englishObj = dataArray.getJSONObject(2)

            val uthmaniAyahs = uthmaniObj.getJSONArray("ayahs")
            val tafsirAyahs = tafsirObj.getJSONArray("ayahs")
            val englishAyahs = englishObj.getJSONArray("ayahs")

            val count = uthmaniAyahs.length()
            val list = mutableListOf<Ayah>()

            for (i in 0 until count) {
                val uAyah = uthmaniAyahs.getJSONObject(i)
                val tAyah = if (i < tafsirAyahs.length()) tafsirAyahs.getJSONObject(i) else null
                val eAyah = if (i < englishAyahs.length()) englishAyahs.getJSONObject(i) else null

                val numberInSurah = uAyah.optInt("numberInSurah", i + 1)
                var arabicText = uAyah.optString("text", "")

                // Remove Bismillah from verse 1 of non-Fatihah surahs if present as prefix
                if (surahNumber != 1 && numberInSurah == 1 && arabicText.startsWith("بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ ")) {
                    arabicText = arabicText.removePrefix("بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ ").trim()
                }

                val tafsirText = tAyah?.optString("text", "") ?: ""
                val engText = eAyah?.optString("text", "") ?: ""

                list.add(
                    Ayah(
                        surahNumber = surahNumber,
                        ayahNumber = numberInSurah,
                        textArabic = arabicText,
                        translationEnglish = engText,
                        tafsirMuyassar = tafsirText
                    )
                )
            }
            return@withContext list
        } catch (e: Exception) {
            Log.e("QuranApiClient", "Failed to fetch surah $surahNumber: ${e.message}")
            return@withContext null
        }
    }
}
