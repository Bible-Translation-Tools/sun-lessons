package org.bibletranslationtools.sun.ui.model

import org.bibletranslationtools.sun.data.model.CardEntity

data class CardItem(
    val id: Long,
    val symbol: String,
    val sort: Int,
    val image: String,
    val learned: Boolean,
    val tested: Boolean,
    val lessonId: Long,
    val passed: Boolean = false,
    val correct: Boolean? = null
)

fun CardItem.toEntity(): CardEntity {
    return CardEntity(
        id = id,
        symbol = symbol,
        sort = sort,
        image = image,
        learned = learned,
        tested = tested,
        lessonId = lessonId
    )
}

fun CardEntity.toItem(): CardItem {
    return CardItem(
        id = id,
        symbol = symbol,
        sort = sort,
        image = image,
        learned = learned,
        tested = tested,
        lessonId = lessonId,
        passed = false,
        correct = null
    )
}
