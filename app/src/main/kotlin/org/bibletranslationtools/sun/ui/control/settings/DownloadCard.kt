package org.bibletranslationtools.sun.ui.control.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bibletranslationtools.sun.ui.model.LessonItem

enum class DownloadStatus {
    NOT_STARTED,
    HAS_UPDATE,
    COMPLETED
}

@Composable
fun DownloadCard(
    lesson: LessonItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    status: DownloadStatus = DownloadStatus.NOT_STARTED,
    progress: Float = -1f
) {
    val icon = when (status) {
        DownloadStatus.NOT_STARTED -> Icons.Default.Download
        DownloadStatus.HAS_UPDATE -> Icons.Default.RestartAlt
        DownloadStatus.COMPLETED -> Icons.Default.Check
    }

    val (color, enabled) = if (status == DownloadStatus.COMPLETED) {
        MaterialTheme.colorScheme.tertiary to false
    } else {
        Color.Unspecified to (progress < 0)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = lesson.name,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = lesson.author,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
                    .padding(horizontal = 8.dp)
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.width(40.dp)
            ) {
                IconButton(
                    onClick = onClick,
                    enabled = enabled
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "download",
                        tint = color
                    )
                }
            }
        }

        if (progress >= 0) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    gapSize = 0.dp,
                    drawStopIndicator = {},
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 12.sp
                )
            }
        }
    }
}