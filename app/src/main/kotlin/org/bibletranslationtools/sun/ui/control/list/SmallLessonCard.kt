package org.bibletranslationtools.sun.ui.control.list

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.bibletranslationtools.sun.ui.model.LessonItem

@Composable
fun SmallLessonCard(
    lesson: LessonItem,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
    ) {
        Row {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "status"
            )
            Text(text = lesson.name)
            Text(text = lesson.author)

        }
    }
}