package org.bibletranslationtools.sun.data.repositories

import org.bibletranslationtools.sun.data.dao.SentenceDao
import org.bibletranslationtools.sun.data.dao.SymbolDao
import org.bibletranslationtools.sun.data.model.SentenceEntity
import org.bibletranslationtools.sun.data.model.SentenceWithSymbols
import org.bibletranslationtools.sun.data.model.SymbolEntity

interface SentenceRepository {
    suspend fun insert(sentence: SentenceEntity)
    suspend fun delete(sentence: SentenceEntity)
    suspend fun update(sentence: SentenceEntity)
    suspend fun insert(symbol: SymbolEntity)
    suspend fun get(id: String): SentenceEntity?
    suspend fun getByLesson(lessonId: Int): List<SentenceEntity>
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
    override suspend fun insert(sentence: SentenceEntity) {
        sentenceDao.insert(sentence)
    }

    override suspend fun delete(sentence: SentenceEntity) {
        sentenceDao.delete(sentence)
    }

    override suspend fun update(sentence: SentenceEntity) {
        sentenceDao.update(sentence)
    }

    override suspend fun insert(symbol: SymbolEntity) {
        symbolDao.insert(symbol)
    }

    override suspend fun get(id: String): SentenceEntity? {
        return sentenceDao.get(id)
    }

    override suspend fun getByLesson(lessonId: Int): List<SentenceEntity> {
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