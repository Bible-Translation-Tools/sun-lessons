package org.bibletranslationtools.sun.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "symbols")
data class SymbolEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sort: Int,
    val name: String,
    val sentenceId: Long = 0
)
