package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlaybackState
import com.example.data.local.DownloadedSurahEntity
import com.example.data.model.QuranData
import com.example.data.model.Reciter
import com.example.ui.UiState
import com.example.ui.components.DownloadedSurahsList
import com.example.ui.components.StorageDashboardCard
import com.example.ui.components.ZenTimerCard

@Composable
fun DownloadsAndTimerScreen(
  uiState: UiState,
  playbackState: PlaybackState,
  downloadedSurahs: List<DownloadedSurahEntity>,
  onToggleTimer: () -> Unit,
  onAddFiveMinutes: () -> Unit,
  onSelectPreset: (String) -> Unit,
  onToggleSmartStop: (Boolean) -> Unit,
  onToggleFadeOut: (Boolean) -> Unit,
  onToggleDarkScreen: (Boolean) -> Unit,
  onQualityChange: (String) -> Unit,
  onDownloadFullMushaf: (Reciter) -> Unit,
  onClearCache: () -> Unit,
  onPlaySurah: (Int) -> Unit,
  onDeleteDownloadedSurah: (Int) -> Unit,
  onOpenAlerts: () -> Unit,
  onOpenCloudSync: () -> Unit,
  modifier: Modifier = Modifier
) {
  val featuredReciter = QuranData.reciters[0] // Sheikh Al-Hosary

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(bottom = 120.dp) // Leave room for floating audio player
  ) {
    // Top Bar matching Screenshot 1
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onOpenAlerts,
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .testTag("btn_downloads_alerts")
        ) {
          Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "التنبيهات",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = onOpenCloudSync,
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .testTag("btn_downloads_cloud")
        ) {
          Icon(
            imageVector = Icons.Default.CloudSync,
            contentDescription = "السحابة",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "مؤقت السكينة والتنزيلات",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "نور الفرقان • الإصدار القرآني الشامل",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.secondary
        )
      }

      // User Profile Avatar with gold bezel
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surfaceContainerHighest)
          .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
          .clickable(onClick = onOpenCloudSync),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Person,
          contentDescription = "الملف الشخصي والمزامنة",
          tint = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.size(22.dp)
        )
      }
    }

    // Spiritual Header & Ayah Badge matching Screenshot 1
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp)
        .shadow(12.dp, RoundedCornerShape(20.dp))
        .clip(RoundedCornerShape(20.dp))
        .background(
          Brush.horizontalGradient(
            listOf(
              MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.8f),
              MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f)
            )
          )
        )
        .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Text(
                text = "سكون الليل نشط",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSecondaryContainer
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "مؤقت السكينة والتهجد",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "﴿ وَجَعَلْنَا نَوْمَكُمْ سُبَاتًا ﴾",
            style = MaterialTheme.typography.bodyMedium.copy(
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.secondary
          )

          Text(
            text = "سورة النبأ • الآية ٩",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Bedtime,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(26.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 1. Zen Timer Section matching Screenshot 1
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      ZenTimerCard(
        state = playbackState,
        onToggleTimer = onToggleTimer,
        onAddFiveMinutes = onAddFiveMinutes,
        onSelectPreset = onSelectPreset,
        onToggleSmartStop = onToggleSmartStop,
        onToggleFadeOut = onToggleFadeOut,
        onToggleDarkScreen = onToggleDarkScreen
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // 2. Storage Dashboard & Reciter Bento Section matching Screenshot 1
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      StorageDashboardCard(
        usedStorageGb = uiState.usedStorageGb,
        availableStorageGb = uiState.availableStorageGb,
        currentQuality = uiState.audioQualityMode,
        onQualityChange = onQualityChange,
        onDownloadFullMushaf = onDownloadFullMushaf,
        onClearCache = onClearCache,
        featuredReciter = featuredReciter
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // 3. Downloaded Surahs List matching Screenshot 1
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      DownloadedSurahsList(
        downloadedList = downloadedSurahs,
        onPlaySurah = onPlaySurah,
        onDeleteSurah = onDeleteDownloadedSurah,
        onManageClick = onOpenCloudSync
      )
    }
  }
}
