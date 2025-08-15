package org.bibletranslationtools.sun.ui.control.test

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.model.SymbolItem
import org.bibletranslationtools.sun.ui.sunFontFamily

@Composable
fun TestSentenceCard(
    symbol: SymbolItem,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: (SymbolItem) -> Unit = {}
) {
    val (borderColor, icon) = when (symbol.correct) {
        true -> MaterialTheme.colorScheme.tertiary to R.drawable.check_outline
        false -> MaterialTheme.colorScheme.error to R.drawable.close_outline
        null -> (if (symbol.selected) {
            MaterialTheme.colorScheme.outline
        } else MaterialTheme.colorScheme.outlineVariant) to null
    }

    val boxModifier = if (icon != null) {
        modifier.padding(top = 10.dp, end = 10.dp)
    } else modifier

    Box(modifier = boxModifier.aspectRatio(1f)) {
        Surface(
            enabled = !symbol.selected && !enabled,
            onClick = { onClick(symbol) },
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(3.dp, borderColor),
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                BasicText(
                    text = symbol.name,
                    modifier = Modifier.fillMaxWidth()
                        .padding(4.dp)
                        .align(Alignment.Center),
                    style = TextStyle(
                        fontFamily = sunFontFamily(),
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 36.sp,
                        maxFontSize = 60.sp
                    )
                )
            }
        }

        if (icon != null) {
            Box(
                modifier = Modifier
                    .offset(x = (10).dp, y = -(10).dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = "correct",
                    tint = borderColor,
                    modifier = Modifier.fillMaxSize().padding(2.dp)
                )
            }
        }
    }
}