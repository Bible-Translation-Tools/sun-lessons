package org.bibletranslationtools.sun.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class GroupId(
    val book: String? = null,
    val chapter: Int? = null,
    val verse: Int? = null,
    val author: String? = null
) {
    val id = listOfNotNull(book, chapter, verse, author)
        .joinToString("_")
    val isScripture = book != null && chapter != null && verse != null
}

data class LessonGroup(
    val groupId: GroupId,
    val lessons: List<LessonItem>
) {
    val name: String
        get() = lessons.first().name

    val author: String
        get() = lessons.first().author

    val totalProgress: Float
        get() {
            return if (lessons.isNotEmpty()) {
                val total = lessons.sumOf { it.totalProgress.toDouble() }
                total.toFloat() / lessons.size
            } else 0f
        }
}