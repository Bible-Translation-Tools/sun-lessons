package org.bibletranslationtools.sun.ui.components.home

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.utils.AssetReader
import org.bibletranslationtools.sun.utils.Section
import org.bibletranslationtools.sun.utils.Utils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface HomeComponent : ParentContext {
    fun onLearnClicked()
}

class DefaultHomeComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    private val onNavigateLearn: (Section, Int, SectionState) -> Unit
) : HomeComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val cardRepository: CardRepository by inject()
    private val lessonRepository: LessonRepository by inject()
    private val settingsRepository: SettingsRepository by inject()
    private val sentenceRepository: SentenceRepository by inject()
    private val assetReader: AssetReader by inject()

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        importLessons()
    }

    override fun onLearnClicked() {
        componentScope.launch {
            navigateToSection { lastSection: Section, lastLesson: Int, state: SectionState ->
                onNavigateLearn(lastSection, lastLesson, state)
            }
        }
    }

    private fun importLessons(): Job {
        return componentScope.launch {
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
                    SettingEntity(SettingEntity.VERSION, lessonSuite.version.toString())
                )
            }
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

    suspend fun navigateToSection(callback: (Section, Int, SectionState) -> Unit) {
        val lastSection = settingsRepository
            .get("last_section")
            ?.value
            ?.let { Section.of(it) } ?: Section.LEARN_SYMBOLS
        val lastLesson = settingsRepository.get("last_lesson")?.value?.toInt() ?: 1

        val all: Int
        val done: Int

        when (lastSection) {
            Section.LEARN_SYMBOLS -> {
                all = cardRepository.getByLessonCount(lastLesson)
                done = cardRepository.getLearnedByLessonCount(lastLesson)
            }
            Section.TEST_SYMBOLS -> {
                all = cardRepository.getByLessonCount(lastLesson)
                done = cardRepository.getTestedByLessonCount(lastLesson)
            }
            Section.LEARN_SENTENCES -> {
                all = sentenceRepository.getByLessonCount(lastLesson)
                done = sentenceRepository.getLearnedByLessonCount(lastLesson)
            }
            else -> {
                all = sentenceRepository.getByLessonCount(lastLesson)
                done = sentenceRepository.getTestedByLessonCount(lastLesson)
            }
        }

        val sectionState = when {
            done == all -> SectionState.COMPLETED
            done > 0 -> SectionState.IN_PROGRESS
            else -> SectionState.NOT_STARTED
        }

        return callback(lastSection, lastLesson, sectionState)
    }

    enum class SectionState {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }
}