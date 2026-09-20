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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlaybackState

@Composable
fun FloatingAudioBar(
  state: PlaybackState,
  onTogglePlay: () -> Unit,
  onSkipNext: () -> Unit,
  onSkipPrevious: () -> Unit,
  onCycleSpeed: () -> Unit,
  onSeek: (Int) -> Unit,
  onOpenAudioEffects: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp)
      .shadow(elevation = 16.dp, shape = RoundedCornerShape(22.dp))
      .clip(RoundedCornerShape(22.dp))
      .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f))
      .border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f),
        shape = RoundedCornerShape(22.dp)
      )
      .padding(10.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Upper row: Reciter Info + Playback controls
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Reciter & Track Info
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceContainer)
              .border(1.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.GraphicEq,
              contentDescription = "Audio track",
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "سورة ${state.currentSurah.nameArabic} • الآية ${state.currentAyahIndex + 1}",
              style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold
              ),
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = state.currentReciter.nameArabic,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
              color = MaterialTheme.colorScheme.secondary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        // Action Buttons
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onSkipPrevious,
            modifier = Modifier
              .size(36.dp)
              .testTag("audio_skip_previous")
          ) {
            Icon(
              imageVector = Icons.Default.SkipPrevious,
              contentDescription = "السابق",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
          }

          // 3D Embossed Play/Pause Button
          Box(
            modifier = Modifier
              .size(44.dp)
              .shadow(8.dp, CircleShape)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  colors = listOf(
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.secondaryContainer
                  )
                )
              )
              .clickable(onClick = onTogglePlay)
              .testTag("audio_play_pause"),
            contentAlignment = Alignment.Center
          ) {
            if (state.isLoading) {
              CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                strokeWidth = 2.5.dp
              )
            } else {
              Icon(
                imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (state.isPlaying) "إيقاف مؤقت" else "تشغيل",
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(26.dp)
              )
            }
          }

          IconButton(
            onClick = onSkipNext,
            modifier = Modifier
              .size(36.dp)
              .testTag("audio_skip_next")
          ) {
            Icon(
              imageVector = Icons.Default.SkipNext,
              contentDescription = "التالي",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Scrubber row
      val progress = if (state.totalDurationSeconds > 0) {
        state.currentPositionSeconds.toFloat() / state.totalDurationSeconds
      } else 0f

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = formatTime(state.currentPositionSeconds),
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Scrubber Track
        Box(
          modifier = Modifier
            .weight(1f)
            .height(5.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .clickable {
              val newSec = (progress * state.totalDurationSeconds).toInt()
              onSeek(newSec)
            }
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth(progress.coerceIn(0f, 1f))
              .height(5.dp)
              .clip(RoundedCornerShape(3.dp))
              .background(
                Brush.horizontalGradient(
                  colors = listOf(
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.primary
                  )
                )
              )
          )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(
          text = formatTime(state.totalDurationSeconds),
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Audio Effects & Equalizer button
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(0.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .clickable(onClick = onOpenAudioEffects)
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .testTag("audio_effects_button")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = "مؤثرات الصوت",
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "المؤثرات",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              ),
              color = MaterialTheme.colorScheme.secondary
            )
          }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Speed Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
            .clickable(onClick = onCycleSpeed)
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .testTag("audio_speed_button")
        ) {
          Text(
            text = "${state.playbackSpeed}x",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.secondary
          )
        }
      }
    }
  }
}

private fun formatTime(seconds: Int): String {
  val m = seconds / 60
  val s = seconds % 60
  return "%02d:%02d".format(m, s)
}
