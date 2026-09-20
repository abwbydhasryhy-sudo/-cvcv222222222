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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.SpatialAudio
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlaybackState
import com.example.audio.SoundEffectMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioEffectsBottomSheet(
  playbackState: PlaybackState,
  onSelectMode: (SoundEffectMode) -> Unit,
  onBassBoostChange: (Short) -> Unit,
  onSpeedChange: (Float) -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .padding(bottom = 24.dp)
    ) {
      // Header
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
              tint = MaterialTheme.colorScheme.onSecondaryContainer,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "تحسين جودة الصوت والمؤثرات",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "هندسة صوتية خاشعة ومحاكاة لمحراب المسجد",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      Text(
        text = "أنماط الصوت والمؤثرات البيئية",
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.secondary
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Effect Modes List
      listOf(
        Triple(SoundEffectMode.PURE_STUDIO, Icons.Default.GraphicEq, "صوت خام بأعلى نقاوة أصلية"),
        Triple(SoundEffectMode.MIHRAB_ECHO, Icons.Default.SurroundSound, "محاكاة صدى المحراب وترداد الحرم"),
        Triple(SoundEffectMode.VOCAL_CLARITY, Icons.Default.SpatialAudio, "إبراز مخارج الحروف والترتيل المتقن"),
        Triple(SoundEffectMode.DEEP_WARMTH, Icons.Default.Tune, "دفء خاشع وتناغم عميق يريح النفس")
      ).forEach { (mode, icon, desc) ->
        val isSelected = playbackState.soundEffectMode == mode
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
              if (isSelected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
              else MaterialTheme.colorScheme.surfaceContainer
            )
            .border(
              width = if (isSelected) 1.5.dp else 0.5.dp,
              color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant,
              shape = RoundedCornerShape(16.dp)
            )
            .clickable { onSelectMode(mode) }
            .padding(14.dp)
            .testTag("effect_mode_${mode.name}")
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = mode.titleArabic,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = desc,
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            if (isSelected) {
              Box(
                modifier = Modifier
                  .size(26.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Selected",
                  tint = MaterialTheme.colorScheme.onSecondary,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Bass Boost Slider
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(MaterialTheme.colorScheme.surfaceContainer)
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "عمق الصوت والترددات الخفيضة (Bass Boost)",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${(playbackState.bassBoostLevel / 10)}%",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.secondary
          )
        }

        Slider(
          value = playbackState.bassBoostLevel.toFloat(),
          onValueChange = { onBassBoostChange(it.toInt().toShort()) },
          valueRange = 0f..1000f,
          colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.secondary,
            activeTrackColor = MaterialTheme.colorScheme.secondary
          ),
          modifier = Modifier.testTag("slider_bass_boost")
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Speed selection
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Speed,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "سرعة التلاوة والتدبر",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          listOf(0.75f, 1.0f, 1.25f, 1.5f).forEach { spd ->
            val isCurrent = playbackState.playbackSpeed == spd
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(
                  if (isCurrent) MaterialTheme.colorScheme.secondary
                  else MaterialTheme.colorScheme.surfaceContainerHighest
                )
                .clickable { onSpeedChange(spd) }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("speed_${spd}x")
            ) {
              Text(
                text = "${spd}x",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isCurrent) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }
  }
}
