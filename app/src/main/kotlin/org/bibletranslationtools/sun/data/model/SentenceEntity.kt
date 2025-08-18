package org.bibletranslationtools.sun.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "sentences", primaryKeys = ["id"])
data class SentenceEntity (
    @ColumnInfo(name = "id")
    val id: String = "",
    @ColumnInfo(name = "correct")
    val correct: String,
    @ColumnInfo(name = "incorrect")
    val incorrect: String,
    @ColumnInfo(name = "learned")
    var learned: Boolean = false,
    @ColumnInfo(name = "tested")
    var tested: Boolean = false,
    @ColumnInfo(name = "lesson_id")
    val lessonId: Int = 0
)