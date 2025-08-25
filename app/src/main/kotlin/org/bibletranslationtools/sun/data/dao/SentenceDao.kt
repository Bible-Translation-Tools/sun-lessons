package org.bibletranslationtools.sun.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import org.bibletranslationtools.sun.data.entity.SentenceEntity
import org.bibletranslationtools.sun.data.entity.SentenceWithSymbols

@Dao
interface SentenceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sentence: SentenceEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(sentences: List<SentenceEntity>)

    @Delete
    suspend fun delete(sentence: SentenceEntity)

    @Delete
    suspend fun deleteAll(sentences: List<SentenceEntity>)

    @Update
    suspend fun update(sentence: SentenceEntity)

    @Update
    suspend fun updateAll(sentences: List<SentenceEntity>)

    @Query("SELECT * FROM sentences WHERE id = :id")
    suspend fun get(id: Long): SentenceEntity?

    @Query("SELECT * FROM sentences WHERE lessonId = :lessonId")
    suspend fun getByLesson(lessonId: Long): List<SentenceEntity>

    @Query("SELECT * FROM sentences WHERE lessonId = :lessonId")
    suspend fun getByLessonWithSymbols(lessonId: Long): List<SentenceWithSymbols>

    @Query("SELECT * FROM sentences WHERE tested = 1")
    suspend fun getAllTestedWithSymbols(): List<SentenceWithSymbols>

    @Query("SELECT COUNT(*) FROM sentences WHERE tested = 1")
    suspend fun allTestedCount(): Int

    @Query("SELECT * FROM sentences WHERE learned = 1")
    suspend fun getAllLearnedWithSymbols(): List<SentenceWithSymbols>

    @Query("SELECT COUNT(*) FROM sentences WHERE learned = 1")
    suspend fun allLearnedCount(): Int

    @Query("SELECT * FROM sentences WHERE learned = 1 AND lessonId = :lessonId")
    suspend fun getLearnedByLesson(lessonId: Long): List<SentenceEntity>

    @Query("SELECT COUNT(*) FROM sentences WHERE lessonId = :lessonId")
    suspend fun getByLessonCount(lessonId: Long): Int

    @Query("SELECT COUNT(*) FROM sentences WHERE learned = 1 AND lessonId = :lessonId")
    suspend fun getLearnedByLessonCount(lessonId: Long): Int

    @Query("SELECT COUNT(*) FROM sentences WHERE tested = 1 AND lessonId = :lessonId")
    suspend fun getTestedByLessonCount(lessonId: Long): Int
}