package org.bibletranslationtools.sun.ui.components.splash

import coil3.ImageLoader
import coil3.request.ImageRequest
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bibletranslationtools.sun.api.CardData
import org.bibletranslationtools.sun.api.LessonCatalog
import org.bibletranslationtools.sun.api.LessonData
import org.bibletranslationtools.sun.api.SentenceData
import org.bibletranslationtools.sun.api.SymbolData
import org.bibletranslationtools.sun.data.entity.SettingEntity
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.data.repositories.SymbolRepository
import org.bibletranslationtools.sun.ui.model.DataMapper
import org.bibletranslationtools.sun.utils.AssetReader
import org.bibletranslationtools.sun.utils.Utils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface SplashComponent

class DefaultSplashComponent(
    componentContext: ComponentContext,
    private val onInitDone: () -> Unit
) : SplashComponent, KoinComponent, ComponentContext by componentContext {

    private val dataMapper: DataMapper by inject()
    private val cardRepository: CardRepository by inject()
    private val lessonRepository: LessonRepository by inject()
    private val settingsRepository: SettingsRepository by inject()
    private val sentenceRepository: SentenceRepository by inject()
    private val symbolRepository: SymbolRepository by inject()
    private val assetReader: AssetReader by inject()
    private val imageLoader: ImageLoader by inject()
    private val imageRequestBuilder: ImageRequest.Builder by inject()

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        importLessons()
    }

    private fun importLessons() {
        componentScope.launch {
            withContext(Dispatchers.Default) {
                val json = assetReader.readText("lessons.json")

                val dbVersion = getVersion() ?: 0

                val lessonCatalog: LessonCatalog = Utils.JsonLenient.decodeFromString(json)

                if (lessonCatalog.version > dbVersion) {
                    for (lesson in lessonCatalog.lessons) {
                        val lessonId = insertLesson(lesson)
                        var sort = 0

                        for (card in lesson.cards) {
                            insertCard(
                                card = card.copy(sort = sort++),
                                lessonId = lessonId
                            )
                        }

                        sort = 0
                        for (sentence in lesson.sentences) {
                            val sentenceId = insertSentence(
                                sentence = sentence.copy(sort = sort++),
                                lessonId = lessonId
                            )
                            var symbolSort = 0
                            for (symbol in sentence.symbols) {
                                insertSymbol(
                                    symbol = symbol.copy(sort = symbolSort++),
                                    sentenceId = sentenceId
                                )
                            }
                        }
                    }

                    insertSetting(
                        SettingEntity(
                            SettingEntity.VERSION,
                            lessonCatalog.version.toString()
                        )
                    )
                }
            }

            delay(2000)

            onInitDone()
        }
    }

    private suspend fun insertLesson(lesson: LessonData): Long {
        return lessonRepository.insert(lesson.let(dataMapper::toEntity))
    }

    private suspend fun insertCard(card: CardData, lessonId: Long): Long {
        val imageUrl = "file:///android_asset/images/symbols/${card.image}"
        val request = imageRequestBuilder.data(imageUrl).build()
        imageLoader.enqueue(request)

        return cardRepository.insert(
            card.let(dataMapper::toEntity).copy(
                lessonId = lessonId,
                image = imageUrl
            )
        )
    }

    private suspend fun insertSentence(sentence: SentenceData, lessonId: Long): Long {
        val imageUrl = "file:///android_asset/images/sentences/${sentence.image}"
        val request = imageRequestBuilder.data(imageUrl).build()
        imageLoader.enqueue(request)

        return sentenceRepository.insert(
            sentence.let(dataMapper::toEntity).copy(
                lessonId = lessonId,
                image = imageUrl
            )
        )
    }

    private fun insertSymbol(symbol: SymbolData, sentenceId: Long) {
        componentScope.launch {
            symbolRepository.insert(
                symbol.let(dataMapper::toEntity).copy(sentenceId = sentenceId)
            )
        }
    }

    private suspend fun getVersion(): Int? {
        return settingsRepository.get("version")?.value?.toInt()
    }

    private suspend fun insertSetting(setting: SettingEntity) {
        settingsRepository.insert(setting)
    }
}