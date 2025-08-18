package org.bibletranslationtools.sun.ui.model

import org.bibletranslationtools.sun.data.model.SentenceEntity

data class SentenceItem(
    val id: Long,
    val sort: Int,
    val image: String,
    val learned: Boolean,
    val tested: Boolean,
    val lessonId: Long,
    val symbols: List<SymbolItem>,
    var passed: Boolean
)

fun SentenceItem.toEntity() = SentenceEntity(
    id = id,
    sort = sort,
    image = image,
    learned = learned,
    tested = tested,
    lessonId = lessonId
)

fun SentenceEntity.toItem() = SentenceItem(
    id = id,
    sort = sort,
    image = image,
    learned = learned,
    tested = tested,
    lessonId = lessonId,
    symbols = emptyList(),
    passed = false
)
