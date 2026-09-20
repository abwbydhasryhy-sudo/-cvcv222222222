package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ScreenTab

data class NavItem(
  val tab: ScreenTab,
  val labelArabic: String,
  val labelEnglish: String,
  val iconActive: ImageVector,
  val iconInactive: ImageVector,
  val testTag: String
)

@Composable
fun NoorBottomNavBar(
  currentTab: ScreenTab,
  onTabSelected: (ScreenTab) -> Unit,
  isEnglish: Boolean = false,
  modifier: Modifier = Modifier
) {
  val items = listOf(
    NavItem(
      tab = ScreenTab.HOME,
      labelArabic = "الرئيسية",
      labelEnglish = "Home",
      iconActive = Icons.Filled.Mosque,
      iconInactive = Icons.Outlined.Home,
      testTag = "nav_home"
    ),
    NavItem(
      tab = ScreenTab.MUSHAF,
      labelArabic = "المصحف",
      labelEnglish = "Mushaf",
      iconActive = Icons.Filled.MenuBook,
      iconInactive = Icons.Outlined.MenuBook,
      testTag = "nav_mushaf"
    ),
    NavItem(
      tab = ScreenTab.RECITERS,
      labelArabic = "القرّاء",
      labelEnglish = "Reciters",
      iconActive = Icons.Filled.GraphicEq,
      iconInactive = Icons.Filled.GraphicEq,
      testTag = "nav_reciters"
    ),
    NavItem(
      tab = ScreenTab.QIBLA,
      labelArabic = "القبلة",
      labelEnglish = "Qibla",
      iconActive = Icons.Filled.Explore,
      iconInactive = Icons.Outlined.Explore,
      testTag = "nav_qibla"
    ),
    NavItem(
      tab = ScreenTab.MOSQUES,
      labelArabic = "المساجد",
      labelEnglish = "Mosques",
      iconActive = Icons.Filled.Place,
      iconInactive = Icons.Outlined.Place,
      testTag = "nav_mosques"
    ),
    NavItem(
      tab = ScreenTab.DOWNLOADS_TIMER,
      labelArabic = "المحفوظات",
      labelEnglish = "Saved",
      iconActive = Icons.Filled.Downloading,
      iconInactive = Icons.Outlined.FileDownload,
      testTag = "nav_downloads_timer"
    )
  )

  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f))
      .navigationBarsPadding()
      .height(68.dp)
      .padding(horizontal = 8.dp),
    horizontalArrangement = Arrangement.SpaceAround,
    verticalAlignment = Alignment.CenterVertically
  ) {
    items.forEach { item ->
      val selected = currentTab == item.tab

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .clickable { onTabSelected(item.tab) }
          .padding(horizontal = 14.dp, vertical = 6.dp)
          .testTag(item.testTag)
      ) {
        if (selected) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(MaterialTheme.colorScheme.secondaryContainer)
              .padding(horizontal = 12.dp, vertical = 4.dp)
          ) {
            Icon(
              imageVector = item.iconActive,
              contentDescription = item.labelArabic,
              tint = MaterialTheme.colorScheme.onSecondaryContainer,
              modifier = Modifier.size(22.dp)
            )
            Text(
              text = if (isEnglish) item.labelEnglish else item.labelArabic,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSecondaryContainer
            )
          }
        } else {
          Icon(
            imageVector = item.iconInactive,
            contentDescription = item.labelArabic,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
          )
          Text(
            text = if (isEnglish) item.labelEnglish else item.labelArabic,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}
