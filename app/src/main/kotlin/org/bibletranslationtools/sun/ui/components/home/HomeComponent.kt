package org.bibletranslationtools.sun.ui.components.home

import com.arkivanov.decompose.ComponentContext
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.data.model.Lesson
import org.bibletranslationtools.sun.data.model.LessonSuite
import org.bibletranslationtools.sun.data.model.Sentence
import org.bibletranslationtools.sun.data.model.Setting
import org.bibletranslationtools.sun.data.model.Symbol
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.utils.AssetReader
import org.bibletranslationtools.sun.utils.Section
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
            val mapper = ObjectMapper().registerKotlinModule()
            val reference = object : TypeReference<LessonSuite>() {}
            val json = assetReader.readText("lessons.json")

            val dbVersion = getVersion() ?: 0

            val lessonSuite = mapper.readValue(json, reference)

            if (lessonSuite.version > dbVersion) {
                for (lesson in lessonSuite.lessons) {
                    insertLesson(lesson)

                    for (card in lesson.cards) {
                        card.lessonId = lesson.id
                        insertCard(card)
                    }

                    for (sentence in lesson.sentences) {
                        sentence.lessonId = lesson.id
                        insertSentence(sentence)
                        for (symbol in sentence.symbols) {
                            symbol.sentenceId = sentence.id
                            insertSymbol(symbol)
                        }
                    }
                }

                insertSetting(
                    Setting(Setting.VERSION, lessonSuite.version.toString())
                )
            }
        }
    }

    private suspend fun insertLesson(lesson: Lesson) {
        lessonRepository.insert(lesson)
    }

    private suspend fun insertCard(card: Card) {
        cardRepository.insert(card)
    }

    private fun insertSentence(sentence: Sentence) {
        componentScope.launch {
            sentenceRepository.insert(sentence)
        }
    }

    private fun insertSymbol(symbol: Symbol) {
        componentScope.launch {
            sentenceRepository.insert(symbol)
        }
    }

    private suspend fun getVersion(): Int? {
        return settingsRepository.get("version")?.value?.toInt()
    }

    private suspend fun insertSetting(setting: Setting) {
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