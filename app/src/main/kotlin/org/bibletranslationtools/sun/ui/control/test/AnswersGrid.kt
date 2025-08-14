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
import org.bibletranslationtools.sun.data.model.Answer
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.data.model.TestCard

@Composable
fun AnswerGrid(
    choices: List<TestCard>,
    onCardSelected: (Card) -> Unit,
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
        items(choices, key = { it.id }) { testCard ->
            when (testCard) {
                is Card -> SymbolCardItem(
                    card = testCard,
                    showStatusIcon = showStatusIcon,
                    onCardSelected = onCardSelected
                )
                is Answer -> AnswerResultItem(answer = testCard)
            }
        }
    }
}