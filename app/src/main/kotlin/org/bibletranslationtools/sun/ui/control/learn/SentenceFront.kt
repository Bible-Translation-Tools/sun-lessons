package org.bibletranslationtools.sun.ui.control.learn

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.data.model.Symbol
import org.bibletranslationtools.sun.ui.sunFontFamily
import kotlin.math.ceil
import kotlin.math.max

@Composable
fun SentenceFront(symbols: List<Symbol>, onFlip: () -> Unit) {

    val symbolsCount = symbols.size
    val maxSymbols = 4

    val text = if (symbolsCount > maxSymbols) {
        val symbols = symbols.map { it.name }
        val builder = StringBuilder()
        var counter = 1
        for (symbol in symbols) {
            builder.append(symbol)
            if (counter == maxSymbols) {
                builder.append("\n")
                counter = 1
            } else {
                builder.append(" ")
                counter += 1
            }
        }
        builder.toString()
    } else {
        symbols.joinToString(" ") { it.name }
    }

    val maxLines = max(
        ceil(symbolsCount / maxSymbols.toDouble()).toInt(),
        1
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp)
            .clickable(
                onClick = onFlip,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        BasicText(
            text = text,
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.Center),
            style = TextStyle(
                fontFamily = sunFontFamily(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 78.sp
            ),
            maxLines = maxLines,
            autoSize = TextAutoSize.StepBased(
                minFontSize = 36.sp,
                maxFontSize = 220.sp
            )
        )
        OutlinedButton(
            onClick = onFlip,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(40.dp)
                .align(Alignment.BottomCenter),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_eye_open),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = stringResource(id = R.string.see_answer))
        }
    }
}