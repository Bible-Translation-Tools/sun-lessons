package org.bibletranslationtools.sun.ui.components.splash

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bibletranslationtools.sun.data.model.CardData
import org.bibletranslationtools.sun.data.model.LessonData
import org.bibletranslationtools.sun.data.model.LessonSuite
import org.bibletranslationtools.sun.data.model.SentenceData
import org.bibletranslationtools.sun.data.model.SettingEntity
import org.bibletranslationtools.sun.data.model.SymbolData
import org.bibletranslationtools.sun.data.model.toEntity
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
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

                val lessonSuite: LessonSuite = Utils.JsonLenient.decodeFromString(json)

                if (lessonSuite.version > dbVersion) {
                    for (lesson in lessonSuite.lessons) {
                        insertLesson(lesson)

                        for (card in lesson.cards) {
                            insertCard(card.copy(lessonId = lesson.id))
                        }

                        for (sentence in lesson.sentences) {
                            insertSentence(sentence.copy(lessonId = lesson.id))
                            for (symbol in sentence.symbols) {
                                insertSymbol(symbol.copy(sentenceId = sentence.id))
                            }
                        }
                    }

                    insertSetting(
                        SettingEntity(
                            SettingEntity.VERSION,
                            lessonSuite.version.toString()
                        )
                    )
                }
            }

            delay(1000)

            onInitDone()
        }
    }

    private suspend fun insertLesson(lesson: LessonData) {
        lessonRepository.insert(lesson.toEntity())
    }

    private suspend fun insertCard(card: CardData) {
        cardRepository.insert(card.toEntity())
    }

    private fun insertSentence(sentence: SentenceData) {
        componentScope.launch {
            sentenceRepository.insert(sentence.toEntity())
        }
    }

    private fun insertSymbol(symbol: SymbolData) {
        componentScope.launch {
            sentenceRepository.insert(symbol.toEntity())
        }
    }

    private suspend fun getVersion(): Int? {
        return settingsRepository.get("version")?.value?.toInt()
    }

    private suspend fun insertSetting(setting: SettingEntity) {
        settingsRepository.insert(setting)
    }
}