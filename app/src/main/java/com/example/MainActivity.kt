package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.data.model.QuranData
import com.example.ui.QuranViewModel
import com.example.ui.ScreenTab
import com.example.ui.components.AudioEffectsBottomSheet
import com.example.ui.components.AyahDetailBottomSheet
import com.example.ui.components.AzkarAndAlertsDialog
import com.example.ui.components.CloudSyncDialog
import com.example.ui.components.FloatingAudioBar
import com.example.ui.components.NoorBottomNavBar
import com.example.ui.components.SearchDialog
import com.example.ui.screens.DownloadsAndTimerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MosquesScreen
import com.example.ui.screens.MushafScreen
import com.example.ui.screens.QiblaScreen
import com.example.ui.screens.RecitersScreen
import com.example.ui.theme.NoorAlFurqanTheme

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest

class MainActivity : ComponentActivity() {

  private val viewModel: QuranViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val uiState by viewModel.uiState.collectAsState()
      val playbackState by viewModel.playbackState.collectAsState()
      val downloadedSurahs by viewModel.downloadedSurahs.collectAsState()
      val lastRead by viewModel.lastRead.collectAsState()

      val snackbarHostState = remember { SnackbarHostState() }
      val context = LocalContext.current

      val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
      ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
          permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
          viewModel.refreshLocation()
        }
      }

      LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
          arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
          )
        )
      }

      // Observe info messages for SnackBar
      LaunchedEffect(uiState.infoMessage) {
        uiState.infoMessage?.let { msg ->
          snackbarHostState.showSnackbar(
            message = msg,
            duration = SnackbarDuration.Short
          )
        }
      }

      NoorAlFurqanTheme(darkTheme = uiState.isDarkTheme) {
        // Enforce Arabic RTL layout direction for holy scriptural authenticity
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isExpanded = maxWidth >= 700.dp

            Scaffold(
              modifier = Modifier.fillMaxSize(),
              snackbarHost = { SnackbarHost(snackbarHostState) },
              containerColor = MaterialTheme.colorScheme.background,
              bottomBar = {
                if (!isExpanded) {
                  Column(modifier = Modifier.fillMaxWidth()) {
                    // Floating Audio Dock above navigation bar
                    FloatingAudioBar(
                      state = playbackState,
                      onTogglePlay = { viewModel.audioPlayer.togglePlayPause() },
                      onSkipNext = { viewModel.audioPlayer.skipNextAyah() },
                      onSkipPrevious = { viewModel.audioPlayer.skipPreviousAyah() },
                      onCycleSpeed = { viewModel.audioPlayer.cyclePlaybackSpeed() },
                      onSeek = { viewModel.audioPlayer.seekTo(it) },
                      onOpenAudioEffects = { viewModel.toggleAudioEffectsSheet(true) }
                    )

                    NoorBottomNavBar(
                      currentTab = uiState.currentTab,
                      onTabSelected = { viewModel.setTab(it) }
                    )
                  }
                }
              }
            ) { innerPadding ->
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
              ) {
                // Wide Screen Tablet Adaptivity
                val contentModifier = if (isExpanded) {
                  Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxHeight()
                } else {
                  Modifier.fillMaxSize()
                }

                Box(modifier = contentModifier) {
                  when (uiState.currentTab) {
                    ScreenTab.HOME -> {
                      HomeScreen(
                        uiState = uiState,
                        lastRead = lastRead,
                        onNavigateTab = { viewModel.setTab(it) },
                        onSelectSurah = { viewModel.selectSurah(it) },
                        onOpenAlerts = { viewModel.toggleAzkarAlertsDialog(true) },
                        onOpenCloudSync = { viewModel.toggleCloudSyncDialog(true) },
                        onOpenSearch = { viewModel.toggleSearch(true) },
                        onTestSalawat = { viewModel.testSalawatChimeNow() },
                        onToggleSalawatHourly = { enabled ->
                          viewModel.updateReminderSettings(
                            uiState.reminderSettings.copy(hourlySalawatEnabled = enabled)
                          )
                        },
                        onOpenAudioEffects = { viewModel.toggleAudioEffectsSheet(true) }
                      )
                    }

                    ScreenTab.MUSHAF -> {
                      MushafScreen(
                        uiState = uiState,
                        onSelectSurah = { viewModel.selectSurah(it) },
                        onSelectAyah = { viewModel.selectAyah(it) },
                        onSetReadingMode = { viewModel.setReadingMode(it) },
                        onToggleNightMode = { viewModel.toggleDarkTheme() },
                        onOpenTafsir = { viewModel.toggleTafsirSheet(true) },
                        onOpenSearch = { viewModel.toggleSearch(true) },
                        onOpenAlerts = { viewModel.toggleAzkarAlertsDialog(true) },
                        onOpenCloudSync = { viewModel.toggleCloudSyncDialog(true) },
                        onPlayAyah = { s, idx ->
                          viewModel.audioPlayer.playSurah(s)
                          viewModel.audioPlayer.playAyah(s, idx)
                        },
                        onBookmarkCurrentAyah = { viewModel.bookmarkCurrentAyah() },
                        onFontSizeChange = { viewModel.setFontSizeMultiplier(it) }
                      )
                    }

                    ScreenTab.RECITERS -> {
                      RecitersScreen(
                        currentReciter = playbackState.currentReciter,
                        onSelectReciter = { reciter ->
                          viewModel.audioPlayer.setReciter(reciter)
                          viewModel.showMessage("تم تعيين القارئ: ${reciter.nameArabic}")
                        },
                        onDownloadFullPack = { reciter ->
                          viewModel.downloadFullMushaf(reciter)
                        }
                      )
                    }

                    ScreenTab.DOWNLOADS_TIMER -> {
                      DownloadsAndTimerScreen(
                        uiState = uiState,
                        playbackState = playbackState,
                        downloadedSurahs = downloadedSurahs,
                        onToggleTimer = { viewModel.audioPlayer.toggleZenTimer() },
                        onAddFiveMinutes = { viewModel.audioPlayer.addFiveMinutesToTimer() },
                        onSelectPreset = { viewModel.audioPlayer.setTimerPreset(it) },
                        onToggleSmartStop = { viewModel.audioPlayer.toggleSmartStop(it) },
                        onToggleFadeOut = { viewModel.audioPlayer.toggleFadeOut(it) },
                        onToggleDarkScreen = { viewModel.audioPlayer.toggleDarkSukunScreen(it) },
                        onQualityChange = { viewModel.setAudioQuality(it) },
                        onDownloadFullMushaf = { viewModel.downloadFullMushaf(it) },
                        onClearCache = { viewModel.clearCache() },
                        onPlaySurah = { num ->
                          val s = QuranData.allSurahs.find { it.number == num } ?: QuranData.allSurahs[0]
                          viewModel.selectSurah(s)
                          viewModel.audioPlayer.playSurah(s)
                          viewModel.setTab(ScreenTab.MUSHAF)
                        },
                        onDeleteDownloadedSurah = { viewModel.deleteDownload(it) },
                        onOpenAlerts = { viewModel.toggleAzkarAlertsDialog(true) },
                        onOpenCloudSync = { viewModel.toggleCloudSyncDialog(true) }
                      )
                    }

                    ScreenTab.QIBLA -> {
                      val qiblaState by viewModel.qiblaState.collectAsState()
                      QiblaScreen(qiblaState = qiblaState)
                    }

                    ScreenTab.MOSQUES -> {
                      MosquesScreen()
                    }
                  }
                }
              }
            }

            // Dialogs & Sheets
            // 1. Ayah Detail & Tafsir Bottom Sheet
            if (uiState.isTafsirSheetVisible) {
              val currentAyahs = QuranData.getAyahsForSurah(uiState.currentSurah.number)
              val activeAyah = currentAyahs.getOrNull(uiState.selectedAyahIndex)
                ?: currentAyahs.firstOrNull()
                ?: QuranData.alIsraAyahs[0]

              AyahDetailBottomSheet(
                surah = uiState.currentSurah,
                ayah = activeAyah,
                fontSizeMultiplier = uiState.fontSizeMultiplier,
                onFontSizeChange = { viewModel.setFontSizeMultiplier(it) },
                onPlayAyah = {
                  viewModel.audioPlayer.playSurah(uiState.currentSurah)
                  viewModel.audioPlayer.playAyah(uiState.currentSurah, uiState.selectedAyahIndex)
                  viewModel.toggleTafsirSheet(false)
                },
                onBookmarkAyah = {
                  viewModel.bookmarkCurrentAyah()
                },
                onCopyAyah = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clip = ClipData.newPlainText("Quran Ayah", "${activeAyah.textArabic}\n[سورة ${uiState.currentSurah.nameArabic} - الآية ${activeAyah.ayahNumber}]")
                  clipboard.setPrimaryClip(clip)
                  viewModel.showMessage("تم نسخ الآية الكريمة إلى الحافظة")
                },
                onShareAyah = {
                  val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "${activeAyah.textArabic}\n[سورة ${uiState.currentSurah.nameArabic} - الآية ${activeAyah.ayahNumber}]\n\nالتفسير الميسر:\n${activeAyah.tafsirMuyassar}")
                    type = "text/plain"
                  }
                  val shareIntent = Intent.createChooser(sendIntent, "مشاركة الآية الكريمة")
                  context.startActivity(shareIntent)
                },
                onDismiss = { viewModel.toggleTafsirSheet(false) }
              )
            }

            // 2. Azkar & Salawat Hourly Alerts Dialog
            if (uiState.isAzkarAlertsDialogVisible) {
              AzkarAndAlertsDialog(
                settings = uiState.reminderSettings,
                azkarItems = uiState.azkarItems,
                onUpdateSettings = { viewModel.updateReminderSettings(it) },
                onTestSalawat = { viewModel.testSalawatChimeNow() },
                onIncrementCount = { viewModel.incrementAzkarCount(it) },
                onResetCategory = { viewModel.resetAzkarCategory(it) },
                onDismiss = { viewModel.toggleAzkarAlertsDialog(false) }
              )
            }

            // 3. Cloud Sync Dialog
            if (uiState.isCloudSyncDialogVisible) {
              CloudSyncDialog(
                isSyncing = uiState.isCloudSyncing,
                lastSyncTime = uiState.lastCloudSyncTime,
                onTriggerSync = { viewModel.syncWithCloud() },
                onDismiss = { viewModel.toggleCloudSyncDialog(false) }
              )
            }

            // 4. Search Dialog
            if (uiState.isSearchVisible) {
              SearchDialog(
                query = uiState.searchQuery,
                results = uiState.searchResults,
                onQueryChange = { viewModel.performSearch(it) },
                onSelectResult = { surah, ayahIndex ->
                  viewModel.selectSurah(surah)
                  viewModel.selectAyah(ayahIndex)
                  viewModel.toggleSearch(false)
                  viewModel.setTab(ScreenTab.MUSHAF)
                },
                onDismiss = { viewModel.toggleSearch(false) }
              )
            }

            // 5. Audio Effects & Equalizer Bottom Sheet
            if (uiState.isAudioEffectsVisible) {
              AudioEffectsBottomSheet(
                playbackState = playbackState,
                onSelectMode = { viewModel.setSoundEffectMode(it) },
                onBassBoostChange = { viewModel.setBassBoost(it) },
                onSpeedChange = { viewModel.audioPlayer.cyclePlaybackSpeed() },
                onDismiss = { viewModel.toggleAudioEffectsSheet(false) }
              )
            }
          }
        }
      }
    }
  }
}
