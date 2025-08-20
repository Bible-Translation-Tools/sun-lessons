package org.bibletranslationtools.sun.ui.model

import kotlinx.datetime.LocalDateTime
import org.bibletranslationtools.sun.data.model.LessonEntity
import org.bibletranslationtools.sun.utils.Utils
import org.bibletranslationtools.sun.utils.toLocalDateTime
import org.bibletranslationtools.sun.utils.toTimestamp

data class LessonItem(
    val book: String?,
    val chapter: Int?,
    val verse: Int?,
    val author: String?,
    val createdAt: LocalDateTime = Utils.getCurrentTime(),
    val id: Long = 0
) {
    val name: String
        get() = if (book != null && chapter != null && verse != null) {
            "$book $chapter:$verse"
        } else {
            "Lesson $id"
        }
}

fun LessonEntity.toItem(): LessonItem {
    return LessonItem(
        book = book,
        chapter = chapter,
        verse = verse,
        author = author,
        createdAt = createdAt.toLocalDateTime(),
        id = id
    )
}

fun LessonItem.toEntity(): LessonEntity {
    return LessonEntity(
        book = book,
        chapter = chapter,
        verse = verse,
        author = author,
        createdAt = createdAt.toTimestamp(),
        id = id
    )
}
