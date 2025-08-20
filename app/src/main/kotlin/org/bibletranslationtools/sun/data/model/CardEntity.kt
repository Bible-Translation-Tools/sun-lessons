package org.bibletranslationtools.sun.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val symbol: String,
    val sort: Int,
    val image: String,
    var learned: Boolean = false,
    var tested: Boolean = false,
    var lessonId: Long = 0
)