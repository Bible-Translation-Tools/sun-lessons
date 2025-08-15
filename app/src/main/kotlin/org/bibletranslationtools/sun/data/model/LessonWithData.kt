package org.bibletranslationtools.sun.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class LessonWithData(
    @Embedded val lesson: LessonEntity,
    @Relation(
        entity = CardEntity::class,
        parentColumn = "id",
        entityColumn = "lesson_id"
    )
    val cards: List<CardEntity>,
    @Relation(
        entity = SentenceEntity::class,
        parentColumn = "id",
        entityColumn = "lesson_id"
    )
    val sentences: List<SentenceEntity>
)