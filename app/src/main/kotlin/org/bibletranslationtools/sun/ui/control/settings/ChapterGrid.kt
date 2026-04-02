package org.bibletranslationtools.sun.ui.control.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChapterGrid(
    chapters: Int,
    onChapterClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val chapterNumbers = (1..chapters).toList()
    val chunkedChapters = chapterNumbers.chunked(5)

    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chunkedChapters.forEach { rowChapters ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowChapters.forEach { chapter ->
                        val isAvailable = listOf(1, 3, 15, 16, 18, 43, 117, 119).contains(chapter)
                        ChapterButton(
                            chapter = chapter,
                            available = isAvailable,
                            onClick = { onChapterClick(chapter) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    val remainingSpace = 5 - rowChapters.size
                    if (remainingSpace > 0) {
                        Spacer(modifier = Modifier.weight(remainingSpace.toFloat()))
                    }
                }
            }
        }
    }
}