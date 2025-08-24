package org.bibletranslationtools.sun.ui.model

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
