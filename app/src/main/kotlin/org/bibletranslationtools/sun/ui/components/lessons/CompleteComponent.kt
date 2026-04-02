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
import org.bibletranslationtools.sun.data.entity.SettingEntity
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.DataMapper
import org.bibletranslationtools.sun.ui.model.GroupId
import org.bibletranslationtools.sun.ui.model.LessonItem
import org.bibletranslationtools.sun.utils.Section
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface CompleteComponent : ParentContext {
    val model: Value<Model>

    data class Model(
        val lesson: LessonItem? = null,
        val section: Section = Section.LEARN_SYMBOLS,
        val sectionTitle: Int = R.string.learn_symbols_completed,
        val onNext: () -> Unit = {}
    )

    fun onNextClicked()
}

class DefaultCompleteComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    private val lessonId: Long,
    private val section: Section,
    private val groupId: GroupId,
    private val onStartLesson: (Long, Section) -> Unit,
    private val onNextSection: (LessonsComponent.Intent) -> Unit
) : CompleteComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val dataMapper: DataMapper by inject()
    private val settingsRepository: SettingsRepository by inject()
    private val lessonRepository: LessonRepository by inject()

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _model = MutableValue(CompleteComponent.Model())
    override val model: Value<CompleteComponent.Model> = _model

    init {
        componentScope.launch {
            val lesson = lessonRepository.get(lessonId)
            _model.update { it.copy(lesson = lesson?.let(dataMapper::toItem)) }

            setupNextAction()
        }
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

    private suspend fun getNextLesson(id: Long): Long {
        val lessons = lessonRepository.getGroup(groupId).map { it.id }
        val current = lessons.indexOf(id)
        var next = lessons.first()
        if (current < lessons.size - 1) {
            next = lessons[current + 1]
        }
        return next
    }

    private suspend fun saveSectionStatus(lessonId: Long, section: Section) {
        val lastSection = SettingEntity(
            SettingEntity.lastSection(groupId.id),
            section.id
        )
        val lastLesson = SettingEntity(
            SettingEntity.lastLesson(groupId.id),
            lessonId.toString()
        )
        settingsRepository.insertOrUpdate(lastSection)
        settingsRepository.insertOrUpdate(lastLesson)
    }

    private fun setupNextAction() {
        when (section) {
            Section.LEARN_SYMBOLS -> {
                _model.update {
                    it.copy(
                        sectionTitle = R.string.learn_symbols_completed,
                        onNext = { onNextSection(LessonsComponent.Intent.TestSymbol(lessonId)) }
                    )
                }
            }
            Section.TEST_SYMBOLS -> {
                _model.update {
                    it.copy(
                        sectionTitle = R.string.test_symbols_completed,
                        onNext = { onNextSection(LessonsComponent.Intent.LearnSentence(lessonId)) }
                    )
                }
            }
            Section.LEARN_SENTENCES -> {
                _model.update {
                    it.copy(
                        sectionTitle = R.string.learn_sentences_completed,
                        onNext = { onNextSection(LessonsComponent.Intent.TestSentence(lessonId)) }
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