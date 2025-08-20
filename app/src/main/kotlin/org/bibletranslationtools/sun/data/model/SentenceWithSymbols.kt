package org.bibletranslationtools.sun.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class SentenceWithSymbols(
    @Embedded val sentence: SentenceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "sentenceId"
    )
    val symbols: List<SymbolEntity>
)
