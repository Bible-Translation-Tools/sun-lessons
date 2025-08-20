package org.bibletranslationtools.sun.ui.components.lessons

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import org.bibletranslationtools.sun.utils.Utils

@Composable
fun LessonsScreen(component: LessonsComponent) {
    Children(
        stack = component.stack,
        animation = stackAnimation(Utils.slideHorizontally())
    ) {
        when (val child = it.instance) {
            is LessonsComponent.Child.List ->
                ListScreen(child.component)
            is LessonsComponent.Child.Start ->
                StartScreen(child.component)
            is LessonsComponent.Child.LearnSymbol ->
                LearnSymbolScreen(child.component)
            is LessonsComponent.Child.TestSymbol ->
                TestSymbolScreen(child.component)
            is LessonsComponent.Child.LearnSentence ->
                LearnSentenceScreen(child.component)
            is LessonsComponent.Child.TestSentence ->
                TestSentenceScreen(child.component)
            is LessonsComponent.Child.Complete ->
                CompleteScreen(child.component)
        }
    }
}