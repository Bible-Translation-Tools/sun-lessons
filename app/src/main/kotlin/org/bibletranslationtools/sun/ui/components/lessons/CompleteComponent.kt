package org.bibletranslationtools.sun.ui.components.lessons

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.data.model.SettingEntity
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.LessonsIntent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.utils.Section
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface CompleteComponent : ParentContext {
    val model: Value<Model>

    data class Model(
        val lessonId: Int = 0,
        val section: Section = Section.LEARN_SYMBOLS,
        val sectionTitle: Int = R.string.learn_symbols_completed,
        val onNext: () -> Unit = {}
    )

    fun onNextClicked()
}

class DefaultCompleteComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    private val lessonId: Int,
    private val section: Section,
    private val onStartLesson: (Int, Section) -> Unit,
    private val onNextSection: (LessonsIntent) -> Unit
) : CompleteComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val settingsRepository: SettingsRepository by inject()
    private val lessonRepository: LessonRepository by inject()

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _model = MutableValue(CompleteComponent.Model())
    override val model: Value<CompleteComponent.Model> = _model

    init {
        _model.update { it.copy(lessonId = lessonId, section = section) }
        setupNextAction()
    }

    override fun onNextClicked() {
        model.value.onNext()
    }

    private fun navigateToNextLesson() {
        componentScope.launch {
            val next = getNextLesson(lessonId)
            saveSectionStatus(next, Section.LEARN_SYMBOLS)
            onStartLesson(next, Section.LEARN_SYMBOLS)
        }
    }

    private suspend fun getNextLesson(id: Int): Int {
        val lessons = lessonRepository.getAll().map { it.id }
        val current = lessons.indexOf(id)
        var next = 1
        if (current < lessons.size - 1) {
            next = lessons[current + 1]
        }
        return next
    }

    private suspend fun saveSectionStatus(lessonId: Int, section: Section) {
        val lastSection = SettingEntity(SettingEntity.LAST_SECTION, section.id)
        val lastLesson = SettingEntity(SettingEntity.LAST_LESSON, lessonId.toString())
        settingsRepository.insertOrUpdate(lastSection)
        settingsRepository.insertOrUpdate(lastLesson)
    }

    private fun setupNextAction() {
        componentScope.launch {
            when (section) {
                Section.LEARN_SYMBOLS -> {
                    _model.update {
                        it.copy(
                            sectionTitle = R.string.learn_symbols_completed,
                            onNext = { onNextSection(LessonsIntent.TestSymbol(lessonId)) }
                        )
                    }
                }
                Section.TEST_SYMBOLS -> {
                    _model.update {
                        it.copy(
                            sectionTitle = R.string.test_symbols_completed,
                            onNext = { onNextSection(LessonsIntent.LearnSentence(lessonId)) }
                        )
                    }
                }
                Section.LEARN_SENTENCES -> {
                    _model.update {
                        it.copy(
                            sectionTitle = R.string.learn_sentences_completed,
                            onNext = { onNextSection(LessonsIntent.TestSentence(lessonId)) }
                        )
                    }
                }
                else -> {
                    _model.update {
                        it.copy(
                            sectionTitle = R.string.lesson_completed,
                            onNext = { navigateToNextLesson() }
                        )
                    }
                }
            }
        }
    }
}