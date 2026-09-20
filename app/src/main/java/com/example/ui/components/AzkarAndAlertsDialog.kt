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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AzkarCategory
import com.example.data.model.AzkarItem
import com.example.data.model.ReminderSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzkarAndAlertsDialog(
  settings: ReminderSettings,
  azkarItems: List<AzkarItem>,
  onUpdateSettings: (ReminderSettings) -> Unit,
  onTestSalawat: () -> Unit,
  onIncrementCount: (String) -> Unit,
  onResetCategory: (AzkarCategory) -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var selectedTab by remember { mutableIntStateOf(0) } // 0: التنبيهات, 1: الأذكار اليومية

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.NotificationsActive,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "التنبيهات والأذكار اليومية",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
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

      // Tabs: إعدادات التنبيهات / مسبحة الأذكار
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.secondary
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = {
            Text(
              text = "التنبيهات والتذكير",
              fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
            )
          }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Text(
              text = "قراءة الأذكار والتسبيح",
              fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
            )
          }
        )
      }

      if (selectedTab == 0) {
        // Hourly Salawat Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
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
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "ﷺ",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
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
                    text = "إشعار وتنبيه صوتي رقيق كل ٦٠ دقيقة",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Switch(
                checked = settings.hourlySalawatEnabled,
                onCheckedChange = { onUpdateSettings(settings.copy(hourlySalawatEnabled = it)) },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = MaterialTheme.colorScheme.secondary,
                  checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.testTag("switch_hourly_salawat")
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Test Chime Button
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .clickable(onClick = onTestSalawat)
                .padding(vertical = 10.dp, horizontal = 12.dp)
                .testTag("btn_test_salawat_chime"),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "استمع لتنبيه الصلاة على النبي الآن (تجربة)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.secondary
              )
            }
          }
        }

        // Daily Azkar Notifications System
        Text(
          text = "مواعيد تنبيهات الأذكار اليومية",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          ReminderSettingRow(
            icon = Icons.Default.WbSunny,
            title = "أذكار الصباح",
            time = settings.morningAzkarTime,
            checked = settings.morningAzkarEnabled,
            onCheckedChange = { onUpdateSettings(settings.copy(morningAzkarEnabled = it)) },
            tag = "switch_morning_azkar"
          )

          ReminderSettingRow(
            icon = Icons.Default.WbTwilight,
            title = "أذكار المساء",
            time = settings.eveningAzkarTime,
            checked = settings.eveningAzkarEnabled,
            onCheckedChange = { onUpdateSettings(settings.copy(eveningAzkarEnabled = it)) },
            tag = "switch_evening_azkar"
          )

          ReminderSettingRow(
            icon = Icons.Default.Alarm,
            title = "أذكار النوم",
            time = settings.sleepAzkarTime,
            checked = settings.sleepAzkarEnabled,
            onCheckedChange = { onUpdateSettings(settings.copy(sleepAzkarEnabled = it)) },
            tag = "switch_sleep_azkar"
          )
        }
      } else {
        // Tasbeeh & Azkar Reading Tab
        azkarItems.forEach { item ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(18.dp))
              .background(MaterialTheme.colorScheme.surfaceContainer)
              .padding(14.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = item.title,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.secondary
                )
                // Counter Badge
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "${item.currentCount} / ${item.targetCount}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = item.text,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = MaterialTheme.colorScheme.onSurface
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = item.reward,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.primary
              )

              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(MaterialTheme.colorScheme.primary)
                  .clickable { onIncrementCount(item.id) }
                  .padding(vertical = 10.dp)
                  .testTag("btn_tasbeeh_${item.id}"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = if (item.currentCount >= item.targetCount) "تم بحمد الله ✓" else "تسبيح (+1)",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onPrimary
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun ReminderSettingRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  time: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  tag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(MaterialTheme.colorScheme.surfaceContainer)
      .padding(horizontal = 14.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(18.dp)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "موعد التنبيه: $time",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = MaterialTheme.colorScheme.primary,
        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
      ),
      modifier = Modifier.testTag(tag)
    )
  }
}
