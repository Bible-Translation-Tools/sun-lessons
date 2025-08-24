package org.bibletranslationtools.sun.ui.control.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bibletranslationtools.sun.ui.model.LessonItem
import org.bibletranslationtools.sun.ui.tallyFontFamily
import org.bibletranslationtools.sun.utils.TallyMarkConverter

@Composable
fun LessonBox(
    lesson: LessonItem,
    modifier: Modifier = Modifier
) {
    val (borderColor, backgroundColor) = if (lesson.isAvailable) {
        MaterialTheme.colorScheme.primary
            .copy(alpha = 0.3f) to MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.outline
            .copy(alpha = 0.5f) to MaterialTheme.colorScheme.surfaceVariant
    }

    Box(
        modifier = modifier.aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.medium
            )
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (lesson.isAvailable) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = lesson.id.toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = TallyMarkConverter.toText(lesson.part),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = tallyFontFamily()
                )
            }
        } else {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "lock",
                tint = borderColor
            )
        }
    }
}