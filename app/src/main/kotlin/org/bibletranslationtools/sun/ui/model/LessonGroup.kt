package org.bibletranslationtools.sun.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class GroupId(
    val book: String?,
    val chapter: Int?,
    val verse: Int?,
    val author: String
)

data class LessonGroup(
    val groupId: GroupId,
    val lessons: List<LessonItem>
) {
    val groupIdStr = "${groupId.book}|${groupId.chapter}|${groupId.verse}|${groupId.author}"

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