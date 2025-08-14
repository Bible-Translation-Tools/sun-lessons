package org.bibletranslationtools.sun.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.bibletranslationtools.sun.ui.components.home.HomeScreen
import org.bibletranslationtools.sun.ui.components.lessons.LessonsScreen
import org.bibletranslationtools.sun.ui.components.progress.ProgressScreen
import org.bibletranslationtools.sun.ui.components.settings.SettingsScreen
import org.bibletranslationtools.sun.ui.control.BottomNavBar

@Composable
fun RootScreen(component: RootComponent) {
    val childStack by component.stack.subscribeAsState()
    val activeChild = childStack.active.instance

    val topBarContent by component.topBarSlot.subscribeAsState()

    Scaffold(
        topBar = {
            topBarContent()
        },
        bottomBar = {
            BottomNavBar(activeChild) { tab ->
                component.onTabClicked(tab)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Children(
                stack = component.stack,
                animation = stackAnimation(slide())
            ) {
                when (val child = it.instance) {
                    is RootComponent.Child.Home -> HomeScreen(child.component)
                    is RootComponent.Child.Progress -> ProgressScreen(child.component)
                    is RootComponent.Child.Lessons -> LessonsScreen(child.component)
                    is RootComponent.Child.Settings -> SettingsScreen(child.component)
                }
            }
        }
    }
}