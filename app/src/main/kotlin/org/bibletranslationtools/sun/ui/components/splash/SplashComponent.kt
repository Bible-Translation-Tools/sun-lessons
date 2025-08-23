package org.bibletranslationtools.sun.ui.components.splash

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bibletranslationtools.sun.data.entity.CardData
import org.bibletranslationtools.sun.data.entity.LessonCatalog
import org.bibletranslationtools.sun.data.entity.LessonData
import org.bibletranslationtools.sun.data.entity.SentenceData
import org.bibletranslationtools.sun.data.entity.SettingEntity
import org.bibletranslationtools.sun.data.entity.SymbolData
import org.bibletranslationtools.sun.data.entity.toEntity
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.data.repositories.SymbolRepository
import org.bibletranslationtools.sun.utils.AssetReader
import org.bibletranslationtools.sun.utils.Utils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface SplashComponent

class DefaultSplashComponent(
    componentContext: ComponentContext,
    private val onInitDone: () -> Unit
) : SplashComponent, KoinComponent, ComponentContext by componentContext {

    private val cardRepository: CardRepository by inject()
    private val lessonRepository: LessonRepository by inject()
    private val settingsRepository: SettingsRepository by inject()
    private val sentenceRepository: SentenceRepository by inject()
    private val symbolRepository: SymbolRepository by inject()
    private val assetReader: AssetReader by inject()

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
        return lessonRepository.insert(lesson.toEntity())
    }

    private suspend fun insertCard(card: CardData, lessonId: Long): Long {
        return cardRepository.insert(card.toEntity().copy(lessonId = lessonId))
    }

    private suspend fun insertSentence(sentence: SentenceData, lessonId: Long): Long {
        return sentenceRepository.insert(sentence.toEntity().copy(lessonId = lessonId))
    }

    private fun insertSymbol(symbol: SymbolData, sentenceId: Long) {
        componentScope.launch {
            symbolRepository.insert(symbol.toEntity().copy(sentenceId = sentenceId))
        }
    }

    private suspend fun getVersion(): Int? {
        return settingsRepository.get("version")?.value?.toInt()
    }

    private suspend fun insertSetting(setting: SettingEntity) {
        settingsRepository.insert(setting)
    }
}