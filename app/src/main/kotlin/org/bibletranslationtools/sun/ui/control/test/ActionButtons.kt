package org.bibletranslationtools.sun.ui.control.test

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.bibletranslationtools.sun.R

@Composable
private fun NextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "next"
        )
    }
}

@Composable
fun ActionButtons(
    isCorrect: Boolean,
    questionDone: Boolean,
    showCorrectAnswer: Boolean,
    onShowCorrectAnswer: (Boolean?) -> Unit,
    onNextSentence: () -> Unit
) {
    var correctAnswerRevealed by remember { mutableStateOf(false) }

    if (questionDone) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(50.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (isCorrect) {
                true -> {
                    NextButton(modifier = Modifier.fillMaxWidth()) {
                        correctAnswerRevealed = false
                        onShowCorrectAnswer(false)
                        onNextSentence()
                    }
                }
                false -> {
                    val (text, icon) = if (showCorrectAnswer) {
                        R.string.hide to Icons.Outlined.VisibilityOff
                    } else R.string.show to Icons.Outlined.Visibility

                    OutlinedButton(
                        onClick = {
                            correctAnswerRevealed = true
                            onShowCorrectAnswer(null)
                        },
                        shape = MaterialTheme.shapes.medium,
                        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = stringResource(text),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(stringResource(text).uppercase())
                    }

                    if (correctAnswerRevealed) {
                        NextButton(modifier = Modifier.weight(1f)) {
                            correctAnswerRevealed = false
                            onShowCorrectAnswer(false)
                            onNextSentence()
                        }
                    }
                }
            }
        }
    }
}