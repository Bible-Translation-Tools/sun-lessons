package org.bibletranslationtools.sun.data.repositories

import org.bibletranslationtools.sun.data.dao.LessonDao
import org.bibletranslationtools.sun.data.model.LessonEntity
import org.bibletranslationtools.sun.data.model.LessonWithData
interface LessonRepository {
    suspend fun insert(lesson: LessonEntity)
    suspend fun delete(lesson: LessonEntity)
    suspend fun update(lesson: LessonEntity)
    suspend fun getAll(): List<LessonEntity>
    suspend fun getAllWithData(): List<LessonWithData>
    suspend fun getWithData(id: Int): LessonWithData?
    suspend fun get(id: Int): LessonEntity?
}
class LessonRepositoryImpl(private val lessonDao: LessonDao) : LessonRepository {
    override suspend fun insert(lesson: LessonEntity) {
        return lessonDao.insert(lesson)
    }

    override suspend fun delete(lesson: LessonEntity) {
        lessonDao.delete(lesson)
    }

    override suspend fun update(lesson: LessonEntity) {
        lessonDao.update(lesson)
    }

    override suspend fun getAll(): List<LessonEntity> {
        return lessonDao.getAll()
    }

    override suspend fun getAllWithData(): List<LessonWithData> {
        return lessonDao.getAllWithData()
    }

    override suspend fun getWithData(id: Int): LessonWithData? {
        return lessonDao.getWithData(id)
    }

    override suspend fun get(id: Int): LessonEntity? {
        return lessonDao.get(id)
    }

}