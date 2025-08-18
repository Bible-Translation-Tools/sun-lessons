package org.bibletranslationtools.sun.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.bibletranslationtools.sun.ui.components.home.HomeScreen
import org.bibletranslationtools.sun.ui.components.lessons.LessonsComponent
import org.bibletranslationtools.sun.ui.components.lessons.LessonsScreen
import org.bibletranslationtools.sun.ui.components.progress.ProgressScreen
import org.bibletranslationtools.sun.ui.components.settings.SettingsScreen
import org.bibletranslationtools.sun.ui.control.BottomNavBar
import org.bibletranslationtools.sun.utils.Utils

@Composable
fun RootScreen(component: RootComponent) {
    val childStack by component.stack.subscribeAsState()
    val activeChild = childStack.active.instance

    val showBottomBar = when (val child = activeChild) {
        is RootComponent.Child.Home,
        is RootComponent.Child.Progress,
        is RootComponent.Child.Settings -> true
        is RootComponent.Child.Lessons -> {
            val lessonsStack by child.component.stack.subscribeAsState()
            lessonsStack.active.instance is LessonsComponent.Child.List
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(activeChild) { tab ->
                    component.onTabClicked(tab)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Children(
            stack = component.stack,
            animation = stackAnimation(Utils.slideHorizontally())
        ) {
            when (val child = it.instance) {
                is RootComponent.Child.Home ->
                    HomeScreen(child.component, paddingValues)
                is RootComponent.Child.Progress ->
                    ProgressScreen(child.component, paddingValues)
                is RootComponent.Child.Lessons ->
                    LessonsScreen(child.component, paddingValues)
                is RootComponent.Child.Settings ->
                    SettingsScreen(child.component, paddingValues)
            }
        }
    }
}