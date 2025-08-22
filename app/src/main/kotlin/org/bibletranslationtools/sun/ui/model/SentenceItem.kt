package org.bibletranslationtools.sun.ui.model

import org.bibletranslationtools.sun.data.model.SentenceEntity

data class SentenceItem(
    val sort: Int,
    val image: String,
    val learned: Boolean,
    val tested: Boolean,
    val lessonId: Long,
    val symbols: List<SymbolItem>,
    var passed: Boolean = false,
    val id: Long = 0
) {
    val fingerprint: String
        get() = symbols.sortedBy { it.sort }.joinToString("|") { it.name }
}

fun SentenceItem.toEntity() = SentenceEntity(
    sort = sort,
    image = image,
    learned = learned,
    tested = tested,
    lessonId = lessonId,
    id = id
)

fun SentenceEntity.toItem() = SentenceItem(
    sort = sort,
    image = image,
    learned = learned,
    tested = tested,
    lessonId = lessonId,
    symbols = emptyList(),
    passed = false,
    id = id
)
