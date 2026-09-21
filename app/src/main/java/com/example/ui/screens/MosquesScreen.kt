package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Mosque(
    val name: String,
    val distance: String,
    val address: String,
    val nextPrayer: String,
    val nextPrayerTime: String,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val isOpen: Boolean = true
)

@Composable
fun MosquesScreen(
    location: Location? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mosques = remember(location) {
        listOf(
            Mosque("جامع الراجحي الكبير", "1.2 كم", "حي الجزيرة، الرياض", "العصر", "3:45 م", 24.6854, 46.7825),
            Mosque("مسجد الملك عبد العزيز", "0.5 كم", "وسط المدينة", "العصر", "3:45 م", 24.6468, 46.7118),
            Mosque("جامع الأميرة لؤلؤة", "2.1 كم", "حي المعذر", "العصر", "3:45 م", 24.6675, 46.6852),
            Mosque("مسجد الفرقان", "0.8 كم", "شارع العليا", "العصر", "3:45 م", 24.7112, 46.6744),
            Mosque("جامع الإيمان", "3.5 كم", "حي الروضة", "العصر", "3:45 م", 24.7431, 46.7621)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "المساجد القريبة",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "ابحث عن أقرب بيوت الله لأداء صلاة الجماعة",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { openMapSearch(context, location) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("فتح الخريطة", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(mosques) { mosque ->
                MosqueCard(mosque = mosque, onDirections = { openDirections(context, mosque, location) })
            }
        }
    }
}

@Composable
fun MosqueCard(mosque: Mosque, onDirections: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = mosque.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = mosque.distance,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = mosque.address,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp).alpha(0.1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "صلاة ${mosque.nextPrayer} القادمة: ${mosque.nextPrayerTime}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Button(
                    onClick = onDirections,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(34.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("الاتجاهات", fontSize = 12.sp)
                }
            }
        }
    }
}

private fun openMapSearch(context: Context, location: Location?) {
    val query = if (location != null) {
        "geo:${location.latitude},${location.longitude}?q=مساجد"
    } else {
        "geo:0,0?q=مساجد"
    }
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(query))
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        val web = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=مساجد"))
        context.startActivity(web)
    }
}

private fun openDirections(context: Context, mosque: Mosque, location: Location?) {
    val uri = if (mosque.lat != 0.0 && mosque.lon != 0.0) {
        "google.navigation:q=${mosque.lat},${mosque.lon}"
    } else {
        "geo:0,0?q=" + Uri.encode(mosque.name + " " + mosque.address)
    }
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        val web = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(mosque.name + " " + mosque.address)))
        context.startActivity(web)
    }
}

