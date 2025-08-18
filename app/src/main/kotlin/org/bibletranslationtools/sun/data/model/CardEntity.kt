package org.bibletranslationtools.sun.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "cards", primaryKeys = ["id"])
data class CardEntity(
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "symbol")
    val symbol: String,
    @ColumnInfo(name = "primary")
    val primary: String,
    @ColumnInfo(name = "secondary")
    val secondary: String,
    @ColumnInfo(name = "learned")
    var learned: Boolean = false,
    @ColumnInfo(name = "tested")
    var tested: Boolean = false,
    @ColumnInfo(name = "lesson_id")
    var lessonId: Int
)