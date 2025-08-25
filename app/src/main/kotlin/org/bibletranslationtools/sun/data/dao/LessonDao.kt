package org.bibletranslationtools.sun.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import org.bibletranslationtools.sun.data.entity.LessonEntity
import org.bibletranslationtools.sun.data.entity.LessonWithData

@Dao
interface LessonDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(lesson: LessonEntity): Long

    @Delete
    suspend fun delete(lesson: LessonEntity)

    @Query(
        """
    DELETE FROM lessons
    WHERE
        ((:book IS NULL AND book IS NOT NULL) OR (book = :book)) AND
        ((:chapter IS NULL AND chapter IS NOT NULL) OR (chapter = :chapter)) AND
        ((:verse IS NULL AND verse IS NOT NULL) OR (verse = :verse)) AND
        ((:author IS NULL AND author IS NOT NULL) OR (author = :author))
    """
    )
    suspend fun delete(book: String?, chapter: Int?, verse: Int?, author: String?)

    @Update
    suspend fun update(lesson: LessonEntity)

    @Query(
        """
    SELECT * FROM lessons
    WHERE
        ((:book IS NULL AND book IS NOT NULL) OR (book = :book)) AND
        ((:chapter IS NULL AND chapter IS NOT NULL) OR (chapter = :chapter)) AND
        ((:verse IS NULL AND verse IS NOT NULL) OR (verse = :verse)) AND
        ((:author IS NULL AND author IS NOT NULL) OR (author = :author))
    ORDER BY verse, sort
    """
    )
    suspend fun getAll(book: String?, chapter: Int?, verse: Int?, author: String?): List<LessonEntity>

    @Query("""
        SELECT * FROM lessons 
        WHERE book IS NOT NULL 
        AND chapter IS NOT NULL 
        AND verse IS NOT NULL 
        AND author IS NOT NULL
    """)
    suspend fun getScriptureWithData(): List<LessonWithData>

    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun get(id: Long): LessonEntity?

    @Query(
        """
    SELECT * FROM lessons
    WHERE
        ((:book IS NULL AND book IS NOT NULL) OR (book = :book)) AND
        ((:chapter IS NULL AND chapter IS NOT NULL) OR (chapter = :chapter)) AND
        ((:verse IS NULL AND verse IS NOT NULL) OR (verse = :verse)) AND
        ((:author IS NULL AND author IS NOT NULL) OR (author = :author))
    ORDER BY sort
    """
    )
    suspend fun getGroup(
        book: String?,
        chapter: Int?,
        verse: Int?,
        author: String?
    ): List<LessonEntity>

    @Query(
        """
    SELECT * FROM lessons
    WHERE
        ((:book IS NULL AND book IS NOT NULL) OR (book = :book)) AND
        ((:chapter IS NULL AND chapter IS NOT NULL) OR (chapter = :chapter)) AND
        ((:verse IS NULL AND verse IS NOT NULL) OR (verse = :verse)) AND
        ((:author IS NULL AND author IS NOT NULL) OR (author = :author))
    ORDER BY sort
    """
    )
    suspend fun getGroupWithData(
        book: String?,
        chapter: Int?,
        verse: Int?,
        author: String?
    ): List<LessonWithData>
}