package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Spa
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DownloadedSurahEntity

@Composable
fun DownloadedSurahsList(
  downloadedList: List<DownloadedSurahEntity>,
  onPlaySurah: (Int) -> Unit,
  onDeleteSurah: (Int) -> Unit,
  onManageClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "السور المحملة والحالية",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .clickable(onClick = onManageClick)
          .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "إدارة السور",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(2.dp))
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(14.dp)
        )
      }
    }

    // List of Surahs
    if (downloadedList.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f))
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "لا توجد سور محملة بعد. يمكنك تحميل أي سورة للاستماع بدون اتصال.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    } else {
      downloadedList.forEach { item ->
        DownloadedSurahCard(
          item = item,
          onPlay = { onPlaySurah(item.surahNumber) },
          onDelete = { onDeleteSurah(item.surahNumber) }
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Peaceful Night Quote Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(18.dp))
        .background(
          Brush.horizontalGradient(
            listOf(
              MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.6f),
              MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
            )
          )
        )
        .padding(14.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Spa,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = "أنس الليل والقرآن",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.secondary
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "استمع لتلاوتك وأنت مستلقٍ، وسيتولى التطبيق خفض الصوت وإيقاف الشاشة برفق عند استغراقك في النوم.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

@Composable
private fun DownloadedSurahCard(
  item: DownloadedSurahEntity,
  onPlay: () -> Unit,
  onDelete: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .shadow(4.dp, RoundedCornerShape(16.dp))
      .clip(RoundedCornerShape(16.dp))
      .background(
        if (item.isCompleted) MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.75f)
        else MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f)
      )
      .padding(12.dp)
      .testTag("download_card_${item.surahNumber}")
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Number Cartouche
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "${item.surahNumber}",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = if (item.isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = item.surahName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(
                    if (item.isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                  )
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = if (item.isCompleted) "جاهزة" else "جارِ التحميل ${item.downloadProgress}٪",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                  color = if (item.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
              }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
              text = "${item.reciterName} • %.0f ميجابايت".format(item.sizeMb),
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Action icons
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (item.isCompleted) {
            IconButton(
              onClick = onPlay,
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .testTag("play_downloaded_${item.surahNumber}")
            ) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "تشغيل",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
              )
            }

            IconButton(
              onClick = onDelete,
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "خيارات",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
              )
            }
          } else {
            IconButton(
              onClick = onPlay,
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            ) {
              Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "إيقاف مؤقت",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }

      // If downloading, show progress bar
      if (!item.isCompleted) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth(item.downloadProgress / 100f)
              .height(5.dp)
              .clip(RoundedCornerShape(3.dp))
              .background(
                Brush.horizontalGradient(
                  listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary)
                )
              )
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          val loadedMb = item.sizeMb * (item.downloadProgress / 100.0)
          Text(
            text = "تم تحميل %.1f من %.0f ميجابايت".format(loadedMb, item.sizeMb),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "السرعة: ٤.٢ ميجابايت/ث",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}
