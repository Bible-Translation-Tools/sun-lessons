package org.bibletranslationtools.sun.usecase

import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.data.repositories.SymbolRepository
import org.bibletranslationtools.sun.ui.model.CardItem
import org.bibletranslationtools.sun.ui.model.LessonSuite
import org.bibletranslationtools.sun.ui.model.SentenceItem
import org.bibletranslationtools.sun.ui.model.SymbolItem
import org.bibletranslationtools.sun.ui.model.toEntity
import org.bibletranslationtools.sun.ui.model.toItem

class UpdateLesson(
    private val lessonRepository: LessonRepository,
    private val cardRepository: CardRepository,
    private val sentenceRepository: SentenceRepository,
    private val symbolRepository: SymbolRepository
) {
    suspend fun update(lesson: LessonSuite) {
        val localLesson = lessonRepository.getWithData(
            book = lesson.lesson.book ?: throw IllegalArgumentException("book is null"),
            chapter = lesson.lesson.chapter ?: throw IllegalArgumentException("chapter is null"),
            verse = lesson.lesson.verse ?: throw IllegalArgumentException("verse is null"),
            sort = lesson.lesson.sort,
            author = lesson.lesson.author
        )?.toItem()

        if (localLesson != null) {
            updateLesson(localLesson, lesson)
        } else {
            insertLesson(lesson)
        }
    }

    private suspend fun insertLesson(suite: LessonSuite) {
        val lessonId = lessonRepository.insert(
            suite.lesson.toEntity()
        )

        suite.cards.forEach {
            cardRepository.insert(
                it.copy(lessonId = lessonId).toEntity()
            )
        }

        suite.sentences.forEach {
            val sentenceId = sentenceRepository.insert(
                it.copy(lessonId = lessonId).toEntity()
            )
            it.symbols.forEach { symbol ->
                symbolRepository.insert(
                    symbol.copy(sentenceId = sentenceId).toEntity()
                )
            }
        }
    }

    private suspend fun updateLesson(local: LessonSuite, remote: LessonSuite) {
        if (local.lesson.updatedAt == remote.lesson.updatedAt) {
            return
        }

        val lesson = remote.lesson.copy(id = local.lesson.id)
        lessonRepository.update(lesson.toEntity())

        updateCards(local.cards, remote.cards)
        updateSentences(local.sentences, remote.sentences)
    }

    private suspend fun updateCards(local: List<CardItem>, remote: List<CardItem>) {
        val localMap = local.associateBy { it.symbol }
        val remoteMap = remote.associateBy { it.symbol }

        val cardsToInsert = remote.filter { it.symbol !in localMap }

        val cardsToDelete = local.filter { it.symbol !in remoteMap }

        val cardsToUpdate = remote.filter { remoteCard ->
            val localCard = localMap[remoteCard.symbol]
            localCard != null && localCard.image != remoteCard.image
        }

        if (cardsToInsert.isNotEmpty()) {
            cardRepository.insertAll(cardsToInsert.map { it.toEntity() })
        }

        if (cardsToDelete.isNotEmpty()) {
            cardRepository.deleteAll(cardsToDelete.map { it.toEntity() })
        }

        if (cardsToUpdate.isNotEmpty()) {
            cardRepository.updateAll(cardsToUpdate.map { it.toEntity() })
        }
    }

    private suspend fun updateSentences(local: List<SentenceItem>, remote: List<SentenceItem>) {
        val localSentenceMap = local.associateBy { it.fingerprint }
        val remoteSentenceMap = remote.associateBy { it.fingerprint }

        val sentencesToDelete = local.filter { it.fingerprint !in remoteSentenceMap }
        if (sentencesToDelete.isNotEmpty()) {
            sentenceRepository.deleteAll(
                sentencesToDelete.map { it.toEntity() }
            )
        }

        for (remoteSentence in remote) {
            val fingerprint = remoteSentence.fingerprint
            val localSentence = localSentenceMap[fingerprint]

            if (localSentence == null) {
                sentenceRepository.insert(remoteSentence.toEntity())
                symbolRepository.insertAll(remoteSentence.symbols.map { it.toEntity() })
            } else {
                if (localSentence.sort != remoteSentence.sort) {
                    val sentenceToUpdate = localSentence.copy(sort = remoteSentence.sort)
                    sentenceRepository.update(sentenceToUpdate.toEntity())
                }
                updateSymbols(
                    localSymbols = localSentence.symbols,
                    remoteSymbols = remoteSentence.symbols,
                    sentenceId = localSentence.id
                )
            }
        }
    }

    private suspend fun updateSymbols(
        localSymbols: List<SymbolItem>,
        remoteSymbols: List<SymbolItem>,
        sentenceId: Long
    ) {
        val localSymbolMap = localSymbols.associateBy { it.name }
        val remoteSymbolMap = remoteSymbols.associateBy { it.name }

        val symbolsToInsert = remoteSymbols
            .filter { it.name !in localSymbolMap }
            .map { remoteSymbol ->
                remoteSymbol.copy(sentenceId = sentenceId)
            }

        val symbolsToDelete = localSymbols.filter { it.name !in remoteSymbolMap }

        val symbolsToUpdate = remoteSymbols
            .mapNotNull { remoteSymbol ->
                val localSymbol = localSymbolMap[remoteSymbol.name]
                if (localSymbol != null && localSymbol.sort != remoteSymbol.sort) {
                    localSymbol.copy(sort = remoteSymbol.sort)
                } else {
                    null
                }
            }

        if (symbolsToInsert.isNotEmpty()) {
            symbolRepository.insertAll(symbolsToInsert.map { it.toEntity() })
        }

        if (symbolsToDelete.isNotEmpty()) {
            symbolRepository.deleteAll(symbolsToDelete.map { it.toEntity() })
        }

        if (symbolsToUpdate.isNotEmpty()) {
            symbolRepository.updateAll(symbolsToUpdate.map { it.toEntity() })
        }
    }
}