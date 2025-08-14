package org.bibletranslationtools.sun.ui.components.lessons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.control.list.LessonCard
import org.bibletranslationtools.sun.ui.control.TopAppBar

@Composable
fun ListScreen(component: ListComponent) {
    val model by component.model.subscribeAsState()

    var expandedLessonId by rememberSaveable {
        mutableIntStateOf(model.selectedId)
    }

    LaunchedEffect(Unit) {
        component.setTopAppBar {
            TopAppBar(onBackClick = component::onBackClick) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(R.drawable.book),
                    contentDescription = "Lessons"
                )
                Text(
                    text = "Lessons",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        LazyColumn {
            items(model.lessons) { lesson ->
                LessonCard(
                    lesson = lesson,
                    isExpanded = lesson.lesson.id == expandedLessonId,
                    onClick = {
                        expandedLessonId = if (expandedLessonId != lesson.lesson.id) {
                            lesson.lesson.id
                        } else 0
                    },
                    onAction = {
                        component.onLessonAction(lesson.lesson.id, it)
                    }
                )
            }
        }
    }
}