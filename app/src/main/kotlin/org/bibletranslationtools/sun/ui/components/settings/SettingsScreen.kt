package org.bibletranslationtools.sun.ui.components.settings

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import org.bibletranslationtools.sun.utils.Utils

@Composable
fun SettingsScreen(component: SettingsComponent) {
    Children(
        stack = component.stack,
        animation = stackAnimation(Utils.slideHorizontally())
    ) {
        when (val child = it.instance) {
            is SettingsComponent.Child.SettingsList ->
                SettingsListScreen(child.component)
            is SettingsComponent.Child.SelectChapter ->
                SelectChapterScreen(child.component)
            is SettingsComponent.Child.DownloadLessons ->
                DownloadLessonsScreen(child.component)
            is SettingsComponent.Child.UpdateLessons ->
                UpdateLessonsScreen(child.component)
        }
    }
}