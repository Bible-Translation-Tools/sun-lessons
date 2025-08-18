package org.bibletranslationtools.sun.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.bibletranslationtools.sun.utils.Utils

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "book")
    val book: String? = null,
    @ColumnInfo(name = "chapter")
    val chapter: Int? = null,
    @ColumnInfo(name = "verse")
    val verse: Int? = null,
    @ColumnInfo(name = "author")
    val author: String? = null,
    @ColumnInfo(name = "createdAt")
    val createdAt: Long = Utils.getCurrentTimestamp()
)