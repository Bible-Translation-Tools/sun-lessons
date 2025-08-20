package org.bibletranslationtools.sun.ui.components.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.bibletranslationtools.sun.ui.components.lessons.LessonsComponent
import org.bibletranslationtools.sun.ui.components.lessons.LessonsScreen
import org.bibletranslationtools.sun.ui.components.progress.ProgressScreen
import org.bibletranslationtools.sun.ui.components.settings.SettingsScreen
import org.bibletranslationtools.sun.ui.control.BottomNavBar
import org.bibletranslationtools.sun.utils.Utils

@Composable
fun MainScreen(component: MainComponent) {
    val childStack by component.stack.subscribeAsState()
    val activeChild = childStack.active.instance

    val showBottomBar = when (val child = activeChild) {
        is MainComponent.Child.Home -> {
            val lessonsStack by child.component.stack.subscribeAsState()
            lessonsStack.active.instance is LessonsComponent.Child.List
        }
        is MainComponent.Child.Progress,
        is MainComponent.Child.Settings -> true
        is MainComponent.Child.Lessons -> {
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
            animation = stackAnimation(Utils.slideHorizontally()),
            modifier = Modifier.padding(paddingValues)
        ) {
            when (val child = it.instance) {
                is MainComponent.Child.Home -> LessonsScreen(child.component)
                is MainComponent.Child.Progress -> ProgressScreen(child.component)
                is MainComponent.Child.Lessons -> LessonsScreen(child.component)
                is MainComponent.Child.Settings -> SettingsScreen(child.component)
            }
        }
    }
}