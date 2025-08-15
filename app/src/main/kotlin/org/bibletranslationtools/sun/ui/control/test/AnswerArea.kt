package org.bibletranslationtools.sun.ui.control.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import org.bibletranslationtools.sun.ui.components.lessons.TestSentenceComponent
import org.bibletranslationtools.sun.ui.model.SymbolItem

@Composable
fun AnswerArea(
    state: TestSentenceComponent.Model,
    showCorrectAnswer: Boolean,
    modifier: Modifier = Modifier,
    onSymbolSelected: (SymbolItem) -> Unit
) {
    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        val slotSize = min((maxWidth / state.answerSlots.size) - 8.dp, 80.dp)

        println(maxWidth)
        println(state.answerSlots.size)
        println(maxWidth / state.answerSlots.size)
        println(slotSize)

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            modifier = modifier.fillMaxSize()
        ) {
            when {
                !state.questionDone -> {
                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        state.answerSlots.forEach { symbol ->
                            TestSentenceCard(
                                symbol = symbol,
                                enabled = true,
                                modifier = Modifier.size(slotSize)
                                    .aspectRatio(1f)
                            )
                        }
                    }

                    DynamicGrid(
                        symbols = state.optionChoices,
                        enabled = false,
                        onItemClick = onSymbolSelected,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                state.isCorrect || showCorrectAnswer -> {
                    ResultDisplay(
                        isCorrect = true,
                        symbols = state.correctAnswer
                    )
                }
                else -> {
                    ResultDisplay(
                        isCorrect = false,
                        symbols = state.answer
                    )
                }
            }
        }
    }
}