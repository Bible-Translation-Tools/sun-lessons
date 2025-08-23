package org.bibletranslationtools.sun.ui.model

import org.bibletranslationtools.sun.data.entity.CardEntity

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

fun CardItem.toEntity(): CardEntity {
    return CardEntity(
        symbol = symbol,
        sort = sort,
        image = image,
        learned = learned,
        tested = tested,
        lessonId = lessonId,
        id = id
    )
}

fun CardEntity.toItem(): CardItem {
    return CardItem(
        symbol = symbol,
        sort = sort,
        image = image,
        learned = learned,
        tested = tested,
        lessonId = lessonId,
        passed = false,
        correct = null,
        id = id
    )
}
