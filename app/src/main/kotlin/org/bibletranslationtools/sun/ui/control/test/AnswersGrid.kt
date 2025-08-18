package org.bibletranslationtools.sun.ui.control.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.bibletranslationtools.sun.ui.model.CardItem

@Composable
fun AnswerGrid(
    choices: List<CardItem>,
    answer: List<CardItem>,
    onCardSelected: (CardItem) -> Unit,
    modifier: Modifier = Modifier,
    showStatusIcon: Boolean = false
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            20.dp,
            Alignment.CenterHorizontally
        ),
        verticalArrangement = Arrangement.spacedBy(
            20.dp,
            Alignment.CenterVertically
        )
    ) {
        if (answer.isEmpty()) {
            items(choices, key = { it.id }) { choice ->
                TestSymbolCard(
                    card = choice,
                    showStatusIcon = showStatusIcon,
                    onCardSelected = onCardSelected
                )
            }
        } else {
            answer.forEach { item ->
                when (item.correct) {
                    false -> {
                        item { AnswerResultItem(isCorrect = false) }
                        item { TestSymbolCard(card = item) }
                    }
                    true -> {
                        item { AnswerResultItem(isCorrect = true) }
                        item { TestSymbolCard(card = item) }
                    }
                    else -> {}
                }
            }
        }
    }
}