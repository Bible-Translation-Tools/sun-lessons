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

@Dao
interface LessonDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(lesson: LessonEntity)

    @Delete
    suspend fun delete(lesson: LessonEntity)

    @Update
    suspend fun update(lesson: LessonEntity)

    @Transaction
    @Query("SELECT * FROM lessons")
    suspend fun getAll(): List<LessonEntity>

    @Transaction
    @Query("SELECT * FROM lessons")
    suspend fun getAllWithData(): List<LessonWithData>

    @Transaction
    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun get(id: Int): LessonEntity?

    @Transaction
    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun getWithData(id: Int): LessonWithData?
}