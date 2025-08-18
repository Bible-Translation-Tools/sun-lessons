package org.bibletranslationtools.sun.data.repositories

import org.bibletranslationtools.sun.data.dao.LessonDao
import org.bibletranslationtools.sun.data.model.LessonEntity
import org.bibletranslationtools.sun.data.model.LessonWithData
import org.bibletranslationtools.sun.ui.components.lessons.LessonType

interface LessonRepository {
    suspend fun insert(lesson: LessonEntity): Long
    suspend fun delete(lesson: LessonEntity)
    suspend fun update(lesson: LessonEntity)
    suspend fun getAll(lessonType: LessonType): List<LessonEntity>
    suspend fun getBasicLessons(): List<LessonEntity>
    suspend fun getScriptureLessons(): List<LessonEntity>
    suspend fun getAllWithData(lessonType: LessonType): List<LessonWithData>
    suspend fun getBasicWithData(): List<LessonWithData>
    suspend fun getScriptureWithData(): List<LessonWithData>
    suspend fun getWithData(id: Long): LessonWithData?
    suspend fun get(id: Long): LessonEntity?
}
class LessonRepositoryImpl(private val lessonDao: LessonDao) : LessonRepository {
    override suspend fun insert(lesson: LessonEntity): Long {
        return lessonDao.insert(lesson)
    }

    override suspend fun delete(lesson: LessonEntity) {
        lessonDao.delete(lesson)
    }

    override suspend fun update(lesson: LessonEntity) {
        lessonDao.update(lesson)
    }

    override suspend fun getAll(lessonType: LessonType): List<LessonEntity> {
        return when (lessonType) {
            LessonType.BASIC -> lessonDao.getBasicLessons()
            LessonType.SCRIPTURE -> lessonDao.getScriptureLessons()
        }
    }

    override suspend fun getBasicLessons(): List<LessonEntity> {
        return lessonDao.getBasicLessons()
    }

    override suspend fun getScriptureLessons(): List<LessonEntity> {
        return lessonDao.getScriptureLessons()
    }

    override suspend fun getAllWithData(lessonType: LessonType): List<LessonWithData> {
        return when (lessonType) {
            LessonType.BASIC -> lessonDao.getBasicWithData()
            LessonType.SCRIPTURE -> lessonDao.getScriptureWithData()
        }
    }

    override suspend fun getBasicWithData(): List<LessonWithData> {
        return lessonDao.getBasicWithData()
    }

    override suspend fun getScriptureWithData(): List<LessonWithData> {
        return lessonDao.getScriptureWithData()
    }

    override suspend fun getWithData(id: Long): LessonWithData? {
        return lessonDao.getWithData(id)
    }

    override suspend fun get(id: Long): LessonEntity? {
        return lessonDao.get(id)
    }

}