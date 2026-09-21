package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.QiblaState
import kotlin.math.roundToInt

@Composable
fun QiblaScreen(
  qiblaState: QiblaState,
  onRefreshLocation: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val animatedHeading by animateFloatAsState(
    targetValue = qiblaState.compassHeading,
    animationSpec = tween(durationMillis = 150),
    label = "heading"
  )
  val qiblaRelativeAngle = (qiblaState.qiblaAngle - qiblaState.compassHeading + 360f) % 360f
  val isAligned = qiblaState.isAligned || (qiblaRelativeAngle < 6f || qiblaRelativeAngle > 354f)

  val dialBorderColor by animateColorAsState(
    targetValue = if (isAligned) Color(0xFF2E7D32) else MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
    label = "dial_border"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
      .padding(bottom = 96.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Top
  ) {
    // Top Bar / Title
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "بوصلة القبلة المشرفة",
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.secondary,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "استشعار مدمج ودقيق باتجاه الكعبة",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      IconButton(
        onClick = onRefreshLocation,
        modifier = Modifier
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surfaceContainerHigh)
          .testTag("qibla_btn_refresh_location")
      ) {
        Icon(
          imageVector = Icons.Default.GpsFixed,
          contentDescription = "تحديث الموقع",
          tint = MaterialTheme.colorScheme.secondary
        )
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Alignment Banner
    if (isAligned) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20).copy(alpha = 0.15f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF2E7D32))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF2E7D32),
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "أنت الآن تواجه القبلة المشرفة باتجاه الكعبة بدقة 🕋",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF2E7D32)
          )
        }
      }
    } else {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        shape = RoundedCornerShape(16.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Explore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "قم بتدوير الهاتف حتى يتطابق المؤشر الأخضر مع الأعلى",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(28.dp))

    // Interactive Compass Dial
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(280.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        .border(2.5.dp, dialBorderColor, CircleShape)
    ) {
      // 360 Degrees Dial Canvas (rotates with phone heading)
      Canvas(
        modifier = Modifier
          .fillMaxSize()
          .rotate(-animatedHeading)
      ) {
        val centerOffset = center
        val radius = size.minDimension / 2.0f

        for (i in 0 until 360 step 15) {
          val isMajor = i % 90 == 0
          val isSemi = i % 30 == 0
          val strokeLen = if (isMajor) 22.dp.toPx() else if (isSemi) 14.dp.toPx() else 8.dp.toPx()
          val strokeW = if (isMajor) 3.dp.toPx() else 1.5.dp.toPx()
          val markColor = if (i == 0) Color(0xFFD32F2F) else Color.Gray.copy(alpha = if (isSemi) 0.6f else 0.3f)

          rotate(i.toFloat()) {
            drawLine(
              color = markColor,
              start = Offset(centerOffset.x, centerOffset.y - radius + 8.dp.toPx()),
              end = Offset(centerOffset.x, centerOffset.y - radius + 8.dp.toPx() + strokeLen),
              strokeWidth = strokeW
            )
          }
        }
      }

      // Compass North Icon
      Icon(
        imageVector = Icons.Default.Navigation,
        contentDescription = "North",
        modifier = Modifier
          .size(36.dp)
          .offset(y = (-95).dp)
          .rotate(-animatedHeading),
        tint = Color(0xFFD32F2F)
      )

      // Qibla Pointer (Fixed Relative Indicator to Kaaba)
      Icon(
        imageVector = Icons.Default.LocationOn,
        contentDescription = "Kaaba Direction",
        modifier = Modifier
          .size(54.dp)
          .offset(y = (-80).dp)
          .rotate(qiblaRelativeAngle),
        tint = if (isAligned) Color(0xFF2E7D32) else MaterialTheme.colorScheme.secondary
      )

      // Central Hub
      Surface(
        modifier = Modifier.size(24.dp),
        shape = CircleShape,
        color = if (isAligned) Color(0xFF2E7D32) else MaterialTheme.colorScheme.secondary,
        shadowElevation = 6.dp
      ) {
        Box(contentAlignment = Alignment.Center) {
          Surface(
            modifier = Modifier.size(8.dp),
            shape = CircleShape,
            color = Color.White
          ) {}
        }
      }
    }

    Spacer(modifier = Modifier.height(28.dp))

    // Statistics Cards: Degrees & Kaaba Distance
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "زاوية القبلة",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "${qiblaState.qiblaAngle.roundToInt()}°",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "من الشمال الحقيقي",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "المسافة إلى الكعبة",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "${qiblaState.distanceToKaabaKm.roundToInt()} كم",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "مكة المكرمة",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Location Info & Status
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "الموقع الجغرافي الحالي",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          val loc = qiblaState.location
          if (loc != null) {
            Text(
              text = String.format(java.util.Locale.US, "خط العرض: %.4f ، خط الطول: %.4f", loc.latitude, loc.longitude),
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          } else {
            Text(
              text = "جاري الاتصال بنظام GPS...",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.error
            )
          }
        }

        OutlinedButton(
          onClick = onRefreshLocation,
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "تحديث", fontSize = 12.sp)
        }
      }
    }
  }
}

