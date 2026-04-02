package org.bibletranslationtools.sun.usecase

import androidx.room.Transaction
import coil3.ImageLoader
import coil3.request.ImageRequest
import org.bibletranslationtools.sun.api.SunApi
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.data.repositories.SymbolRepository
import org.bibletranslationtools.sun.ui.model.CardItem
import org.bibletranslationtools.sun.ui.model.DataMapper
import org.bibletranslationtools.sun.ui.model.GroupId
import org.bibletranslationtools.sun.ui.model.LessonGroup
import org.bibletranslationtools.sun.ui.model.LessonItem
import org.bibletranslationtools.sun.ui.model.SentenceItem
import org.bibletranslationtools.sun.ui.model.SymbolItem

class DownloadLesson(
    private val sunApi: SunApi,
    private val dataMapper: DataMapper,
    private val lessonRepository: LessonRepository,
    private val cardRepository: CardRepository,
    private val sentenceRepository: SentenceRepository,
    private val symbolRepository: SymbolRepository,
    private val imageLoader: ImageLoader,
    private val imageRequestBuilder: ImageRequest.Builder
) {
    suspend operator fun invoke(groupId: GroupId, onProgress: (Float) -> Unit) {
        val localLessons = lessonRepository.getGroupWithData(groupId)
            .map(dataMapper::toItem)
        val localGroup: LessonGroup? = if (localLessons.isNotEmpty()) {
            LessonGroup(
                groupId = localLessons.first().groupId,
                lessons = localLessons
            )
        } else {
            null
        }

        val remoteLessons = sunApi.getLessonCatalog(groupId)
            .lessons.map(dataMapper::toItem)

        val remoteGroup: LessonGroup? = if (remoteLessons.isNotEmpty()) {
            LessonGroup(
                groupId = remoteLessons.first().groupId,
                lessons = remoteLessons
            )
        } else {
            null
        }

        val totalItems = remoteGroup?.lessons?.sumOf {
            1 + it.cards.size + it.sentences.size
        } ?: 0
        var processedItems = 0

        val reportProgress: (Int) -> Unit = { count ->
            if (totalItems > 0 && count > 0) {
                processedItems += count
                val progress = processedItems.toFloat() / totalItems
                onProgress(progress.coerceIn(0f, 1f))
            }
        }

        when {
            localGroup != null && remoteGroup != null -> {
                updateLessons(localGroup, remoteGroup, reportProgress)
            }
            remoteGroup != null -> insertLessons(remoteGroup, reportProgress)
        }

        onProgress(1f)
    }

    private suspend fun insertLessons(group: LessonGroup, reportProgress: (Int) -> Unit) {
        group.lessons.forEach { lesson ->
            insertLesson(lesson, reportProgress)
        }
    }

    private suspend fun updateLessons(
        local: LessonGroup,
        remote: LessonGroup,
        reportProgress: (Int) -> Unit
    ) {
        val localIds = local.lessons.map { it.uniqueId }
        remote.lessons.forEach { remoteLesson ->
            val id = remoteLesson.uniqueId
            if (id in localIds) {
                val localLesson = local.lessons.first { it.uniqueId == id }
                if (localLesson.updatedAt != remoteLesson.updatedAt) {
                    updateLesson(localLesson, remoteLesson, reportProgress)
                } else {
                    // Lesson is unchanged; report progress for it and all its contents at once.
                    val numItemsInLesson = 1 + remoteLesson.cards.size + remoteLesson.sentences.size
                    reportProgress(numItemsInLesson)
                }
            } else {
                insertLesson(remoteLesson, reportProgress)
            }
        }
    }

    @Transaction
    private suspend fun insertLesson(lesson: LessonItem, reportProgress: (Int) -> Unit) {
        val lessonId = lessonRepository.insert(
            lesson.let(dataMapper::toEntity)
        )

        reportProgress(1)

        lesson.cards
            .map { it.copy(lessonId = lessonId) }
            .forEach {
                cacheImage(it.image)
                cardRepository.insert(it.let(dataMapper::toEntity))
                reportProgress(1)
            }

        lesson.sentences
            .map { it.copy(lessonId = lessonId) }
            .forEach { sentence ->
                sentence.image?.let { cacheImage(it) }
                val sentenceId = sentenceRepository.insert(
                    sentence.let(dataMapper::toEntity)
                )
                sentence.symbols
                    .map { it.copy(sentenceId = sentenceId) }
                    .forEach { symbol ->
                        symbolRepository.insert(symbol.let(dataMapper::toEntity))
                    }
                reportProgress(1)
            }
    }

    @Transaction
    private suspend fun updateLesson(
        local: LessonItem,
        remote: LessonItem,
        reportProgress: (Int) -> Unit
    ) {
        val lesson = remote.copy(id = local.id)
        lessonRepository.update(lesson.let(dataMapper::toEntity))

        reportProgress(1)

        updateCards(
            local.cards,
            remote.cards,
            local.id,
            reportProgress
        )
        updateSentences(
            local.sentences,
            remote.sentences,
            local.id,
            reportProgress
        )
    }

    private suspend fun updateCards(
        local: List<CardItem>,
        remote: List<CardItem>,
        lessonId: Long,
        reportProgress: (Int) -> Unit
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
            .map { remoteCard ->
                val localId = localMap[remoteCard.symbol]!!.id
                remoteCard.copy(id = localId, lessonId = lessonId)
            }

        val unchangedCount = remote.size - cardsToInsert.size - cardsToUpdate.size

        if (cardsToInsert.isNotEmpty()) {
            cacheImages(cardsToInsert.map { it.image })
            cardRepository.insertAll(cardsToInsert.map(dataMapper::toEntity))
            reportProgress(cardsToInsert.size)
        }

        if (cardsToDelete.isNotEmpty()) {
            cardRepository.deleteAll(cardsToDelete.map(dataMapper::toEntity))
        }

        if (cardsToUpdate.isNotEmpty()) {
            cacheImages(cardsToInsert.map { it.image })
            cardRepository.updateAll(cardsToUpdate.map(dataMapper::toEntity))
            reportProgress(cardsToUpdate.size)
        }

        if (unchangedCount > 0) {
            reportProgress(unchangedCount)
        }
    }

    private suspend fun updateSentences(
        local: List<SentenceItem>,
        remote: List<SentenceItem>,
        lessonId: Long,
        reportProgress: (Int) -> Unit
    ) {
        val localSentenceMap = local.associateBy { it.fingerprint }
        val remoteSentenceMap = remote.associateBy { it.fingerprint }

        val sentencesToDelete = local.filter { it.fingerprint !in remoteSentenceMap }
        if (sentencesToDelete.isNotEmpty()) {
            sentenceRepository.deleteAll(
                sentencesToDelete.map { it.let(dataMapper::toEntity) }
            )
        }

        for (remoteSentence in remote) {
            val fingerprint = remoteSentence.fingerprint
            val localSentence = localSentenceMap[fingerprint]

            if (localSentence == null) {
                remoteSentence.image?.let { cacheImage(it) }
                val sentenceId = sentenceRepository.insert(
                    remoteSentence.copy(lessonId = lessonId).let(dataMapper::toEntity)
                )
                symbolRepository.insertAll(remoteSentence.symbols.map {
                    it.copy(sentenceId = sentenceId).let(dataMapper::toEntity)
                })
            } else {
                if (localSentence.sort != remoteSentence.sort) {
                    val sentenceToUpdate = localSentence.copy(sort = remoteSentence.sort)
                    sentenceToUpdate.image?.let { cacheImage(it) }
                    sentenceRepository.update(sentenceToUpdate.let(dataMapper::toEntity))
                }
                updateSymbols(
                    localSymbols = localSentence.symbols,
                    remoteSymbols = remoteSentence.symbols,
                    sentenceId = localSentence.id
                )
            }
            reportProgress(1)
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
            symbolRepository.insertAll(symbolsToInsert.map(dataMapper::toEntity))
        }

        if (symbolsToDelete.isNotEmpty()) {
            symbolRepository.deleteAll(symbolsToDelete.map(dataMapper::toEntity))
        }

        if (symbolsToUpdate.isNotEmpty()) {
            symbolRepository.updateAll(symbolsToUpdate.map(dataMapper::toEntity))
        }
    }

    private suspend fun cacheImage(url: String) {
        val request = imageRequestBuilder.data(url).build()
        imageLoader.execute(request)
    }

    private suspend fun cacheImages(urls: List<String>) {
        urls.forEach { cacheImage(it) }
    }
}