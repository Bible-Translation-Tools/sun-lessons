package org.bibletranslationtools.sun.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.bibletranslationtools.sun.ui.model.SYSTEM_USER
import org.bibletranslationtools.sun.utils.Utils

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val book: String? = null,
    val chapter: Int? = null,
    val verse: Int? = null,
    val sort: Int = 1,
    val author: String = SYSTEM_USER,
    val createdAt: Long = Utils.getCurrentTimestamp(),
    val updatedAt: Long = Utils.getCurrentTimestamp()
)