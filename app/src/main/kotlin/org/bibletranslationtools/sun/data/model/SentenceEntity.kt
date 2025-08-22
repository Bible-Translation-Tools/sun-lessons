package org.bibletranslationtools.sun.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "sentences",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SentenceEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sort: Int,
    val image: String,
    var learned: Boolean = false,
    var tested: Boolean = false,
    val lessonId: Long = 0
)