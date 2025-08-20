package org.bibletranslationtools.sun.ui.components

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import org.bibletranslationtools.sun.ui.components.main.MainScreen
import org.bibletranslationtools.sun.ui.components.splash.SplashScreen
import org.bibletranslationtools.sun.utils.Utils

@Composable
fun RootScreen(component: RootComponent) {
    Children(
        stack = component.stack,
        animation = stackAnimation(Utils.slideHorizontally())
    ) {
        when (val child = it.instance) {
            is RootComponent.Child.Splash -> SplashScreen(child.component)

            is RootComponent.Child.Main ->
                MainScreen(child.component)
        }
    }
}