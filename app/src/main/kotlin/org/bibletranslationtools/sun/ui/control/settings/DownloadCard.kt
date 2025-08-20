package org.bibletranslationtools.sun.ui.control.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.bibletranslationtools.sun.ui.model.LessonItem

@Composable
fun DownloadCard(
    lesson: LessonItem,
    downloaded: Boolean,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = lesson.name,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = lesson.author ?: "unknown",
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
                .padding(horizontal = 8.dp)
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.width(36.dp)
        ) {
            if (downloaded) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "downloaded",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            } else {
                IconButton(onClick = onDownloadClick) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "download"
                    )
                }
            }
        }
    }
}