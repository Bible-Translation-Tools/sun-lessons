package org.bibletranslationtools.sun.ui.model

import org.bibletranslationtools.sun.data.entity.SentenceEntity

data class SentenceItem(
    val sort: Int,
    val learned: Boolean,
    val tested: Boolean,
    val lessonId: Long,
    val symbols: List<SymbolItem>,
    val image: String? = null,
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
