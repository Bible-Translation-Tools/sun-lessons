package org.bibletranslationtools.sun.ui.model

import org.bibletranslationtools.sun.data.model.SymbolEntity

data class SymbolItem(
    val id: Int,
    val name: String,
    val sort: Int,
    val sentenceId: String?,
    val selected: Boolean,
    val correct: Boolean?
)

fun SymbolItem.toEntity() = SymbolEntity(
    id = id,
    name = name,
    sort = sort,
    sentenceId = sentenceId
)

fun SymbolEntity.toItem() = SymbolItem(
    id = id,
    name = name,
    sort = sort,
    sentenceId = sentenceId,
    selected = false,
    correct = null,
)
