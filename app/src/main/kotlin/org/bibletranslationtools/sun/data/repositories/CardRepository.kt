package org.bibletranslationtools.sun.data.repositories

import org.bibletranslationtools.sun.data.dao.CardDao
import org.bibletranslationtools.sun.data.model.CardEntity

interface CardRepository {
    suspend fun insert(card: CardEntity)
    suspend fun delete(card: CardEntity)
    suspend fun update(card: CardEntity)
    suspend fun get(id: String): CardEntity?
    suspend fun getByLesson(lessonId: Int): List<CardEntity>
    suspend fun getAllLearned(): List<CardEntity>
    suspend fun countAllLearned(): Int
    suspend fun getAllTested(): List<CardEntity>
    suspend fun countAllTested(): Int
    suspend fun getByLessonCount(lessonId: Int): Int
    suspend fun getLearnedByLesson(lessonId: Int): List<CardEntity>
    suspend fun getLearnedByLessonCount(lessonId: Int): Int
    suspend fun getTestedByLesson(lessonId: Int): List<CardEntity>
    suspend fun getTestedByLessonCount(lessonId: Int): Int
}
class CardRepositoryImpl(private val cardDao: CardDao) : CardRepository {

    override suspend fun insert(card: CardEntity) {
        cardDao.insert(card)
    }

    override suspend fun delete(card: CardEntity) {
        cardDao.delete(card)
    }

    override suspend fun update(card: CardEntity) {
        cardDao.update(card)
    }

    override suspend fun get(id: String): CardEntity? {
        return cardDao.get(id)
    }

    override suspend fun getByLesson(lessonId: Int): List<CardEntity> {
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

    override suspend fun getByLessonCount(lessonId: Int): Int {
        return cardDao.getByLessonCount(lessonId)
    }

    override suspend fun getLearnedByLesson(lessonId: Int): List<CardEntity> {
        return cardDao.getLearnedByLesson(lessonId)
    }

    override suspend fun getLearnedByLessonCount(lessonId: Int): Int {
        return cardDao.getLearnedByLessonCount(lessonId)
    }

    override suspend fun getTestedByLesson(lessonId: Int): List<CardEntity> {
        return cardDao.getTestedByLesson(lessonId)
    }

    override suspend fun getTestedByLessonCount(lessonId: Int): Int {
        return cardDao.getTestedByLessonCount(lessonId)
    }

}