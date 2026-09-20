package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DataSaverOn
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Reciter

@Composable
fun StorageDashboardCard(
  usedStorageGb: Double,
  availableStorageGb: Double,
  currentQuality: String,
  onQualityChange: (String) -> Unit,
  onDownloadFullMushaf: (Reciter) -> Unit,
  onClearCache: () -> Unit,
  featuredReciter: Reciter,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.FolderSpecial,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "مستودع السكينة دون إنترنت",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      Text(
        text = "١٨ سورة جاهزة",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.secondary
      )
    }

    // Storage Visual Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(12.dp, RoundedCornerShape(22.dp))
        .clip(RoundedCornerShape(22.dp))
        .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.85f))
        .padding(16.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(
              text = "المساحة المشغولة للمصحف",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = "%.1f".format(usedStorageGb),
                style = MaterialTheme.typography.headlineLarge.copy(
                  fontSize = 24.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "جيجابايت",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "المساحة المتبقية بالجهاز",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = "%.1f".format(availableStorageGb),
                style = MaterialTheme.typography.headlineSmall.copy(
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.secondary
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "GB",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Segmented Gradient Storage Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(1.5.dp),
          horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(0.48f)
              .height(7.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(MaterialTheme.colorScheme.primary)
          )
          Box(
            modifier = Modifier
              .weight(0.28f)
              .height(7.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(MaterialTheme.colorScheme.secondary)
          )
          Box(
            modifier = Modifier
              .weight(0.14f)
              .height(7.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(MaterialTheme.colorScheme.tertiaryContainer)
          )
          Box(
            modifier = Modifier
              .weight(0.10f)
              .height(7.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Storage Legend
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          LegendItem(color = MaterialTheme.colorScheme.primary, text = "صوتيات HQ")
          LegendItem(color = MaterialTheme.colorScheme.secondary, text = "التفاسير والترجمة")
          LegendItem(color = MaterialTheme.colorScheme.tertiaryContainer, text = "التلاوات المؤقتة")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Audio Quality Switcher
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          val isHq = currentQuality == "320kbps"

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(
                if (isHq) MaterialTheme.colorScheme.surfaceContainerHighest else MaterialTheme.colorScheme.surfaceContainer
              )
              .clickable { onQualityChange("320kbps") }
              .padding(vertical = 8.dp)
              .testTag("quality_btn_320"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.HighQuality,
                contentDescription = null,
                tint = if (isHq) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "استوديو فائق (320kbps)",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = if (isHq) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(
                if (!isHq) MaterialTheme.colorScheme.surfaceContainerHighest else MaterialTheme.colorScheme.surfaceContainer
              )
              .clickable { onQualityChange("128kbps") }
              .padding(vertical = 8.dp)
              .testTag("quality_btn_128"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.DataSaverOn,
                contentDescription = null,
                tint = if (!isHq) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "موفّر للمساحة (128kbps)",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = if (!isHq) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    // Complete Reciter One-Tap Download Bento Showcase
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(16.dp, RoundedCornerShape(24.dp))
        .clip(RoundedCornerShape(24.dp))
        .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f))
        .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
        .padding(16.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Reciter Portrait with gold bezel
          Box(
            modifier = Modifier
              .size(56.dp)
              .clip(CircleShape)
              .border(
                2.dp,
                Brush.linearGradient(
                  listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary)
                ),
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = R.drawable.img_reciter_hosary),
              contentDescription = featuredReciter.nameArabic,
              contentScale = ContentScale.Crop,
              modifier = Modifier.size(56.dp)
            )

            // Small verified badge
            Box(
              modifier = Modifier
                .size(18.dp)
                .align(Alignment.BottomEnd)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Verified",
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.size(12.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "حزمة النخبة",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  ),
                  color = MaterialTheme.colorScheme.secondary
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "المرتل الكامل",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
              text = "مصحف الشيخ الحصري كاملاً",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )

            Text(
              text = "١١٤ سورة • رواية حفص عن عاصم",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "تلاوة متقنة بقصر المنفصل وضبط مخارج الحروف، مسجلة بنقاوة إتقان الأستوديو لتعينك على التدبر دون انقطاع الاتصال.",
          style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, lineHeight = 18.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "حجم الحزمة",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = featuredReciter.packSizeFormatted,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.secondary
            )
          }

          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(24.dp))
              .background(
                Brush.horizontalGradient(
                  listOf(
                    MaterialTheme.colorScheme.secondaryContainer,
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.secondaryContainer
                  )
                )
              )
              .clickable { onDownloadFullMushaf(featuredReciter) }
              .padding(horizontal = 18.dp, vertical = 10.dp)
              .testTag("btn_download_full_pack"),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.CloudDownload,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSecondary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "تحميل المصحف كاملاً",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSecondary
            )
          }
        }
      }
    }
  }
}

@Composable
private fun LegendItem(color: androidx.compose.ui.graphics.Color, text: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
