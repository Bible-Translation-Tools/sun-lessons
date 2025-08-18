package org.bibletranslationtools.sun.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "lessons", primaryKeys = ["id"])
data class LessonEntity(
    @ColumnInfo(name = "id")
    val id: Int
)