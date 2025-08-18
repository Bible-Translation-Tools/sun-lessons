package org.bibletranslationtools.sun.data.repositories

import org.bibletranslationtools.sun.data.dao.CardDao
import org.bibletranslationtools.sun.data.model.CardEntity

interface CardRepository {
    suspend fun insert(card: CardEntity): Long
    suspend fun delete(card: CardEntity)
    suspend fun update(card: CardEntity)
    suspend fun get(id: Long): CardEntity?
    suspend fun getByLesson(lessonId: Long): List<CardEntity>
    suspend fun getAllLearned(): List<CardEntity>
    suspend fun countAllLearned(): Int
    suspend fun getAllTested(): List<CardEntity>
    suspend fun countAllTested(): Int
    suspend fun getByLessonCount(lessonId: Long): Int
    suspend fun getLearnedByLesson(lessonId: Long): List<CardEntity>
    suspend fun getLearnedByLessonCount(lessonId: Long): Int
    suspend fun getTestedByLesson(lessonId: Long): List<CardEntity>
    suspend fun getTestedByLessonCount(lessonId: Long): Int
}
class CardRepositoryImpl(private val cardDao: CardDao) : CardRepository {

    override suspend fun insert(card: CardEntity): Long {
        return cardDao.insert(card)
    }

    override suspend fun delete(card: CardEntity) {
        cardDao.delete(card)
    }

    override suspend fun update(card: CardEntity) {
        cardDao.update(card)
    }

    override suspend fun get(id: Long): CardEntity? {
        return cardDao.get(id)
    }

    override suspend fun getByLesson(lessonId: Long): List<CardEntity> {
        return cardDao.getByLesson(lessonId)
    }

    override suspend fun getAllLearned(): List<CardEntity> {
        return cardDao.getAllLearned()
    }

    override suspend fun countAllLearned(): Int {
        return cardDao.allLearnedCount()
    }

    override suspend fun getAllTested(): List<CardEntity> {
        return cardDao.getAllTested()
    }

    override suspend fun countAllTested(): Int {
        return cardDao.allTestedCount()
    }

    override suspend fun getByLessonCount(lessonId: Long): Int {
        return cardDao.getByLessonCount(lessonId)
    }

    override suspend fun getLearnedByLesson(lessonId: Long): List<CardEntity> {
        return cardDao.getLearnedByLesson(lessonId)
    }

    override suspend fun getLearnedByLessonCount(lessonId: Long): Int {
        return cardDao.getLearnedByLessonCount(lessonId)
    }

    override suspend fun getTestedByLesson(lessonId: Long): List<CardEntity> {
        return cardDao.getTestedByLesson(lessonId)
    }

    override suspend fun getTestedByLessonCount(lessonId: Long): Int {
        return cardDao.getTestedByLessonCount(lessonId)
    }

}