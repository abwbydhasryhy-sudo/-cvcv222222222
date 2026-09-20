package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.data.model.Surah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDialog(
  query: String,
  results: List<Pair<Surah, Ayah?>>,
  onQueryChange: (String) -> Unit,
  onSelectResult: (Surah, Int) -> Unit,
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
        .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "البحث الذكي في القرآن الكريم",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "إغلاق",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("ابحث باسم السورة أو بكلمة من الآيات أو التفسير...") },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary
          )
        },
        trailingIcon = {
          if (query.isNotEmpty()) {
            IconButton(onClick = { onQueryChange("") }) {
              Icon(imageVector = Icons.Default.Close, contentDescription = "مسح")
            }
          }
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("search_input_field"),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
          focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
          unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
          focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
          unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
        )
      )

      Spacer(modifier = Modifier.height(14.dp))

      if (query.isEmpty()) {
        Text(
          text = "اقتراحات البحث: الإسراء، الكهف، النبأ، الفاتحة، سبحان، الحمد",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      } else {
        Text(
          text = "نتائج البحث (${results.size})",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(results) { (surah, ayah) ->
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .clickable {
                  val ayahIndex = if (ayah != null) ayah.ayahNumber - 1 else 0
                  onSelectResult(surah, ayahIndex)
                }
                .padding(12.dp)
                .testTag("search_result_${surah.number}_${ayah?.ayahNumber ?: 0}")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "${surah.number}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.secondary
                  )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "سورة ${surah.nameArabic}",
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    if (ayah != null) {
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "• الآية ${ayah.ayahNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                      )
                    }
                  }

                  if (ayah != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = ayah.textArabic,
                      style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                      color = MaterialTheme.colorScheme.secondary,
                      maxLines = 1
                    )
                  } else {
                    Text(
                      text = "${surah.revelationType} • آياتها ${surah.totalAyahs}",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
