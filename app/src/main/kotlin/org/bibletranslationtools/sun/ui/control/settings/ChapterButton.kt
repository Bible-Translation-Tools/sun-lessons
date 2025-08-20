package org.bibletranslationtools.sun.ui.control.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChapterButton(
    chapter: Int,
    available: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (available) {
        MaterialTheme.colorScheme.onBackground
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    OutlinedButton(
        enabled = available,
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.outlineVariant
        ),
        border = BorderStroke(2.dp, borderColor),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier.aspectRatio(1f)
            .clip(MaterialTheme.shapes.small)
    ) {
        Text(
            text = chapter.toString(),
            fontSize = 16.sp,
            maxLines = 1
        )
    }
}