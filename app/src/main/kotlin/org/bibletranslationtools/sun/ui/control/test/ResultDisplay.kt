package org.bibletranslationtools.sun.ui.control.test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.model.SymbolItem

@Composable
fun ResultDisplay(isCorrect: Boolean, symbols: List<SymbolItem>) {

    val correct = stringResource(R.string.correct)
    val incorrect = stringResource(R.string.incorrect)

    val (icon, text, color) = if (isCorrect) {
        Triple(
            Icons.Default.Check,
            correct,
            MaterialTheme.colorScheme.tertiary
        )
    } else {
        Triple(
            Icons.Default.Close,
            incorrect,
            MaterialTheme.colorScheme.error
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text.uppercase(),
                color = color,
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        DynamicGrid(
            symbols = symbols,
            enabled = true,
            horizontalGap = 8.dp,
            verticalGap = 8.dp
        )
    }
}