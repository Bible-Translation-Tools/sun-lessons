package org.bibletranslationtools.sun.ui.control

import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.bibletranslationtools.sun.ui.components.RootComponent
import org.bibletranslationtools.sun.ui.navigation.MainTab

@Composable
fun BottomNavBar(currentTab: RootComponent.Child, onTabSelected: (MainTab) -> Unit) {
    val tabs = MainTab.entries
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .height(80.dp)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = strokeWidth
                )
            }
    ) {
        tabs.forEach { tab ->
            val isSelected = when (currentTab) {
                is RootComponent.Child.Home -> tab == MainTab.Home
                is RootComponent.Child.Progress -> tab == MainTab.Progress
                is RootComponent.Child.Lessons -> tab == MainTab.Lessons
                is RootComponent.Child.Settings -> tab == MainTab.Settings
            }
            NavigationItem(
                label = stringResource(tab.title),
                icon = painterResource(tab.icon),
                isSelected = isSelected,
                onClick = { onTabSelected(tab) }
            )
        }
    }
}
