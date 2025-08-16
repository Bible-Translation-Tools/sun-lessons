package org.bibletranslationtools.sun.ui.control.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.model.LessonItem
import org.bibletranslationtools.sun.utils.Section

@Composable
fun LessonRooms(
    lesson: LessonItem,
    onAction: (Section) -> Unit
) {

    val cardsLearnedProgress = lesson.cardsLearnedProgress
    val testSymbolsAvailable = cardsLearnedProgress == 100.0
    val cardsTestedProgress = lesson.cardsTestedProgress
    val learnSentencesAvailable = cardsTestedProgress == 100.0
    val sentencesLearnedProgress = lesson.sentencesLearnedProgress
    val sentencesTestedProgress = lesson.sentencesTestedProgress
    val testSentencesAvailable = sentencesLearnedProgress == 100.0
    val hasSentences = lesson.sentences.isNotEmpty()

    val learnSymbolsStatus = if (cardsLearnedProgress == 100.0) {
        LessonStatus.COMPLETED
    } else LessonStatus.IN_PROGRESS

    val testSymbolsStatus = when {
        testSymbolsAvailable && cardsTestedProgress == 100.0 -> LessonStatus.COMPLETED
        testSymbolsAvailable && cardsTestedProgress < 100.0 -> LessonStatus.IN_PROGRESS
        else -> LessonStatus.LOCKED
    }

    val learnSentencesStatus = when {
        !hasSentences -> null
        learnSentencesAvailable && sentencesLearnedProgress == 100.0 -> LessonStatus.COMPLETED
        learnSentencesAvailable && sentencesLearnedProgress < 100.0 -> LessonStatus.IN_PROGRESS
        else -> LessonStatus.LOCKED
    }

    val testSentencesStatus = when {
        !hasSentences -> null
        testSentencesAvailable && sentencesTestedProgress == 100.0 -> LessonStatus.COMPLETED
        testSentencesAvailable && sentencesTestedProgress < 100.0 -> LessonStatus.IN_PROGRESS
        else -> LessonStatus.LOCKED
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Spacer(modifier = Modifier.height(20.dp))
        LessonRoomItem(
            icon = painterResource(id = R.drawable.learn),
            text = stringResource(id = R.string.learn_symbols),
            status = learnSymbolsStatus,
            progress = cardsLearnedProgress,
            onClick = { onAction(Section.LEARN_SYMBOLS) }
        )
        LessonRoomItem(
            icon = painterResource(id = R.drawable.test),
            text = stringResource(id = R.string.test_symbols),
            status = testSymbolsStatus,
            progress = cardsTestedProgress,
            onClick = { onAction(Section.TEST_SYMBOLS) }
        )
        if (learnSentencesStatus != null) {
            LessonRoomItem(
                icon = painterResource(id = R.drawable.learn),
                text = stringResource(id = R.string.learn_sentences),
                status = learnSentencesStatus,
                progress = sentencesLearnedProgress,
                onClick = { onAction(Section.LEARN_SENTENCES) }
            )
        }
        if (testSentencesStatus != null) {
            LessonRoomItem(
                icon = painterResource(id = R.drawable.test),
                text = stringResource(id = R.string.test_sentences),
                status = testSentencesStatus,
                progress = sentencesTestedProgress,
                onClick = { onAction(Section.TEST_SENTENCES) }
            )
        }
    }
}