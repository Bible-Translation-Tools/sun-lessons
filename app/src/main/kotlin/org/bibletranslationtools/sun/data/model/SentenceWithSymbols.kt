package org.bibletranslationtools.sun.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class SentenceWithSymbols(
    @Embedded val sentence: SentenceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "sentence_id"
    )
    val symbols: List<SymbolEntity>
)
