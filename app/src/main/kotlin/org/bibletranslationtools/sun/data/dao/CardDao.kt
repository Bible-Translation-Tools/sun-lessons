package org.bibletranslationtools.sun.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import org.bibletranslationtools.sun.data.entity.CardEntity

@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(card: CardEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(cards: List<CardEntity>)

    @Delete
    suspend fun delete(card: CardEntity)

    @Delete
    suspend fun deleteAll(cards: List<CardEntity>)

    @Update
    suspend fun update(card: CardEntity)

    @Update
    suspend fun updateAll(cards: List<CardEntity>)

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun get(id: Long): CardEntity?

    @Query("SELECT * FROM cards WHERE learned = 1")
    suspend fun getAllLearned(): List<CardEntity>

    @Query("SELECT COUNT(*) FROM cards WHERE learned = 1")
    suspend fun allLearnedCount(): Int

    @Query("SELECT * FROM cards WHERE tested = 1")
    suspend fun getAllTested(): List<CardEntity>

    @Query("SELECT COUNT(*) FROM cards WHERE tested = 1")
    suspend fun allTestedCount(): Int

    @Query("SELECT * FROM cards WHERE lessonId = :lessonId")
    suspend fun getByLesson(lessonId: Long): List<CardEntity>

    @Query("SELECT COUNT(*) FROM cards WHERE lessonId = :lessonId")
    suspend fun getByLessonCount(lessonId: Long): Int

    @Query("SELECT * FROM cards WHERE learned = 1 AND lessonId = :lessonId")
    suspend fun getLearnedByLesson(lessonId: Long): List<CardEntity>

    @Query("SELECT COUNT(*) FROM cards WHERE learned = 1 AND lessonId = :lessonId")
    suspend fun getLearnedByLessonCount(lessonId: Long): Int

    @Query("SELECT * FROM cards WHERE tested = 1 AND lessonId = :lessonId")
    suspend fun getTestedByLesson(lessonId: Long): List<CardEntity>

    @Query("SELECT COUNT(*) FROM cards WHERE tested = 1 AND lessonId = :lessonId")
    suspend fun getTestedByLessonCount(lessonId: Long): Int
}