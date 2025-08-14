package org.bibletranslationtools.sun.data.repositories

import org.bibletranslationtools.sun.data.dao.SentenceDao
import org.bibletranslationtools.sun.data.dao.SymbolDao
import org.bibletranslationtools.sun.data.model.Sentence
import org.bibletranslationtools.sun.data.model.SentenceWithSymbols
import org.bibletranslationtools.sun.data.model.Symbol

interface SentenceRepository {
    suspend fun insert(sentence: Sentence)
    suspend fun delete(sentence: Sentence)
    suspend fun update(sentence: Sentence)
    suspend fun insert(symbol: Symbol)
    suspend fun get(id: String): Sentence?
    suspend fun getByLesson(lessonId: Int): List<Sentence>
    suspend fun getAllWithSymbols(lessonId: Int): List<SentenceWithSymbols>
    suspend fun getAllLearnedWithSymbols(): List<SentenceWithSymbols>
    suspend fun allLearnedCount(): Int
    suspend fun getAllTestedWithSymbols(): List<SentenceWithSymbols>
    suspend fun allTestedCount(): Int
    suspend fun getByLessonCount(lessonId: Int): Int
    suspend fun getLearnedByLessonCount(lessonId: Int): Int
    suspend fun getTestedByLessonCount(lessonId: Int): Int
}

class SentenceRepositoryImpl(
    private val sentenceDao: SentenceDao,
    private val symbolDao: SymbolDao
) : SentenceRepository {
    override suspend fun insert(sentence: Sentence) {
        sentenceDao.insert(sentence)
    }

    override suspend fun delete(sentence: Sentence) {
        sentenceDao.delete(sentence)
    }

    override suspend fun update(sentence: Sentence) {
        sentenceDao.update(sentence)
    }

    override suspend fun insert(symbol: Symbol) {
        symbolDao.insert(symbol)
    }

    override suspend fun get(id: String): Sentence? {
        return sentenceDao.get(id)
    }

    override suspend fun getByLesson(lessonId: Int): List<Sentence> {
        return sentenceDao.getByLesson(lessonId)
    }

    override suspend fun getAllWithSymbols(lessonId: Int): List<SentenceWithSymbols> {
        return sentenceDao.getByLessonWithSymbols(lessonId)
    }

    override suspend fun getAllLearnedWithSymbols(): List<SentenceWithSymbols> {
        return sentenceDao.getAllLearnedWithSymbols()
    }

    override suspend fun allLearnedCount(): Int {
        return sentenceDao.allLearnedCount()
    }

    override suspend fun getAllTestedWithSymbols(): List<SentenceWithSymbols> {
        return sentenceDao.getAllTestedWithSymbols()
    }

    override suspend fun allTestedCount(): Int {
        return sentenceDao.allTestedCount()
    }

    override suspend fun getByLessonCount(lessonId: Int): Int {
        return sentenceDao.getByLessonCount(lessonId)
    }

    override suspend fun getLearnedByLessonCount(lessonId: Int): Int {
        return sentenceDao.getLearnedByLessonCount(lessonId)
    }

    override suspend fun getTestedByLessonCount(lessonId: Int): Int {
        return sentenceDao.getTestedByLessonCount(lessonId)
    }
}