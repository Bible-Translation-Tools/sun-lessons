package org.bibletranslationtools.sun.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LessonSuite(
    val version: Int,
    val lessons: List<LessonData>
)

@Serializable
data class LessonData(
    val id: Int,
    val cards: List<CardData> = emptyList(),
    val sentences: List<SentenceData> = emptyList()
)

@Serializable
data class CardData(
    val id: String,
    val symbol: String,
    val primary: String,
    val secondary: String,
    val lessonId: Int = 0
)

@Serializable
data class SentenceData(
    val id: String,
    val symbols: List<SymbolData>,
    val correct: String,
    val incorrect: String,
    val lessonId: Int = 0
)

@Serializable
data class SymbolData(
    val sort: Int,
    val name: String,
    val sentenceId: String = ""
)

fun LessonData.toEntity(): LessonEntity {
    return LessonEntity(id)
}

fun CardData.toEntity(): CardEntity {
    return CardEntity(
        id = id,
        symbol = symbol,
        primary = primary,
        secondary = secondary,
        learned = false,
        tested = false,
        lessonId = lessonId
    )
}

fun SentenceData.toEntity(): SentenceEntity {
    return SentenceEntity(
        id = id,
        correct = correct,
        incorrect = incorrect,
        learned = false,
        tested = false,
        lessonId = lessonId
    )
}

fun SymbolData.toEntity(): SymbolEntity {
    return SymbolEntity(
        sort = sort,
        name = name,
        sentenceId = sentenceId
    )
}