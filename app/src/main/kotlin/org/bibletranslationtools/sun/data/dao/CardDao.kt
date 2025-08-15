package org.bibletranslationtools.sun.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import org.bibletranslationtools.sun.data.model.CardEntity

@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(card: CardEntity)

    @Delete
    suspend fun delete(card: CardEntity)

    @Update
    suspend fun update(card: CardEntity)

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun get(id: String): CardEntity?

    @Query("SELECT * FROM cards WHERE learned = 1")
    suspend fun getAllLearned(): List<CardEntity>

    @Query("SELECT COUNT(*) FROM cards WHERE learned = 1")
    suspend fun allLearnedCount(): Int

    @Query("SELECT * FROM cards WHERE tested = 1")
    suspend fun getAllTested(): List<CardEntity>

    @Query("SELECT COUNT(*) FROM cards WHERE tested = 1")
    suspend fun allTestedCount(): Int

    @Query("SELECT * FROM cards WHERE lesson_id = :lessonId")
    suspend fun getByLesson(lessonId: Int): List<CardEntity>

    @Query("SELECT COUNT(*) FROM cards WHERE lesson_id = :lessonId")
    suspend fun getByLessonCount(lessonId: Int): Int

    @Query("SELECT * FROM cards WHERE learned = 1 AND lesson_id = :lessonId")
    suspend fun getLearnedByLesson(lessonId: Int): List<CardEntity>

    @Query("SELECT COUNT(*) FROM cards WHERE learned = 1 AND lesson_id = :lessonId")
    suspend fun getLearnedByLessonCount(lessonId: Int): Int

    @Query("SELECT * FROM cards WHERE tested = 1 AND lesson_id = :lessonId")
    suspend fun getTestedByLesson(lessonId: Int): List<CardEntity>

    @Query("SELECT COUNT(*) FROM cards WHERE tested = 1 AND lesson_id = :lessonId")
    suspend fun getTestedByLessonCount(lessonId: Int): Int
}