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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.local.BookmarkEntity
import com.example.data.model.QuranData
import com.example.data.model.Surah
import com.example.ui.ScreenTab
import com.example.ui.UiState

@Composable
fun HomeScreen(
  uiState: UiState,
  lastRead: BookmarkEntity?,
  onNavigateTab: (ScreenTab) -> Unit,
  onSelectSurah: (Surah) -> Unit,
  onOpenAlerts: () -> Unit,
  onOpenCloudSync: () -> Unit,
  onOpenSearch: () -> Unit,
  onTestSalawat: () -> Unit,
  onToggleSalawatHourly: (Boolean) -> Unit,
  onOpenAudioEffects: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(bottom = 120.dp)
  ) {
    // Top Bar
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
            .testTag("home_btn_alerts")
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
            .testTag("home_btn_cloud")
        ) {
          Icon(
            imageVector = Icons.Default.CloudSync,
            contentDescription = "تزامن السحابة",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "نور الفرقان",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.secondary
        )
        Text(
          text = "مصحف السكينة والتدبر",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      IconButton(
        onClick = onOpenSearch,
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surfaceContainer)
          .testTag("home_btn_search")
      ) {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "بحث",
          tint = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    // Hero: Resume Reading Bookmark Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .shadow(16.dp, RoundedCornerShape(24.dp))
        .clip(RoundedCornerShape(24.dp))
        .background(
          Brush.linearGradient(
            listOf(
              MaterialTheme.colorScheme.surfaceContainerHighest,
              MaterialTheme.colorScheme.surfaceContainerHigh
            )
          )
        )
        .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
        .padding(18.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoStories,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "متابعة القراءة",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
              )
              Text(
                text = lastRead?.surahName ?: "سورة الإسراء",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.surfaceContainer)
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = "الآية ${lastRead?.ayahNumber ?: 1}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "﴿ سُبْحَانَ الَّذِي أَسْرَىٰ بِعَبْدِهِ لَيْلًا مِّنَ الْمَسْجِدِ الْحَرَامِ إِلَى الْمَسْجِدِ الْأَقْصَى ﴾",
          style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp, lineHeight = 22.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .clickable {
              val surah = QuranData.allSurahs.find { it.number == (lastRead?.surahNumber ?: 17) } ?: QuranData.allSurahs[16]
              onSelectSurah(surah)
              onNavigateTab(ScreenTab.MUSHAF)
            }
            .padding(vertical = 12.dp)
            .testTag("home_btn_resume_reading"),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "متابعة التلاوة والتدبر",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSecondary
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Hourly Salawat Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .shadow(12.dp, RoundedCornerShape(22.dp))
        .clip(RoundedCornerShape(22.dp))
        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f), RoundedCornerShape(22.dp))
        .padding(16.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "ﷺ",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "تذكير الصلاة على النبي كل ساعة",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.secondary
              )
              Text(
                text = "إشعار صوتي رقيق يصدح بالصلاة على الحبيب ﷺ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Switch(
            checked = uiState.reminderSettings.hourlySalawatEnabled,
            onCheckedChange = onToggleSalawatHourly,
            colors = SwitchDefaults.colors(
              checkedThumbColor = MaterialTheme.colorScheme.secondary,
              checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.testTag("home_switch_salawat")
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .clickable(onClick = onTestSalawat)
            .padding(vertical = 10.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.VolumeUp,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "استمع لصوت التذكير الآن (تجربة التنبيه الصوتي)",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.secondary
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Daily Azkar & Alerts Quick Action
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        .clickable(onClick = onOpenAlerts)
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.WbSunny,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "الأذكار اليومية والتنبيهات المخصصة",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "أذكار الصباح، المساء، والنوم مع مسبحة رقمية",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Audio Effects & Acoustic Quality Enhancement Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        .clickable(onClick = onOpenAudioEffects)
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "تحسين جودة الصوت وهندسة التلاوة",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "صدى المحراب، وضوح الترتيل، وتعميق الترددات الخاشعة",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    // Qibla & Mosques Quick Actions
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Box(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(20.dp))
          .background(MaterialTheme.colorScheme.surfaceContainerHigh)
          .clickable { onNavigateTab(ScreenTab.QIBLA) }
          .padding(16.dp)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
          Icon(
            imageVector = Icons.Default.Explore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "اتجاه القبلة",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Box(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(20.dp))
          .background(MaterialTheme.colorScheme.surfaceContainerHigh)
          .clickable { onNavigateTab(ScreenTab.MOSQUES) }
          .padding(16.dp)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
          Icon(
            imageVector = Icons.Default.Place,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(28.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "المساجد القريبة",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Notable Surahs Quick Launch
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      Text(
        text = "سور مباركة مأثورة",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(
          Pair(18, "سورة الكهف"),
          Pair(36, "سورة يس"),
          Pair(56, "سورة الواقعة"),
          Pair(67, "سورة الملك")
        ).forEach { (number, name) ->
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
              .background(MaterialTheme.colorScheme.surfaceContainerHigh)
              .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
              .clickable {
                val surah = QuranData.allSurahs.find { it.number == number } ?: QuranData.allSurahs[0]
                onSelectSurah(surah)
                onNavigateTab(ScreenTab.MUSHAF)
              }
              .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "$number",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.secondary
              )
              Text(
                text = name,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }
  }
}
