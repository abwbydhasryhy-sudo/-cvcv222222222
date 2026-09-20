package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlaybackState

@Composable
fun ZenTimerCard(
  state: PlaybackState,
  onToggleTimer: () -> Unit,
  onAddFiveMinutes: () -> Unit,
  onSelectPreset: (String) -> Unit,
  onToggleSmartStop: (Boolean) -> Unit,
  onToggleFadeOut: (Boolean) -> Unit,
  onToggleDarkScreen: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .shadow(18.dp, RoundedCornerShape(26.dp))
      .clip(RoundedCornerShape(26.dp))
      .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f))
      .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(26.dp))
      .padding(18.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Circular Dial Gauge Visualization
      Box(
        modifier = Modifier
          .size(200.dp)
          .padding(8.dp),
        contentAlignment = Alignment.Center
      ) {
        val totalSec = when (state.timerPreset) {
          "15" -> 15 * 60f
          "30" -> 30 * 60f
          "45" -> 45 * 60f
          else -> 30 * 60f
        }
        val remainingSec = state.timerRemainingSeconds.toFloat().coerceIn(0f, totalSec)
        val sweepAngle = (remainingSec / totalSec) * 280f

        val trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
        val goldColor = MaterialTheme.colorScheme.secondary
        val emeraldColor = MaterialTheme.colorScheme.primary

        Canvas(modifier = Modifier.fillMaxSize()) {
          // Inactive track arc
          drawArc(
            color = trackColor,
            startAngle = 130f,
            sweepAngle = 280f,
            useCenter = false,
            style = Stroke(width = 20f, cap = StrokeCap.Round)
          )
          // Active progress arc
          if (remainingSec > 0) {
            drawArc(
              color = goldColor,
              startAngle = 130f,
              sweepAngle = sweepAngle,
              useCenter = false,
              style = Stroke(width = 22f, cap = StrokeCap.Round)
            )
          }
          // Inner emerald accent dashed track
          drawArc(
            color = emeraldColor.copy(alpha = 0.4f),
            startAngle = 130f,
            sweepAngle = 280f,
            useCenter = false,
            style = Stroke(width = 4f, cap = StrokeCap.Round)
          )
        }

        // Inner Digital Display
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.Nightlight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.height(2.dp))
          val minutes = state.timerRemainingSeconds / 60
          val seconds = state.timerRemainingSeconds % 60
          Text(
            text = "%02d:%02d".format(minutes, seconds),
            style = MaterialTheme.typography.headlineLarge.copy(
              fontSize = 28.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 2.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "متبقٍ حتى السكون",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
          )
        }
      }

      // Quick Play/Pause and +5 mini triggers
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = 8.dp)
      ) {
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable(onClick = onToggleTimer)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("zen_btn_toggle"),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = if (state.isTimerActive) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (state.isTimerActive) "إيقاف مؤقت" else "استئناف",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSecondaryContainer
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .clickable(onClick = onAddFiveMinutes)
            .testTag("zen_btn_add5"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "+5",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.secondary
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Presets title
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "المدة الزمنية المحددة",
          style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = "تدرج لمسي هادئ",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Preset Pills (15, 30, 45, end)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(
          Pair("15", "١٥ دقيقة"),
          Pair("30", "٣٠ دقيقة"),
          Pair("45", "٤٥ دقيقة"),
          Pair("end", "نهاية السورة")
        ).forEach { (key, label) ->
          val isSelected = state.timerPreset == key
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
              .background(
                if (isSelected) {
                  Brush.verticalGradient(
                    listOf(
                      MaterialTheme.colorScheme.secondaryContainer,
                      MaterialTheme.colorScheme.secondary
                    )
                  )
                } else {
                  Brush.verticalGradient(
                    listOf(
                      MaterialTheme.colorScheme.surfaceContainer,
                      MaterialTheme.colorScheme.surfaceContainer
                    )
                  )
                }
              )
              .clickable { onSelectPreset(key) }
              .padding(vertical = 10.dp)
              .testTag("timer_chip_$key"),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              if (key == "end") {
                Icon(
                  imageVector = Icons.Default.DoneAll,
                  contentDescription = null,
                  tint = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = "نهاية السورة",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                  color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              } else {
                Text(
                  text = key,
                  style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                  ),
                  color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "دقيقة",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                  color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Zen Customization Toggles (1: End of Surah, 2: Fade out, 3: Dim screen)
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ZenToggleItem(
          icon = Icons.Default.AutoStories,
          iconTint = MaterialTheme.colorScheme.primary,
          title = "التوقف الذكي بانتهاء السورة",
          subtitle = "يُكمل الآيات حتى ختام السورة دون بتر المعنى",
          checked = state.smartStopOnSurahEnd,
          onCheckedChange = onToggleSmartStop,
          tag = "toggle_smart_stop"
        )

        ZenToggleItem(
          icon = Icons.Default.VolumeDown,
          iconTint = MaterialTheme.colorScheme.secondary,
          title = "التلاشي الهادئ (Fade-Out)",
          subtitle = "انخفاض تدريجي لطيف خلال آخر ٥ دقائق",
          checked = state.fadeOutEnabled,
          onCheckedChange = onToggleFadeOut,
          tag = "toggle_fade_out"
        )

        ZenToggleItem(
          icon = Icons.Default.DarkMode,
          iconTint = MaterialTheme.colorScheme.tertiary,
          title = "شاشة السكون المعتمة",
          subtitle = "إطفاء البكسلات المضيئة لمنع إجهاد العين",
          checked = state.darkSukunScreenEnabled,
          onCheckedChange = onToggleDarkScreen,
          tag = "toggle_dark_screen"
        )
      }
    }
  }
}

@Composable
private fun ZenToggleItem(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconTint: Color,
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  tag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f))
      .padding(horizontal = 12.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      modifier = Modifier.weight(1f),
      verticalAlignment = Alignment.CenterVertically
    ) {
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
          tint = iconTint,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall.copy(fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = MaterialTheme.colorScheme.primary,
        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
      ),
      modifier = Modifier.testTag(tag)
    )
  }
}
