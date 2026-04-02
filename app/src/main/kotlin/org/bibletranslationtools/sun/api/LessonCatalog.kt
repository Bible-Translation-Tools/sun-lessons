package org.bibletranslationtools.sun.api

import kotlinx.serialization.Serializable
import org.bibletranslationtools.sun.utils.Utils

@Serializable
data class LessonCatalog(
    val version: Int,
    val lessons: List<LessonData>
)

@Serializable
data class LessonData(
    val book: String? = null,
    val chapter: Int? = null,
    val verse: Int? = null,
    val sort: Int = 1,
    val author: String? = null,
    val cards: List<CardData> = emptyList(),
    val sentences: List<SentenceData> = emptyList(),
    val createdAt: Long = Utils.getCurrentTimestamp(),
    val updatedAt: Long = Utils.getCurrentTimestamp()
)

@Serializable
data class CardData(
    val symbol: String,
    val image: String,
    val sort: Int = 1
)

@Serializable
data class SentenceData(
    val symbols: List<SymbolData>,
    val image: String? = null,
    val sort: Int = 1
)

@Serializable
data class SymbolData(
    val name: String,
    val sort: Int = 0
)