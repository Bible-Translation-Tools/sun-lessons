package org.bibletranslationtools.sun.ui.model

data class SymbolItem(
    val name: String,
    val sort: Int,
    val sentenceId: Long,
    val selected: Boolean = false,
    val correct: Boolean? = null,
    val id: Long = 0
)
