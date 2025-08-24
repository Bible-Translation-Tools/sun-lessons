package org.bibletranslationtools.sun.ui.model

import org.bibletranslationtools.sun.data.entity.SymbolData
import org.bibletranslationtools.sun.data.entity.SymbolEntity

data class SymbolItem(
    val name: String,
    val sort: Int,
    val sentenceId: Long,
    val selected: Boolean = false,
    val correct: Boolean? = null,
    val id: Long = 0
)

fun SymbolItem.toEntity() = SymbolEntity(
    name = name,
    sort = sort,
    sentenceId = sentenceId,
    id = id
)

fun SymbolEntity.toItem() = SymbolItem(
    name = name,
    sort = sort,
    sentenceId = sentenceId,
    selected = false,
    correct = null,
    id = id
)

fun SymbolData.toItem(): SymbolItem {
    return SymbolItem(
        name = name,
        sort = sort,
        sentenceId = 0
    )
}
