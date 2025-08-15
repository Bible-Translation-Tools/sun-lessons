package org.bibletranslationtools.sun.ui.control.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.bibletranslationtools.sun.ui.model.SymbolItem

private const val MAX_ITEMS_IN_ROW = 4

@Composable
fun DynamicGrid(
    symbols: List<SymbolItem>,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onItemClick: (SymbolItem) -> Unit = {},
    horizontalGap: Dp = 8.dp,
    verticalGap: Dp = 8.dp
) {
    if (symbols.size <= MAX_ITEMS_IN_ROW) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
        ) {
            symbols.forEach { symbol ->
                TestSentenceCard(
                    symbol = symbol,
                    enabled = enabled,
                    onClick = onItemClick,
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(MAX_ITEMS_IN_ROW),
            verticalArrangement = Arrangement.spacedBy(verticalGap),
            horizontalArrangement = Arrangement.spacedBy(horizontalGap),
            contentPadding = PaddingValues(verticalGap),
            modifier = modifier
        ) {
            items(symbols) { symbol ->
                TestSentenceCard(
                    symbol = symbol,
                    enabled = enabled,
                    onClick = onItemClick,
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}