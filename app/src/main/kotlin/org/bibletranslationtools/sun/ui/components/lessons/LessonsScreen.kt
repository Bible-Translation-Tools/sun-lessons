package org.bibletranslationtools.sun.ui.components.lessons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import org.bibletranslationtools.sun.utils.Utils

@Composable
fun LessonsScreen(component: LessonsComponent, paddingValues: PaddingValues) {
    Children(
        stack = component.stack,
        animation = stackAnimation(Utils.slideHorizontally())
    ) {
        when (val child = it.instance) {
            is LessonsComponent.Child.List ->
                ListScreen(child.component, paddingValues)
            is LessonsComponent.Child.Start ->
                StartScreen(child.component, paddingValues)
            is LessonsComponent.Child.LearnSymbol ->
                LearnSymbolScreen(child.component, paddingValues)
            is LessonsComponent.Child.TestSymbol ->
                TestSymbolScreen(child.component, paddingValues)
            is LessonsComponent.Child.LearnSentence ->
                LearnSentenceScreen(child.component, paddingValues)
            is LessonsComponent.Child.TestSentence ->
                TestSentenceScreen(child.component, paddingValues)
            is LessonsComponent.Child.Complete ->
                CompleteScreen(child.component, paddingValues)
        }
    }
}