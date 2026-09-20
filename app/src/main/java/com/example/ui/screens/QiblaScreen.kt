package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.QiblaState
import kotlin.math.roundToInt

@Composable
fun QiblaScreen(
    qiblaState: QiblaState,
    modifier: Modifier = Modifier
) {
    val animatedHeading by animateFloatAsState(targetValue = qiblaState.compassHeading)
    val qiblaDirection = (qiblaState.qiblaAngle - qiblaState.compassHeading + 360) % 360

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "تحديد اتجاه القبلة",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "وجه هاتفك باتجاه الكعبة المشرفة",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(300.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            // Compass background with degrees
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerOffset = center
                val radius = size.minDimension / 2.0f
                
                // Draw 360 degrees marks
                for (i in 0 until 360 step 30) {
                    rotate(i.toFloat()) {
                        drawLine(
                            color = Color.Gray.copy(alpha = 0.5f),
                            start = Offset(centerOffset.x, centerOffset.y - radius + 10.dp.toPx()),
                            end = Offset(centerOffset.x, centerOffset.y - radius + 25.dp.toPx()),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }
            }

            // Compass Needle (North)
            Icon(
                imageVector = Icons.Default.Explore,
                contentDescription = null,
                modifier = Modifier
                    .size(240.dp)
                    .rotate(-animatedHeading),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
            )

            // Qibla Pointer
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Kaaba Direction",
                modifier = Modifier
                    .size(60.dp)
                    .offset(y = (-110).dp)
                    .rotate(qiblaDirection),
                tint = if ((qiblaDirection < 5 || qiblaDirection > 355)) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
            )
            
            // Central Point
            Surface(
                modifier = Modifier.size(12.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {}
        }

        Spacer(modifier = Modifier.height(48.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "زاوية القبلة",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "${qiblaState.qiblaAngle.roundToInt()}°",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "البعد عن الكعبة",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "${qiblaState.distanceToKaabaKm.roundToInt()} كم",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        if (qiblaState.location == null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "جاري تحديد الموقع لضمان الدقة...",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
