package org.bibletranslationtools.sun.usecase

import androidx.room.Transaction
import coil3.ImageLoader
import coil3.request.ImageRequest
import org.bibletranslationtools.sun.api.LessonRequest
import org.bibletranslationtools.sun.api.SunApi
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.data.repositories.SymbolRepository
import org.bibletranslationtools.sun.ui.model.CardItem
import org.bibletranslationtools.sun.ui.model.GroupId
import org.bibletranslationtools.sun.ui.model.LessonGroup
import org.bibletranslationtools.sun.ui.model.LessonItem
import org.bibletranslationtools.sun.ui.model.SentenceItem
import org.bibletranslationtools.sun.ui.model.SymbolItem
import org.bibletranslationtools.sun.ui.model.toEntity
import org.bibletranslationtools.sun.ui.model.toItem

class DownloadLesson(
    private val sunApi: SunApi,
    private val lessonRepository: LessonRepository,
    private val cardRepository: CardRepository,
    private val sentenceRepository: SentenceRepository,
    private val symbolRepository: SymbolRepository,
    private val imageLoader: ImageLoader,
    private val imageRequestBuilder: ImageRequest.Builder
) {
    suspend operator fun invoke(groupId: GroupId) {
        val localLessons = lessonRepository.getGroupWithData(groupId).map {
            it.toItem()
        }
        val localGroup: LessonGroup? = if (localLessons.isNotEmpty()) {
            LessonGroup(
                groupId = localLessons.first().groupId,
                lessons = localLessons
            )
        } else {
            null
        }

        val remoteLessons = sunApi.getLessonCatalog(
            LessonRequest(
                book = groupId.book,
                chapter = groupId.chapter,
                verse = groupId.verse,
                author = groupId.author
            )
        )
            .lessons.map { it.toItem() }

        val remoteGroup: LessonGroup? = if (remoteLessons.isNotEmpty()) {
            LessonGroup(
                groupId = remoteLessons.first().groupId,
                lessons = remoteLessons
            )
        } else {
            null
        }

        when {
            localGroup != null && remoteGroup != null -> {
                updateLessons(localGroup, remoteGroup)
            }
            remoteGroup != null -> insertLessons(remoteGroup)
        }
    }

    private suspend fun insertLessons(group: LessonGroup) {
        group.lessons.forEach { lesson ->
            insertLesson(lesson)
        }
    }

    private suspend fun updateLessons(local: LessonGroup, remote: LessonGroup) {
        val localIds = local.lessons.map { it.uniqueId }
        remote.lessons.forEach { remoteLesson ->
            val id = remoteLesson.uniqueId
            if (id in localIds) {
                val localLesson = local.lessons.first { it.uniqueId == id }
                if (localLesson.updatedAt == remoteLesson.updatedAt) {
                    return
                } else {
                    updateLesson(localLesson, remoteLesson)
                }
            } else {
                insertLesson(remoteLesson)
            }
        }
    }

    @Transaction
    private suspend fun insertLesson(lesson: LessonItem) {
        val lessonId = lessonRepository.insert(
            lesson.toEntity()
        )

        lesson.cards
            .map { it.copy(lessonId = lessonId) }
            .forEach {
                cacheImage(it.image)
                cardRepository.insert(it.toEntity())
            }

        lesson.sentences
            .map { it.copy(lessonId = lessonId) }
            .forEach { sentence ->
                sentence.image?.let { cacheImage(it) }
                val sentenceId = sentenceRepository.insert(sentence.toEntity())
                sentence.symbols
                    .map { it.copy(sentenceId = sentenceId) }
                    .forEach { symbol ->
                        symbolRepository.insert(symbol.toEntity())
                    }
            }
    }

    @Transaction
    private suspend fun updateLesson(local: LessonItem, remote: LessonItem) {
        val lesson = remote.copy(id = local.id)
        lessonRepository.update(lesson.toEntity())

        updateCards(local.cards, remote.cards, local.id)
        updateSentences(local.sentences, remote.sentences, local.id)
    }

    private suspend fun updateCards(
        local: List<CardItem>,
        remote: List<CardItem>,
        lessonId: Long
    ) {
        val localMap = local.associateBy { it.symbol }
        val remoteMap = remote.associateBy { it.symbol }

        val cardsToInsert = remote.filter { it.symbol !in localMap }
            .map { it.copy(lessonId = lessonId) }

        val cardsToDelete = local.filter { it.symbol !in remoteMap }

        val cardsToUpdate = remote
            .filter { remoteCard ->
                val localCard = localMap[remoteCard.symbol]
                localCard != null && localCard.image != remoteCard.image
            }
            .map { it.copy(lessonId = lessonId) }

        if (cardsToInsert.isNotEmpty()) {
            cacheImages(cardsToInsert.map { it.image })
            cardRepository.insertAll(cardsToInsert.map { it.toEntity() })
        }

        if (cardsToDelete.isNotEmpty()) {
            cardRepository.deleteAll(cardsToDelete.map { it.toEntity() })
        }

        if (cardsToUpdate.isNotEmpty()) {
            cacheImages(cardsToInsert.map { it.image })
            cardRepository.updateAll(cardsToUpdate.map { it.toEntity() })
        }
    }

    private suspend fun updateSentences(
        local: List<SentenceItem>,
        remote: List<SentenceItem>,
        lessonId: Long
    ) {
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
                remoteSentence.image?.let { cacheImage(it) }
                val sentenceId = sentenceRepository.insert(
                    remoteSentence.copy(lessonId = lessonId).toEntity()
                )
                symbolRepository.insertAll(remoteSentence.symbols.map {
                    it.copy(sentenceId = sentenceId).toEntity()
                })
            } else {
                if (localSentence.sort != remoteSentence.sort) {
                    val sentenceToUpdate = localSentence.copy(sort = remoteSentence.sort)
                    sentenceToUpdate.image?.let { cacheImage(it) }
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

    private fun cacheImage(url: String) {
        val request = imageRequestBuilder.data(url).build()
        imageLoader.enqueue(request)
    }

    private fun cacheImages(urls: List<String>) {
        urls.forEach { cacheImage(it) }
    }
}