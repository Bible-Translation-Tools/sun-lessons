package org.bibletranslationtools.sun.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.bibletranslationtools.sun.utils.Utils

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val book: String? = null,
    val chapter: Int? = null,
    val verse: Int? = null,
    val sort: Int = 1,
    val author: String = "unknown",
    val createdAt: Long = Utils.getCurrentTimestamp(),
    val updatedAt: Long = Utils.getCurrentTimestamp()
)