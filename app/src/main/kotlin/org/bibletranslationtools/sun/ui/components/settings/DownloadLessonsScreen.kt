package org.bibletranslationtools.sun.ui.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SystemUpdateAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.control.ConfirmDialog
import org.bibletranslationtools.sun.ui.control.TopAppBar
import org.bibletranslationtools.sun.ui.control.settings.DownloadCard

@Composable
fun DownloadLessonsScreen(component: DownloadLessonsComponent) {

    val model by component.model.subscribeAsState()

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var filteredLessons by remember { mutableStateOf(model.lessons) }

    LaunchedEffect(searchQuery) {
        filteredLessons = model.lessons.filter { lesson ->
            lesson.verse.toString().contains(searchQuery, ignoreCase = true)
                    || lesson.author?.contains(searchQuery, ignoreCase = true) == true
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(onBackClick = component::onBackClick) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.SystemUpdateAlt,
                contentDescription = "Lessons"
            )
            Text(
                text = stringResource(R.string.downloads),
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(stringResource(R.string.search_verse_username))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "search"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "clear"
                                )
                            }
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "${model.bookItem.name} ${model.chapter}",
                    fontSize = 28.sp
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(filteredLessons) { index, lesson ->
                        DownloadCard(
                            lesson = lesson,
                            downloaded = index % 2 == 0,
                            onDownloadClick = {
                                component.onLessonSelected(lesson)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        model.selectedLesson?.let { filteredLessons ->
            ConfirmDialog(
                onDismiss = component::onLessonCanceled,
                onCancel = component::onLessonCanceled,
                onConfirm = component::onDownloadLessonClick,
                title = stringResource(R.string.internet_usage),
                message = "${stringResource(R.string.internet_usage_hint)}\n" +
                        stringResource(R.string.confirm_download),
                confirmButtonText = stringResource(R.string.download)
            )
        }
    }
}