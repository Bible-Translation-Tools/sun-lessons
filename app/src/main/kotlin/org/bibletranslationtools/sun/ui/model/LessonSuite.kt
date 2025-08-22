package org.bibletranslationtools.sun.ui.model

import org.bibletranslationtools.sun.data.model.LessonWithData

enum class DownloadStatus {
    DOWNLOAD,
    UPDATE,
    DONE
}

data class LessonSuite(
    val lesson: LessonItem,
    val cards: List<CardItem>,
    val sentences: List<SentenceItem>,
    val isAvailable: Boolean = false,
    val isSelected: Boolean = false,
    val downloadStatus: DownloadStatus = DownloadStatus.DONE,
    val downloadProgress: Float = -1f
) {
    val fingerprint = lesson.fingerprint

    val cardsLearned get() = cards.count { it.learned }
    val cardsLearnedProgress get() = cardsLearned.toDouble() / cards.size * 100

    val cardsTested get() = cards.count { it.tested }
    val cardsTestedProgress get() = cardsTested.toDouble() / cards.size * 100

    val sentencesLearned get() = sentences.count { it.learned }
    val sentencesLearnedProgress get() = run {
        // If there are no sentences, return 100% progress
        if (sentences.isNotEmpty()) {
            sentencesLearned.toDouble() / sentences.size * 100
        } else {
            100.0
        }
    }

    val sentencesTested get() = sentences.count { it.tested }
    val sentencesTestedProgress get() = run {
        // If there are no sentences, return 100% progress
        if (sentences.isNotEmpty()) {
            sentencesTested.toDouble() / sentences.size * 100
        } else {
            100.0
        }
    }

    val totalProgress: Double
        get() {
            // Size times 2, because we have learned and tested cards/sentences
            val total = (cards.size * 2) + (sentences.size * 2)
            val completed = cardsLearned + cardsTested + sentencesLearned + sentencesTested
            return (completed.toDouble() / total) * 100
        }
}

fun LessonWithData.toItem(): LessonSuite {
    return LessonSuite(
        lesson = lesson.toItem(),
        cards = cards.map { it.toItem() },
        sentences = sentences.map { it.toItem() }
    )
}