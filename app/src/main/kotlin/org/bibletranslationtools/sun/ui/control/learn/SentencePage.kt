package org.bibletranslationtools.sun.ui.control.learn

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import org.bibletranslationtools.sun.ui.model.SentenceItem

@Composable
fun SentencePage(
    sentence: SentenceItem,
    onFrontFlipped: (SentenceItem) -> Unit
) {
    var isFlipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "flipRotation"
    )

    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8 * density
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if (rotation <= 90f) {
            SentenceFront(symbols = sentence.symbols) {
                isFlipped = true
                onFrontFlipped(sentence)
            }
        } else {
            SentenceBack(
                sentence = sentence,
                modifier = Modifier.graphicsLayer { rotationY = 180f }
            ) {
                isFlipped = false
            }
        }
    }
}