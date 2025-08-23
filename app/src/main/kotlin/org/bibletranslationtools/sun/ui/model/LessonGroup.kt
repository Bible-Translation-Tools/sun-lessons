package org.bibletranslationtools.sun.ui.model

data class GroupId(
    val book: String?,
    val chapter: Int?,
    val verse: Int?,
    val author: String
)

data class LessonGroup(
    val groupId: GroupId,
    val lessons: List<LessonItem>
)