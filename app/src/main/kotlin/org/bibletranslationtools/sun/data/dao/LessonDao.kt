package org.bibletranslationtools.sun.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import org.bibletranslationtools.sun.data.model.LessonEntity
import org.bibletranslationtools.sun.data.model.LessonWithData

private const val BASIC_FILTER = "book IS NULL OR chapter IS NULL OR verse IS NULL"
private const val SCRIPTURE_FILTER = "book IS NOT NULL AND chapter IS NOT NULL AND verse IS NOT NULL"

@Dao
interface LessonDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(lesson: LessonEntity): Long

    @Delete
    suspend fun delete(lesson: LessonEntity)

    @Update
    suspend fun update(lesson: LessonEntity)

    @Transaction
    @Query("SELECT * FROM lessons WHERE $BASIC_FILTER")
    suspend fun getBasicLessons(): List<LessonEntity>

    @Transaction
    @Query("SELECT * FROM lessons WHERE $SCRIPTURE_FILTER")
    suspend fun getScriptureLessons(): List<LessonEntity>

    @Transaction
    @Query("SELECT * FROM lessons WHERE $BASIC_FILTER")
    suspend fun getBasicWithData(): List<LessonWithData>

    @Transaction
    @Query("SELECT * FROM lessons WHERE $SCRIPTURE_FILTER")
    suspend fun getScriptureWithData(): List<LessonWithData>

    @Transaction
    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun get(id: Long): LessonEntity?

    @Transaction
    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun getWithData(id: Long): LessonWithData?
}