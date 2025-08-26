package org.bibletranslationtools.sun.data.repositories

import org.bibletranslationtools.sun.data.dao.LessonDao
import org.bibletranslationtools.sun.data.entity.LessonEntity
import org.bibletranslationtools.sun.data.entity.LessonWithData
import org.bibletranslationtools.sun.ui.model.GroupId

interface LessonRepository {
    suspend fun insert(lesson: LessonEntity): Long
    suspend fun delete(lesson: LessonEntity)
    suspend fun deleteScripture(groupId: GroupId)
    suspend fun update(lesson: LessonEntity)
    suspend fun getAllScripture(groupId: GroupId): List<LessonEntity>
    suspend fun getGroup(groupId: GroupId): List<LessonEntity>
    suspend fun getScriptureWithData(): List<LessonWithData>
    suspend fun getGroupWithData(id: GroupId): List<LessonWithData>
    suspend fun get(id: Long): LessonEntity?
}
class LessonRepositoryImpl(private val lessonDao: LessonDao) : LessonRepository {
    override suspend fun insert(lesson: LessonEntity): Long {
        return lessonDao.insert(lesson)
    }

    override suspend fun delete(lesson: LessonEntity) {
        lessonDao.delete(lesson)
    }

    override suspend fun deleteScripture(groupId: GroupId) {
        lessonDao.deleteScripture(
            groupId.book,
            groupId.chapter,
            groupId.verse,
            groupId.author
        )
    }

    override suspend fun update(lesson: LessonEntity) {
        lessonDao.update(lesson)
    }

    override suspend fun getAllScripture(groupId: GroupId): List<LessonEntity> {
        return lessonDao.getAllScripture(
            groupId.book,
            groupId.chapter,
            groupId.verse,
            groupId.author
        )
    }

    override suspend fun getGroup(groupId: GroupId): List<LessonEntity> {
        return lessonDao.getGroup(
            groupId.book,
            groupId.chapter,
            groupId.verse,
            groupId.author
        )
    }

    override suspend fun getScriptureWithData(): List<LessonWithData> {
        return lessonDao.getScriptureWithData()
    }

    override suspend fun getGroupWithData(id: GroupId): List<LessonWithData> {
        return lessonDao.getGroupWithData(
            id.book,
            id.chapter,
            id.verse,
            id.author
        )
    }

    override suspend fun get(id: Long): LessonEntity? {
        return lessonDao.get(id)
    }
}