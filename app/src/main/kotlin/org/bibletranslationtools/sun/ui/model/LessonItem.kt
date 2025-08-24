package org.bibletranslationtools.sun.ui.model

import kotlinx.datetime.LocalDateTime

enum class DownloadStatus {
    DOWNLOAD,
    UPDATE,
    DONE
}

data class UniqueId(
    val book: String?,
    val chapter: Int?,
    val verse: Int?,
    val sort: Int,
    val author: String
)

data class LessonItem(
    val book: BookItem?,
    val chapter: Int?,
    val verse: Int?,
    val sort: Int,
    val author: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val downloadStatus: DownloadStatus = DownloadStatus.DONE,
    val downloadProgress: Float = -1f,
    val cards: List<CardItem> = emptyList(),
    val sentences: List<SentenceItem> = emptyList(),
    val isAvailable: Boolean = false,
    val isSelected: Boolean = false,
    val id: Long = 0
) {
    val name: String
        get() = if (isScripture) {
            "${book?.name} $chapter:$verse"
        } else {
            "Lesson $id"
        }

    val order: Int
        get() = if (isScripture) {
            sort
        } else {
            id.toInt()
        }

    val isScripture: Boolean
        get() = book != null && chapter != null && verse != null

    val fingerprint: String
        get() = "$name|$sort|$author"

    val uniqueId = UniqueId(
        book = book?.slug,
        chapter = chapter,
        verse = verse,
        sort = sort,
        author = author
    )

    val groupId = GroupId(
        book = book?.slug,
        chapter = chapter,
        verse = verse,
        author = author
    )

    val cardsLearned get() = cards.count { it.learned }
    val cardsLearnedProgress get() = cardsLearned.toFloat() / cards.size

    val cardsTested get() = cards.count { it.tested }
    val cardsTestedProgress get() = cardsTested.toFloat() / cards.size

    val sentencesLearned get() = sentences.count { it.learned }
    val sentencesLearnedProgress get() = run {
        // If there are no sentences, return 100% progress
        if (sentences.isNotEmpty()) {
            sentencesLearned.toFloat() / sentences.size
        } else {
            1f
        }
    }

    val sentencesTested get() = sentences.count { it.tested }
    val sentencesTestedProgress get() = run {
        // If there are no sentences, return 100% progress
        if (sentences.isNotEmpty()) {
            sentencesTested.toFloat() / sentences.size
        } else {
            1f
        }
    }

    val totalProgress: Float
        get() {
            // Size times 2, because we have learned and tested cards/sentences
            val total = (cards.size * 2) + (sentences.size * 2)
            val completed = cardsLearned + cardsTested + sentencesLearned + sentencesTested
            return if (total > 0) (completed.toFloat() / total) else 0f
        }
}
