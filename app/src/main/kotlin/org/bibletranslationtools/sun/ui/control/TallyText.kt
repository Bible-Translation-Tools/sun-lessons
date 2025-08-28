package org.bibletranslationtools.sun.ui.control

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.bibletranslationtools.sun.ui.tallyFontFamily
import org.bibletranslationtools.sun.utils.TallyMarkConverter

@Composable
fun TallyText(lessonId: Int) {
    Text(
        text = TallyMarkConverter.toText(lessonId),
        fontWeight = FontWeight.Bold,
        fontFamily = tallyFontFamily(),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .widthIn(min = 24.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.extraSmall
            )
    )
}