package org.bibletranslationtools.sun.utils

import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

object Utils {
    val JsonLenient = Json {
        isLenient = true
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @OptIn(ExperimentalTime::class)
    fun getCurrentTime() =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    @OptIn(ExperimentalTime::class)
    fun getCurrentTimestamp() = Clock.System.now().epochSeconds

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

    fun getRandomUuid(): String {
        return UUID.randomUUID().toString()
    }

    private fun <T> customTween(): TweenSpec<T> {
        return tween(
            delayMillis = 20,
            durationMillis = 300,
            easing = EaseIn
        )
    }
}