package org.bibletranslationtools.sun.ui.model

import org.bibletranslationtools.sun.data.model.SentenceEntity

data class SentenceItem(
    val id: String,
    val correct: String,
    val incorrect: String,
    val learned: Boolean,
    val tested: Boolean,
    val lessonId: Int,
    val symbols: List<SymbolItem>,
    var passed: Boolean
)

fun SentenceItem.toEntity() = SentenceEntity(
    id = id,
    correct = correct,
    incorrect = incorrect,
    learned = learned,
    tested = tested,
    lessonId = lessonId
)

fun SentenceEntity.toItem() = SentenceItem(
    id = id,
    correct = correct,
    incorrect = incorrect,
    learned = learned,
    tested = tested,
    lessonId = lessonId,
    symbols = emptyList(),
    passed = false
)
