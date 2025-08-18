package org.bibletranslationtools.sun.ui.model

import org.bibletranslationtools.sun.data.model.CardEntity

data class CardItem(
    val id: String,
    val symbol: String,
    val primary: String,
    val secondary: String,
    val learned: Boolean,
    val tested: Boolean,
    val lessonId: Int,
    val passed: Boolean = false,
    val correct: Boolean? = null
)

fun CardItem.toEntity(): CardEntity {
    return CardEntity(
        id = id,
        symbol = symbol,
        primary = primary,
        secondary = secondary,
        learned = learned,
        tested = tested,
        lessonId = lessonId
    )
}

fun CardEntity.toItem(): CardItem {
    return CardItem(
        id = id,
        symbol = symbol,
        primary = primary,
        secondary = secondary,
        learned = learned,
        tested = tested,
        lessonId = lessonId,
        passed = false,
        correct = null
    )
}
