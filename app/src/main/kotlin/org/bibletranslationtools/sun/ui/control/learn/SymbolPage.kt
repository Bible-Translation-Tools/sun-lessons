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
import org.bibletranslationtools.sun.ui.model.CardItem

@Composable
fun SymbolPage(
    card: CardItem,
    onFrontFlipped: (CardItem) -> Unit
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
            SymbolFront(card = card) {
                isFlipped = true
                onFrontFlipped(card)
            }
        } else {
            CardBack(
                card = card,
                modifier = Modifier.graphicsLayer { rotationY = 180f }
            ) {
                isFlipped = false
            }
        }
    }
}