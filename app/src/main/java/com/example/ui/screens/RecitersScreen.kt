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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.QuranData
import com.example.data.model.Reciter

@Composable
fun RecitersScreen(
  currentReciter: Reciter,
  onSelectReciter: (Reciter) -> Unit,
  onDownloadFullPack: (Reciter) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedFilter by remember { mutableStateOf("all") } // "all", "320", "fav"

  val reciters = QuranData.reciters.filter {
    when (selectedFilter) {
      "320" -> it.quality == "320kbps"
      "fav" -> it.isFavorite
      else -> true
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(bottom = 120.dp)
  ) {
    // Top Bar
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
      Text(
        text = "مكتبة القرّاء والتلاوات",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = "استمع وحمّل المصحف كاملاً بنقاوة عالية دون إنترنت",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Filter Chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(
          label = "جميع القرّاء",
          selected = selectedFilter == "all",
          onClick = { selectedFilter = "all" },
          tag = "filter_all"
        )
        FilterChip(
          label = "استوديو فائق (320kbps)",
          selected = selectedFilter == "320",
          onClick = { selectedFilter = "320" },
          tag = "filter_320"
        )
        FilterChip(
          label = "المفضلة",
          selected = selectedFilter == "fav",
          onClick = { selectedFilter = "fav" },
          tag = "filter_fav"
        )
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(reciters) { reciter ->
        ReciterCard(
          reciter = reciter,
          isSelected = currentReciter.id == reciter.id,
          onSelect = { onSelectReciter(reciter) },
          onDownloadPack = { onDownloadFullPack(reciter) }
        )
      }
    }
  }
}

@Composable
private fun FilterChip(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  tag: String
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .background(
        if (selected) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.surfaceContainer
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 6.dp)
      .testTag(tag)
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
      color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

@Composable
private fun ReciterCard(
  reciter: Reciter,
  isSelected: Boolean,
  onSelect: () -> Unit,
  onDownloadPack: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .shadow(8.dp, RoundedCornerShape(20.dp))
      .clip(RoundedCornerShape(20.dp))
      .background(
        if (isSelected) MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f)
        else MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f)
      )
      .border(
        width = if (isSelected) 1.5.dp else 0.5.dp,
        color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant,
        shape = RoundedCornerShape(20.dp)
      )
      .padding(14.dp)
      .testTag("reciter_card_${reciter.id}")
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Portrait
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceContainerHighest)
              .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            if (reciter.id == "alhosary") {
              Image(
                painter = painterResource(id = R.drawable.img_reciter_hosary),
                contentDescription = reciter.nameArabic,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(54.dp)
              )
            } else {
              Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(30.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = reciter.nameArabic,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              if (reciter.isFavorite) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = Icons.Default.Verified,
                  contentDescription = "Verified",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(14.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
              text = "${reciter.style} • ${reciter.rewaya}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.secondary
            )

            Text(
              text = "${reciter.quality} • حزمة المصحف: ${reciter.packSizeFormatted}",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Active indicator / Favorite
        IconButton(onClick = onSelect) {
          Icon(
            imageVector = if (isSelected) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Bottom action row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Set as active reciter button
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
              if (isSelected) MaterialTheme.colorScheme.secondaryContainer
              else MaterialTheme.colorScheme.surfaceContainerHighest
            )
            .clickable(onClick = onSelect)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("btn_select_reciter_${reciter.id}"),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isSelected) "القارئ النشط للتلاوة" else "اختيار وتلاوة عينة",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
          )
        }

        // Download Full Pack
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onDownloadPack)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("btn_dl_pack_${reciter.id}"),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.CloudDownload,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "تحميل المصحف",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
        }
      }
    }
  }
}
