package org.bibletranslationtools.sun.ui.model

data class CardItem(
    val symbol: String,
    val sort: Int,
    val image: String,
    val learned: Boolean,
    val tested: Boolean,
    val lessonId: Long,
    val passed: Boolean = false,
    val correct: Boolean? = null,
    val id: Long = 0
)
