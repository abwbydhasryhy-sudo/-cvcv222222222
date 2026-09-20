package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.data.model.QuranData
import com.example.data.model.Surah
import com.example.ui.ReadingMode
import com.example.ui.UiState

@Composable
fun MushafScreen(
  uiState: UiState,
  onSelectSurah: (Surah) -> Unit,
  onSelectAyah: (Int) -> Unit,
  onSetReadingMode: (ReadingMode) -> Unit,
  onToggleNightMode: () -> Unit,
  onOpenTafsir: () -> Unit,
  onOpenSearch: () -> Unit,
  onOpenAlerts: () -> Unit,
  onOpenCloudSync: () -> Unit,
  onPlayAyah: (Surah, Int) -> Unit,
  onBookmarkCurrentAyah: () -> Unit,
  onFontSizeChange: (Float) -> Unit,
  modifier: Modifier = Modifier
) {
  val surah = uiState.currentSurah
  val ayahs = QuranData.getAyahsForSurah(surah.number)
  val selectedIndex = uiState.selectedAyahIndex.coerceIn(0, (ayahs.size - 1).coerceAtLeast(0))
  val selectedAyah = if (ayahs.isNotEmpty()) ayahs[selectedIndex] else Ayah(surah.number, 1, "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ", "", "", "")

  var isSurahDropdownOpen by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(bottom = 120.dp) // Padding for floating audio bar
  ) {
    // 1. Top Bar matching Screenshot 2
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
            .testTag("btn_top_alerts")
        ) {
          Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "التنبيهات والأذكار",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(6.dp))

        IconButton(
          onClick = onOpenCloudSync,
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .testTag("btn_top_cloud")
        ) {
          Icon(
            imageVector = Icons.Default.CloudSync,
            contentDescription = "مزامنة السحابة",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      // Center Surah Selector Pill
      Box {
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .clickable { isSurahDropdownOpen = true }
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("btn_surah_selector"),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "القرآن الكريم - سورة ${surah.nameArabic} (${surah.revelationType})",
            style = MaterialTheme.typography.titleMedium.copy(
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(18.dp)
          )
        }

        // Surahs Menu
        DropdownMenu(
          expanded = isSurahDropdownOpen,
          onDismissRequest = { isSurahDropdownOpen = false },
          modifier = Modifier
            .height(400.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
          QuranData.allSurahs.forEach { item ->
            DropdownMenuItem(
              text = {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "${item.number}. سورة ${item.nameArabic}",
                    fontWeight = if (item.number == surah.number) FontWeight.Bold else FontWeight.Normal,
                    color = if (item.number == surah.number) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "${item.totalAyahs} آية",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              },
              onClick = {
                onSelectSurah(item)
                isSurahDropdownOpen = false
              }
            )
          }
        }
      }

      // Search Action
      IconButton(
        onClick = onOpenSearch,
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surfaceContainer)
          .testTag("btn_top_search")
      ) {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "بحث في القرآن",
          tint = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    // 2. Scriptural Reference Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 2.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "الجزء الخامس عشر",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = "•",
        color = MaterialTheme.colorScheme.outlineVariant
      )
      Text(
        text = "الحزب التاسع والعشرون",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = "•",
        color = MaterialTheme.colorScheme.outlineVariant
      )
      Text(
        text = "صفحة ٢٨٢",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.secondary
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 3. Modes & Controls Row (تلاوة مفسرة, مصحوبة, نمط ليلي, حجم الخط)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // Mode 1: تلاوة مفسرة
        val isExplained = uiState.readingMode == ReadingMode.TAFSIR_EXPLAINED
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
              if (isExplained) MaterialTheme.colorScheme.secondaryContainer
              else MaterialTheme.colorScheme.surfaceContainer
            )
            .clickable { onSetReadingMode(ReadingMode.TAFSIR_EXPLAINED) }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("mode_tafsir"),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (isExplained) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSecondaryContainer,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
          }
          Text(
            text = "تلاوة مفسرة",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (isExplained) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Mode 2: مصحوبة
        val isAccompanied = uiState.readingMode == ReadingMode.ACCOMPANIED
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
              if (isAccompanied) MaterialTheme.colorScheme.secondaryContainer
              else MaterialTheme.colorScheme.surfaceContainer
            )
            .clickable { onSetReadingMode(ReadingMode.ACCOMPANIED) }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("mode_accompanied"),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Headphones,
            contentDescription = null,
            tint = if (isAccompanied) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "مصحوبة",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (isAccompanied) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Night Mode & Font Controls
      Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Font Size Increase / Decrease
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable {
              val next = if (uiState.fontSizeMultiplier >= 1.4f) 0.9f else uiState.fontSizeMultiplier + 0.15f
              onFontSizeChange(next)
            }
            .testTag("btn_font_size_cycle"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.FormatSize,
            contentDescription = "حجم الخط",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(18.dp)
          )
        }

        // Night Mode Switch
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onToggleNightMode)
            .testTag("btn_night_mode_toggle"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (uiState.isDarkTheme) Icons.Default.Nightlight else Icons.Default.WbSunny,
            contentDescription = "النمط الليلي",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 4. Noble Quran Ornamental Frame (matching Screenshot 2 Arabesque Gold Border)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp)
        .shadow(20.dp, RoundedCornerShape(26.dp))
        .clip(RoundedCornerShape(26.dp))
        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        .border(
          width = 2.dp,
          brush = Brush.linearGradient(
            listOf(
              MaterialTheme.colorScheme.secondary,
              MaterialTheme.colorScheme.secondaryContainer,
              MaterialTheme.colorScheme.primary,
              MaterialTheme.colorScheme.secondary
            )
          ),
          shape = RoundedCornerShape(26.dp)
        )
        .padding(14.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Surah Header Cartouche
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
              Brush.horizontalGradient(
                listOf(
                  MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f),
                  MaterialTheme.colorScheme.surfaceContainerHigh,
                  MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f)
                )
              )
            )
            .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(vertical = 10.dp, horizontal = 14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "آياتها ${surah.totalAyahs} ۞",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              ),
              color = MaterialTheme.colorScheme.secondary
            )

            Text(
              text = "سُورَةُ ${surah.nameArabic}",
              style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              ),
              color = MaterialTheme.colorScheme.secondary
            )

            Text(
              text = "۞ ترتيبها ${surah.number}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              ),
              color = MaterialTheme.colorScheme.secondary
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Basmalah Calligraphy (Unless Surah At-Tawbah)
        if (surah.number != 9) {
          Text(
            text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontSize = 22.sp,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 6.dp)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 5. Ayah Floating Context Options Bar (matches Screenshot 2 toolbar)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Selected Ayah chip
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "${selectedAyah.ayahNumber}",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSecondary
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "خيارات الآية الكريمة",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            // Interactive action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              AyahOptionButton(
                icon = Icons.Default.PlayArrow,
                label = "استماع",
                onClick = { onPlayAyah(surah, selectedIndex) },
                tag = "ayah_opt_listen"
              )
              AyahOptionButton(
                icon = Icons.Default.AutoAwesome,
                label = "تفسير",
                onClick = onOpenTafsir,
                tag = "ayah_opt_tafsir"
              )
              AyahOptionButton(
                icon = Icons.Default.Bookmark,
                label = "حفظ",
                onClick = onBookmarkCurrentAyah,
                tag = "ayah_opt_bookmark"
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 6. Quranic Scripture Text with Uthmanic Style & 3D Gold Circular Ayah Markers
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          ayahs.forEachIndexed { index, ayah ->
            val isCurrent = index == selectedIndex

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                  if (isCurrent) MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f)
                  else MaterialTheme.colorScheme.surfaceContainerLowest
                )
                .border(
                  width = if (isCurrent) 1.dp else 0.dp,
                  color = if (isCurrent) MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f) else androidx.compose.ui.graphics.Color.Transparent,
                  shape = RoundedCornerShape(14.dp)
                )
                .clickable { onSelectAyah(index) }
                .padding(10.dp)
                .testTag("ayah_item_${ayah.ayahNumber}")
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                // Text Column
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = ayah.textArabic,
                    style = MaterialTheme.typography.displayMedium.copy(
                      fontSize = (22 * uiState.fontSizeMultiplier).sp,
                      lineHeight = (42 * uiState.fontSizeMultiplier).sp,
                      fontWeight = FontWeight.SemiBold
                    ),
                    color = if (isCurrent) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                  )

                  if (uiState.readingMode == ReadingMode.TAFSIR_EXPLAINED && isCurrent) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                      text = ayah.tafsirMuyassar,
                      style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                      ),
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 3D Circular Ayah Marker
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                      if (isCurrent) MaterialTheme.colorScheme.secondary
                      else MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                    .border(
                      1.dp,
                      MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                      CircleShape
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "${ayah.ayahNumber}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isCurrent) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.secondary
                  )
                }
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 7. Tafsir Al-Muyassar Bottom Snippet Card (Matches Screenshot 2 footer)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .shadow(8.dp, RoundedCornerShape(20.dp))
        .clip(RoundedCornerShape(20.dp))
        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
        .padding(16.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "التفسير الميسر • الآية ${selectedAyah.ayahNumber}",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.secondary
            )
          }

          Text(
            text = "المزيد من التفاسير",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.clickable(onClick = onOpenTafsir)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = selectedAyah.tafsirMuyassar,
          style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 12.5.sp,
            lineHeight = 20.sp
          ),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
private fun AyahOptionButton(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  onClick: () -> Unit,
  tag: String
) {
  Row(
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .background(MaterialTheme.colorScheme.surfaceContainer)
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 4.dp)
      .testTag(tag),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = MaterialTheme.colorScheme.secondary,
      modifier = Modifier.size(14.dp)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}
