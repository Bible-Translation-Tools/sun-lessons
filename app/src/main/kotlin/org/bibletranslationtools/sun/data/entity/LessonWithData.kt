package org.bibletranslationtools.sun.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class LessonWithData(
    @Embedded val lesson: LessonEntity,
    @Relation(
        entity = CardEntity::class,
        parentColumn = "id",
        entityColumn = "lessonId"
    )
    val cards: List<CardEntity>,
    @Relation(
        entity = SentenceEntity::class,
        parentColumn = "id",
        entityColumn = "lessonId"
    )
    val sentences: List<SentenceEntity>
)