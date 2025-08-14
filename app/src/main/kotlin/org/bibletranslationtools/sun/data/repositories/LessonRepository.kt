package org.bibletranslationtools.sun.data.repositories

import org.bibletranslationtools.sun.data.dao.LessonDao
import org.bibletranslationtools.sun.data.model.Lesson
import org.bibletranslationtools.sun.data.model.LessonWithData
interface LessonRepository {
    suspend fun insert(lesson: Lesson)
    suspend fun delete(lesson: Lesson)
    suspend fun update(lesson: Lesson)
    suspend fun getAll(): List<Lesson>
    suspend fun getAllWithData(): List<LessonWithData>
    suspend fun getWithData(id: Int): LessonWithData?
    suspend fun get(id: Int): Lesson?
}
class LessonRepositoryImpl(private val lessonDao: LessonDao) : LessonRepository {
    override suspend fun insert(lesson: Lesson) {
        return lessonDao.insert(lesson)
    }

    override suspend fun delete(lesson: Lesson) {
        lessonDao.delete(lesson)
    }

    override suspend fun update(lesson: Lesson) {
        lessonDao.update(lesson)
    }

    override suspend fun getAll(): List<Lesson> {
        return lessonDao.getAll()
    }

    override suspend fun getAllWithData(): List<LessonWithData> {
        return lessonDao.getAllWithData()
    }

    override suspend fun getWithData(id: Int): LessonWithData? {
        return lessonDao.getWithData(id)
    }

    override suspend fun get(id: Int): Lesson? {
        return lessonDao.get(id)
    }

}