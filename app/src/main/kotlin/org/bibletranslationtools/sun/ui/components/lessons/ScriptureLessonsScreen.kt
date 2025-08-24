package org.bibletranslationtools.sun.ui.components.lessons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.control.ConfirmDialog
import org.bibletranslationtools.sun.ui.control.TopAppBar
import org.bibletranslationtools.sun.ui.control.list.SmallLessonCard

@Composable
fun ScriptureLessonsScreen(component: ScriptureLessonsComponent) {

    val model by component.model.subscribeAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            onBackClick = component::onBackClick
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(R.drawable.book),
                contentDescription = "Lessons"
            )
            Text(
                text = stringResource(R.string.lessons),
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                LazyColumn {
                    items(model.lessons, key = { it.groupIdStr }) { lesson ->
                        SmallLessonCard(
                            group = lesson,
                            onClick = {
                                component.onLessonClick(lesson)
                            },
                            onDelete = {
                                component.onLessonDelete(lesson, false)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    model.lessonToDelete?.let { lesson ->
        ConfirmDialog(
            title = stringResource(R.string.delete_lesson_warning),
            message = stringResource(R.string.delete_lesson_continue),
            onDismiss = component::clearLessonToDelete,
            onCancel = component::clearLessonToDelete,
            onConfirm = { component.onLessonDelete(lesson, true) },
            confirmButtonText = stringResource(R.string.delete)
        )
    }
}