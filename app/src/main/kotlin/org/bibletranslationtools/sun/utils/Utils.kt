package org.bibletranslationtools.sun.utils

import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import kotlinx.serialization.json.Json

object Utils {
    val JsonLenient = Json {
        isLenient = true
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    fun slideHorizontally(): StackAnimator {
        return slide(
            animationSpec = customTween(),
            orientation = Orientation.Horizontal
        )
    }

    fun slideVertically(): StackAnimator {
        return slide(
            animationSpec = customTween(),
            orientation = Orientation.Vertical
        )
    }

    private fun <T> customTween(): TweenSpec<T> {
        return tween(
            delayMillis = 20,
            durationMillis = 300,
            easing = EaseIn
        )
    }
}