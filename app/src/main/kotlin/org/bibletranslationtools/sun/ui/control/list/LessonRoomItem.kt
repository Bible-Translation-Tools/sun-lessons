package org.bibletranslationtools.sun.ui.control.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bibletranslationtools.sun.R

@Composable
fun LessonRoomItem(
    icon: Painter,
    text: String,
    status: LessonStatus,
    progress: Float,
    onClick: () -> Unit
) {
    val enabled = status != LessonStatus.LOCKED
    val color = if (enabled) {
        MaterialTheme.colorScheme.onBackground
    } else MaterialTheme.colorScheme.outline

    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(Color.White)
                .border(
                    width = 1.dp,
                    color = color,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                tint = color
            )
        }

        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier
                .weight(1f)
                .padding(start = 20.dp)
        )
        Box(contentAlignment = Alignment.Center) {
            when (status) {
                LessonStatus.IN_PROGRESS -> {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        strokeWidth = 3.dp,
                        gapSize = 0.dp
                    )
                }
                LessonStatus.LOCKED -> {
                    Icon(
                        painter = painterResource(R.drawable.lock),
                        contentDescription = "locked",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
                LessonStatus.COMPLETED -> {
                    Icon(
                        painter = painterResource(R.drawable.check),
                        contentDescription = "completed",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}