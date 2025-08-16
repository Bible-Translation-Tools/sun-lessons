package org.bibletranslationtools.sun.ui.control.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.model.LessonItem
import org.bibletranslationtools.sun.ui.tallyFontFamily
import org.bibletranslationtools.sun.utils.TallyMarkConverter

@Composable
fun LessonHeader(
    lesson: LessonItem,
    isExpanded: Boolean,
    onClick: () -> Unit
) {

    val status = when {
        lesson.isAvailable && lesson.totalProgress == 100.0 -> LessonStatus.COMPLETED
        lesson.isAvailable && lesson.totalProgress < 100.0 -> LessonStatus.IN_PROGRESS
        else -> LessonStatus.LOCKED
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .clickable(
                enabled = lesson.isAvailable,
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.lesson_name, lesson.lesson.id),
                fontSize = 24.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = if (isExpanded) FontWeight.Bold else FontWeight.Normal,
                color = if (status == LessonStatus.LOCKED) {
                    MaterialTheme.colorScheme.outline
                } else MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = TallyMarkConverter.toText(lesson.lesson.id),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = tallyFontFamily(),
                color = if (status == LessonStatus.LOCKED) {
                    MaterialTheme.colorScheme.outline
                } else MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .graphicsLayer(scaleX = 1.5f, scaleY = 1f)
            )
        }

        if (!isExpanded) {
            Box(contentAlignment = Alignment.Center) {
                when (status) {
                    LessonStatus.IN_PROGRESS -> {
                        CircularProgressIndicator(
                            progress = { lesson.totalProgress.toFloat() / 100 },
                            modifier = Modifier.size(24.dp),
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
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    LessonStatus.COMPLETED -> {
                        Icon(
                            painter = painterResource(R.drawable.check),
                            contentDescription = "completed",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }
        }
    }
}