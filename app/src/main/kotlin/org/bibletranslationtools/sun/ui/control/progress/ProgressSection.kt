package org.bibletranslationtools.sun.ui.control.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.model.LessonItem

@Composable
fun ProgressSection(lessons: List<LessonItem>) {

    val scoreProgress = if (lessons.isNotEmpty()) {
        lessons.sumOf { it.totalProgress }.toFloat() / lessons.size
    } else 0f

    val learnedCount = lessons.sumOf { it.cardsLearned }.toString()
    val learnedProgress = if (lessons.isNotEmpty()) {
        lessons.sumOf { it.cardsLearnedProgress }.toFloat() / lessons.size
    } else 0f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProgressIndicatorItem(
            label = stringResource(id = R.string.symbols_learned),
            progress = learnedProgress,
            count = learnedCount
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .width(4.dp)
                .height(100.dp)
                .background(color = MaterialTheme.colorScheme.outline)
        )

        Spacer(modifier = Modifier.weight(1f))

        ProgressIndicatorItem(
            label = stringResource(id = R.string.test_score),
            progress = scoreProgress,
            count = "${scoreProgress.toInt()}%"
        )
    }
}