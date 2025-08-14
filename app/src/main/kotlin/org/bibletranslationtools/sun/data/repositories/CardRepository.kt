package org.bibletranslationtools.sun.data.repositories

import org.bibletranslationtools.sun.data.dao.CardDao
import org.bibletranslationtools.sun.data.model.Card

interface CardRepository {
    suspend fun insert(card: Card)
    suspend fun delete(card: Card)
    suspend fun update(card: Card)
    suspend fun get(id: String): Card?
    suspend fun getByLesson(lessonId: Int): List<Card>
    suspend fun getAllLearned(): List<Card>
    suspend fun countAllLearned(): Int
    suspend fun getAllTested(): List<Card>
    suspend fun countAllTested(): Int
    suspend fun getByLessonCount(lessonId: Int): Int
    suspend fun getLearnedByLesson(lessonId: Int): List<Card>
    suspend fun getLearnedByLessonCount(lessonId: Int): Int
    suspend fun getTestedByLesson(lessonId: Int): List<Card>
    suspend fun getTestedByLessonCount(lessonId: Int): Int
}
class CardRepositoryImpl(private val cardDao: CardDao) : CardRepository {

    override suspend fun insert(card: Card) {
        cardDao.insert(card)
    }

    override suspend fun delete(card: Card) {
        cardDao.delete(card)
    }

    override suspend fun update(card: Card) {
        cardDao.update(card)
    }

    override suspend fun get(id: String): Card? {
        return cardDao.get(id)
    }

    override suspend fun getByLesson(lessonId: Int): List<Card> {
        return cardDao.getByLesson(lessonId)
    }

    override suspend fun getAllLearned(): List<Card> {
        return cardDao.getAllLearned()
    }

    override suspend fun countAllLearned(): Int {
        return cardDao.allLearnedCount()
    }

    override suspend fun getAllTested(): List<Card> {
        return cardDao.getAllTested()
    }

    override suspend fun countAllTested(): Int {
        return cardDao.allTestedCount()
    }

    override suspend fun getByLessonCount(lessonId: Int): Int {
        return cardDao.getByLessonCount(lessonId)
    }

    override suspend fun getLearnedByLesson(lessonId: Int): List<Card> {
        return cardDao.getLearnedByLesson(lessonId)
    }

    override suspend fun getLearnedByLessonCount(lessonId: Int): Int {
        return cardDao.getLearnedByLessonCount(lessonId)
    }

    override suspend fun getTestedByLesson(lessonId: Int): List<Card> {
        return cardDao.getTestedByLesson(lessonId)
    }

    override suspend fun getTestedByLessonCount(lessonId: Int): Int {
        return cardDao.getTestedByLessonCount(lessonId)
    }

}