package org.bibletranslationtools.sun.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "symbols")
data class SymbolEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "sort")
    val sort: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "sentence_id")
    val sentenceId: Long = 0
)
