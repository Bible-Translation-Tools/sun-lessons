package org.bibletranslationtools.sun.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import org.bibletranslationtools.sun.data.entity.LessonEntity
import org.bibletranslationtools.sun.data.entity.LessonWithData

private const val BASIC_FILTER = "book IS NULL OR chapter IS NULL OR verse IS NULL"
private const val SCRIPTURE_FILTER = "book IS NOT NULL AND chapter IS NOT NULL AND verse IS NOT NULL"

@Dao
interface LessonDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(lesson: LessonEntity): Long

    @Delete
    suspend fun delete(lesson: LessonEntity)

    @Query("DELETE FROM lessons WHERE book = :book AND chapter = :chapter " +
            "AND verse = :verse AND author = :author")
    suspend fun delete(book: String?, chapter: Int?, verse: Int?, author: String)

    @Update
    suspend fun update(lesson: LessonEntity)

    @Query("SELECT * FROM lessons WHERE $BASIC_FILTER")
    suspend fun getBasicLessons(): List<LessonEntity>

    @Query("SELECT * FROM lessons WHERE $SCRIPTURE_FILTER")
    suspend fun getScriptureLessons(): List<LessonEntity>

    @Query("SELECT * FROM lessons WHERE $BASIC_FILTER")
    suspend fun getBasicWithData(): List<LessonWithData>

    @Query("SELECT * FROM lessons WHERE book = :book AND chapter = :chapter " +
            "ORDER BY verse, sort")
    suspend fun getAll(book: String?, chapter: Int?): List<LessonEntity>

    @Query("SELECT * FROM lessons WHERE book = :book AND chapter = :chapter")
    suspend fun getAllWithData(book: String?, chapter: Int?): List<LessonWithData>

    @Query("SELECT * FROM lessons WHERE $SCRIPTURE_FILTER")
    suspend fun getScriptureWithData(): List<LessonWithData>

    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun get(id: Long): LessonEntity?

    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun getSingleWithData(id: Long): LessonWithData?

    @Query("SELECT * FROM lessons WHERE book = :book AND chapter = :chapter " +
            "AND verse = :verse AND author = :author ORDER BY sort")
    suspend fun getGroup(
        book: String?,
        chapter: Int?,
        verse: Int?,
        author: String
    ): List<LessonEntity>

    @Query("SELECT * FROM lessons WHERE book = :book AND chapter = :chapter " +
            "AND verse = :verse AND author = :author ORDER BY sort")
    suspend fun getGroupWithData(
        book: String?,
        chapter: Int?,
        verse: Int?,
        author: String
    ): List<LessonWithData>
}