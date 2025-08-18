package org.bibletranslationtools.sun.ui.control.test

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.model.CardItem
import org.bibletranslationtools.sun.ui.sunFontFamily

@Composable
fun TestSymbolCard(
    card: CardItem,
    modifier: Modifier = Modifier,
    showStatusIcon: Boolean = false,
    onCardSelected: (CardItem) -> Unit = {}
) {
    val cardStateColor = when (card.correct) {
        true -> MaterialTheme.colorScheme.tertiary
        false -> MaterialTheme.colorScheme.error
        null -> MaterialTheme.colorScheme.outline
    }

    val boxModifier = if (showStatusIcon) {
        modifier.padding(top = 12.dp, end = 12.dp)
    } else modifier

    Box(
        modifier = boxModifier.height(108.dp),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            onClick = { onCardSelected(card) },
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(3.dp, cardStateColor),
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = card.symbol,
                    fontSize = 80.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = sunFontFamily()
                )
            }
        }

        if (showStatusIcon) {
            val icon = if (card.correct == true) {
                painterResource(R.drawable.check_outline)
            } else painterResource(R.drawable.close_outline)

            Box(
                modifier = Modifier
                    .offset(x = (12).dp, y = -(12).dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .padding(0.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = "correct",
                    tint = cardStateColor,
                    modifier = Modifier.fillMaxSize().padding(2.dp)
                )
            }
        }
    }
}