package org.bibletranslationtools.sun.ui.control.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.bibletranslationtools.sun.ui.model.LessonItem
import org.bibletranslationtools.sun.utils.Section

enum class LessonStatus {
    LOCKED,
    IN_PROGRESS,
    COMPLETED
}

@Composable
fun LessonCard(
    lesson: LessonItem,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onAction: (Section) -> Unit
) {
    val backgroundColor = if (isExpanded) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    } else MaterialTheme.colorScheme.background
    val borderColor = if (isExpanded) {
        MaterialTheme.colorScheme.primary
    } else MaterialTheme.colorScheme.outlineVariant

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 10.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor)
            .border(1.dp, borderColor, MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        LessonHeader(
            lesson = lesson,
            isExpanded = isExpanded,
            onClick = onClick
        )

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = tween(durationMillis = 500)
            )
        ) {
            LessonRooms(
                lesson = lesson,
                onAction = onAction
            )
        }
    }
}