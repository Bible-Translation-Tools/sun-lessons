package org.bibletranslationtools.sun.ui.components.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import kotlinx.coroutines.delay
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.control.TopAppBar
import org.bibletranslationtools.sun.ui.control.settings.BookRow
import org.bibletranslationtools.sun.ui.control.settings.ChapterGrid
import org.bibletranslationtools.sun.ui.control.settings.TestamentItem

@Composable
fun SelectChapterScreen(component: SelectChapterComponent) {

    val model by component.model.subscribeAsState()

    val lazyListState = rememberLazyListState()
    var expandedBookIndex by rememberSaveable { mutableIntStateOf(0) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var filteredBooks by remember { mutableStateOf(model.books) }

    LaunchedEffect(searchQuery) {
        filteredBooks = model.books.filter { book ->
            book.name.contains(searchQuery, ignoreCase = true)
                    || book.slug.contains(searchQuery, ignoreCase = true)
        }
    }

    LaunchedEffect(expandedBookIndex) {
        if (expandedBookIndex != -1) {
            delay(200)
            val visibleItem = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull {
                it.index == expandedBookIndex
            }
            if (visibleItem != null) {
                lazyListState.animateScrollBy(
                    value = visibleItem.offset.toFloat(),
                    animationSpec = tween(500)
                )
            } else {
                lazyListState.animateScrollToItem(expandedBookIndex)
            }
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
            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(stringResource(R.string.search_book_name)) },
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
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState
            ) {
                itemsIndexed(filteredBooks) { index, book ->
                    val isAvailable = !listOf(3, 15, 40, 43).contains(index)
                    when (book.slug) {
                        "gen" -> TestamentItem(R.string.old_testament)
                        "mat" -> TestamentItem(R.string.new_testament)
                    }
                    BookRow(
                        book = book,
                        available = isAvailable,
                        isExpanded = isAvailable && expandedBookIndex == index,
                        onToggle = {
                            expandedBookIndex = if (expandedBookIndex == index) -1 else index
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    AnimatedVisibility(
                        visible = isAvailable && expandedBookIndex == index,
                        enter = expandVertically(
                            expandFrom = Alignment.Top,
                            animationSpec = tween(durationMillis = 500)
                        )
                    ) {
                        ChapterGrid(
                            chapters = book.chapters,
                            modifier = Modifier.fillMaxWidth(),
                            onChapterClick = { chapter ->
                                component.onChapterSelected(book, chapter)
                            }
                        )
                    }
                }
            }
        }
    }
}