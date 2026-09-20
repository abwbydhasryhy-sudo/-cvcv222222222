package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.data.model.Surah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyahDetailBottomSheet(
  surah: Surah,
  ayah: Ayah,
  fontSizeMultiplier: Float,
  onFontSizeChange: (Float) -> Unit,
  onPlayAyah: () -> Unit,
  onBookmarkAyah: () -> Unit,
  onCopyAyah: () -> Unit,
  onShareAyah: () -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 10.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Top bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "${ayah.ayahNumber}",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSecondaryContainer
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "سورة ${surah.nameArabic} • الآية ${ayah.ayahNumber}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "إغلاق",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Quick Actions Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        QuickActionButton(
          icon = Icons.Default.PlayCircle,
          label = "استماع",
          tint = MaterialTheme.colorScheme.primary,
          onClick = onPlayAyah,
          tag = "sheet_btn_play"
        )
        QuickActionButton(
          icon = Icons.Default.Bookmark,
          label = "حفظ موضع",
          tint = MaterialTheme.colorScheme.secondary,
          onClick = onBookmarkAyah,
          tag = "sheet_btn_bookmark"
        )
        QuickActionButton(
          icon = Icons.Default.ContentCopy,
          label = "نسخ",
          tint = MaterialTheme.colorScheme.tertiary,
          onClick = onCopyAyah,
          tag = "sheet_btn_copy"
        )
        QuickActionButton(
          icon = Icons.Default.Share,
          label = "مشاركة",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          onClick = onShareAyah,
          tag = "sheet_btn_share"
        )
      }

      // Font Size Controller Card
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(MaterialTheme.colorScheme.surfaceContainer)
          .padding(14.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.FormatSize,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "التحكم في حجم الخط القرآني",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            Text(
              text = "%.1fx".format(fontSizeMultiplier),
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.secondary
            )
          }

          Slider(
            value = fontSizeMultiplier,
            onValueChange = onFontSizeChange,
            valueRange = 0.8f..1.5f,
            steps = 6,
            colors = SliderDefaults.colors(
              thumbColor = MaterialTheme.colorScheme.secondary,
              activeTrackColor = MaterialTheme.colorScheme.secondary,
              inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
            ),
            modifier = Modifier.testTag("slider_font_size")
          )
        }
      }

      // Ayah Arabic Scripture Display
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.7f))
          .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
          .padding(16.dp)
      ) {
        Text(
          text = ayah.textArabic,
          style = MaterialTheme.typography.displayMedium.copy(
            fontSize = (24 * fontSizeMultiplier).sp,
            lineHeight = (46 * fontSizeMultiplier).sp,
            fontWeight = FontWeight.Bold
          ),
          color = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.fillMaxWidth()
        )
      }

      // Tafsir Al-Muyassar Section
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(MaterialTheme.colorScheme.surfaceContainer)
          .padding(16.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "التفسير الميسر • معاني الآية",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.secondary
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = ayah.tafsirMuyassar,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          if (ayah.irabGrammar.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "الإعراب واللغة:",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
              )
            )
            Text(
              text = ayah.irabGrammar,
              style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // English Translation
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f))
          .padding(14.dp)
      ) {
        Column {
          Text(
            text = "English Translation",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = ayah.translationEnglish,
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

@Composable
private fun QuickActionButton(
  icon: ImageVector,
  label: String,
  tint: Color,
  onClick: () -> Unit,
  tag: String
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 8.dp)
      .testTag(tag)
  ) {
    Box(
      modifier = Modifier
        .size(42.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceContainerHighest),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = tint,
        modifier = Modifier.size(20.dp)
      )
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}
