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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudSyncDialog(
  isSyncing: Boolean,
  lastSyncTime: String,
  onTriggerSync: () -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(60.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.CloudSync,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(32.dp)
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "تزامن السحابة بين الأجهزة",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "مزامنة مشفرة لمواضع التوقف، الإشارات المرجعية، وقوائم التلاوة المفضلة لديك",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Sync State Box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(MaterialTheme.colorScheme.surfaceContainer)
          .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
        .padding(16.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CloudDone,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "حالة الاتصال بالسحابة",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            Text(
              text = "متصل وآمن",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
          }

          Text(
            text = lastSyncTime,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Devices,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "الأجهزة المتزامنة: هاتف أندرويد + جهاز لوحي",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      // Sync Trigger Button
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(MaterialTheme.colorScheme.secondary)
          .clickable(enabled = !isSyncing, onClick = onTriggerSync)
          .padding(vertical = 14.dp)
          .testTag("btn_trigger_cloud_sync"),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (isSyncing) {
          CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "جارِ المزامنة السحابية...",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSecondary
          )
        } else {
          Icon(
            imageVector = Icons.Default.Sync,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "مزامنة البيانات الآن",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
