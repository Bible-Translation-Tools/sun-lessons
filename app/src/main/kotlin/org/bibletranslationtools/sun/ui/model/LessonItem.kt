package org.bibletranslationtools.sun.ui.model

import org.bibletranslationtools.sun.data.model.CardEntity
import org.bibletranslationtools.sun.data.model.LessonEntity
import org.bibletranslationtools.sun.data.model.LessonWithData
import org.bibletranslationtools.sun.data.model.SentenceEntity
import java.util.Objects

data class LessonItem(
    val lesson: LessonEntity,
    val cards: List<CardEntity>,
    val sentences: List<SentenceEntity>,
    val isAvailable: Boolean,
    val isSelected: Boolean
) {
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val lessonItem = other as LessonItem
        return lesson == lessonItem.lesson &&
                totalProgress == lessonItem.totalProgress &&
                isAvailable == lessonItem.isAvailable &&
                isSelected == lessonItem.isSelected
    }

    override fun hashCode(): Int {
        return Objects.hash(lesson, totalProgress, isAvailable, isSelected)
    }
}

fun LessonWithData.toItem(): LessonItem {
    return LessonItem(
        lesson = lesson,
        cards = cards,
        sentences = sentences,
        isAvailable = false,
        isSelected = false
    )
}