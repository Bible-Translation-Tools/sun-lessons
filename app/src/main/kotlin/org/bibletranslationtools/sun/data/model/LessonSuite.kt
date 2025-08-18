package org.bibletranslationtools.sun.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LessonSuite(
    val version: Int,
    val lessons: List<LessonData>
)

@Serializable
data class LessonData(
    val cards: List<CardData> = emptyList(),
    val sentences: List<SentenceData> = emptyList()
)

@Serializable
data class CardData(
    val symbol: String,
    val image: String,
    val sort: Int = 0,
    val lessonId: Long = 0
)

@Serializable
data class SentenceData(
    val image: String,
    val symbols: List<SymbolData>,
    val sort: Int = 0,
    val lessonId: Long = 0
)

@Serializable
data class SymbolData(
    val name: String,
    val sort: Int = 0,
    val sentenceId: Long = 0
)

fun LessonData.toEntity(): LessonEntity {
    return LessonEntity()
}

fun CardData.toEntity(): CardEntity {
    return CardEntity(
        symbol = symbol,
        sort = sort,
        image = image,
        learned = false,
        tested = false,
        lessonId = lessonId
    )
}

fun SentenceData.toEntity(): SentenceEntity {
    return SentenceEntity(
        sort = sort,
        image = image,
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