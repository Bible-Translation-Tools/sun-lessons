package org.bibletranslationtools.sun.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "symbols",
    indices = [Index(value = ["sentenceId"])],
    foreignKeys = [
        ForeignKey(
            entity = SentenceEntity::class,
            parentColumns = ["id"],
            childColumns = ["sentenceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SymbolEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sort: Int,
    val name: String,
    val sentenceId: Long = 0
)
