package org.bibletranslationtools.sun.ui.control.learn

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.model.CardItem
import org.bibletranslationtools.sun.ui.sunFontFamily

@Composable
fun SymbolFront(card: CardItem, onFlip: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp)
            .clickable(
                onClick = onFlip,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        Text(
            text = card.symbol,
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                fontFamily = sunFontFamily(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 250.sp
            )
        )
        OutlinedButton(
            onClick = onFlip,
            modifier = Modifier
                .height(40.dp)
                .align(Alignment.BottomCenter),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Outlined.Visibility,
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = stringResource(id = R.string.see_answer))
        }
    }
}