package org.bibletranslationtools.sun.ui.model

import kotlinx.datetime.LocalDateTime
import org.bibletranslationtools.sun.data.model.LessonEntity
import org.bibletranslationtools.sun.utils.toLocalDateTime
import org.bibletranslationtools.sun.utils.toTimestamp

enum class LessonType {
    BASIC,
    SCRIPTURE
}

data class UniqueId(
    val book: String?,
    val chapter: Int?,
    val verse: Int?,
    val sort: Int,
    val author: String
)

data class LessonItem(
    val book: String?,
    val chapter: Int?,
    val verse: Int?,
    val sort: Int,
    val author: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val id: Long = 0
) {
    val name: String
        get() = if (type == LessonType.SCRIPTURE) {
            "$book $chapter:$verse"
        } else {
            "Lesson $id"
        }

    val type: LessonType
        get() = if (book == null || chapter == null || verse == null) {
            LessonType.BASIC
        } else {
            LessonType.SCRIPTURE
        }

    val fingerprint: String
        get() = "$name|$sort|$author"

    val uniqueId = UniqueId(
        book = book,
        chapter = chapter,
        verse = verse,
        sort = sort,
        author = author
    )
}

fun LessonEntity.toItem(): LessonItem {
    return LessonItem(
        book = book,
        chapter = chapter,
        verse = verse,
        sort = sort,
        author = author,
        createdAt = createdAt.toLocalDateTime(),
        updatedAt = updatedAt.toLocalDateTime(),
        id = id
    )
}

fun LessonItem.toEntity(): LessonEntity {
    return LessonEntity(
        book = book,
        chapter = chapter,
        verse = verse,
        sort = sort,
        author = author,
        createdAt = createdAt.toTimestamp(),
        updatedAt = updatedAt.toTimestamp(),
        id = id
    )
}
