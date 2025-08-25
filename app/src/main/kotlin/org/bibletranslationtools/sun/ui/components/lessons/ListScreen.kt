package org.bibletranslationtools.sun.ui.components.lessons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.control.TopAppBar
import org.bibletranslationtools.sun.ui.control.list.LessonCard

@Composable
fun ListScreen(component: ListComponent) {
    val model by component.model.subscribeAsState()

    var expandedLessonId by remember(model.selectedId) {
        mutableLongStateOf(model.selectedId)
    }

    val (topIcon, topText) = if (model.groupId?.isScripture != true) {
        R.drawable.home to R.string.home
    } else {
        R.drawable.book to R.string.lessons
    }

    val continueLessonText = if (model.nextState == SectionState.NOT_STARTED) {
        stringResource(R.string.start_lesson)
    } else {
        stringResource(R.string.continue_lesson)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            onBackClick = if (model.groupId?.isScripture == true) {
                component::onBackClick
            } else null
        ) {
            if (model.groupId?.isScripture == true) {
                Spacer(modifier = Modifier.weight(1f))
            }
            Icon(
                painter = painterResource(topIcon),
                contentDescription = "Lessons"
            )
            Text(
                text = stringResource(topText),
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Button(
                    enabled = model.lessons.isNotEmpty(),
                    onClick = component::onLearnClicked,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = continueLessonText)
                }

                LazyColumn {
                    items(model.lessons) { lesson ->
                        LessonCard(
                            lesson = lesson,
                            isExpanded = lesson.id == expandedLessonId,
                            onClick = {
                                expandedLessonId = if (expandedLessonId != lesson.id) {
                                    lesson.id
                                } else 0
                            },
                            onAction = {
                                component.onLessonAction(lesson.id, it)
                            }
                        )
                    }
                }
            }
        }
    }
}