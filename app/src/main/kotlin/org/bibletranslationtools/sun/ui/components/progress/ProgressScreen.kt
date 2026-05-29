package org.bibletranslationtools.sun.ui.components.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.control.TopAppBar
import org.bibletranslationtools.sun.ui.control.progress.LessonBox
import org.bibletranslationtools.sun.ui.control.progress.LessonsLearnedHeader
import org.bibletranslationtools.sun.ui.control.progress.ProgressSection
import org.bibletranslationtools.sun.ui.control.progress.ScreenHeader

@Composable
fun ProgressScreen(component: ProgressComponent) {
    val model by component.model.subscribeAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(onBackClick = component::onBackClick) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(R.drawable.sun_logo),
                    contentDescription = "logo"
                )
            }
        }

        ScreenHeader()

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { ProgressSection(model.lessons) }

            item { LessonsLearnedHeader() }

            val rows = model.lessons.chunked(5)

            items(rows) { rowLessons ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.widthIn(max = 500.dp)
                ) {
                    for (lesson in rowLessons) {
                        LessonBox(
                            lesson = lesson,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    }

                    val emptySlots = 5 - rowLessons.size
                    if (emptySlots > 0) {
                        repeat(emptySlots) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}