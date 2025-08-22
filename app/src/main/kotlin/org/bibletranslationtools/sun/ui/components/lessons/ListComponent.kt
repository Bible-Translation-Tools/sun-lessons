package org.bibletranslationtools.sun.ui.components.lessons

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnResume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.LessonMode
import org.bibletranslationtools.sun.ui.model.LessonSuite
import org.bibletranslationtools.sun.ui.model.LessonType
import org.bibletranslationtools.sun.ui.model.toItem
import org.bibletranslationtools.sun.utils.Section
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ListComponent : ParentContext {

    val model: Value<Model>

    data class Model(
        val lessons: List<LessonSuite> = emptyList(),
        val selectedId: Long = 1,
        val nextLessonId: Long = 1,
        val nextSection: Section = Section.LEARN_SYMBOLS,
        val nextState: SectionState = SectionState.NOT_STARTED,
        val lessonType: LessonType = LessonType.BASIC
    )

    fun onLearnClicked()
    fun onLessonAction(lessonId: Long, action: Section)
}

class DefaultListComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    private val lessonType: LessonType,
    private val onContinueLesson: (Long, Section, SectionState) -> Unit,
    private val onStartLesson: (Long, Section, LessonMode) -> Unit
) : ListComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val settingsRepository: SettingsRepository by inject()
    private val lessonRepository: LessonRepository by inject()
    private val cardRepository: CardRepository by inject()
    private val sentenceRepository: SentenceRepository by inject()

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _model = MutableValue(ListComponent.Model())
    override val model: Value<ListComponent.Model> = _model

    init {
        _model.update { it.copy(lessonType = lessonType) }

        doOnResume {
            componentScope.launch {
                defineNextSection()
                setSelectedLesson()
                loadLessons()
            }
        }
    }

    override fun onLearnClicked() {
        onContinueLesson(
            model.value.nextLessonId,
            model.value.nextSection,
            model.value.nextState
        )
    }

    override fun onLessonAction(lessonId: Long, action: Section) {
        onStartLesson(lessonId, action, LessonMode.REPEAT)
    }

    private suspend fun loadLessons() {
        withContext(Dispatchers.Default) {
            val lessons = lessonRepository.getAllWithData(lessonType).map { it.toItem() }
            _model.update {
                it.copy(lessons = lessons.mapIndexed { index, lesson ->
                    lesson.copy(
                        isAvailable = lessonAvailable(lessons, index),
                        isSelected = lesson.lesson.id == model.value.selectedId
                    )
                })
            }
        }
    }

    private suspend fun setSelectedLesson() {
        val lastLesson = settingsRepository.get("last_lesson")?.value?.toLong() ?: 1
        _model.update { it.copy(selectedId = lastLesson) }
    }

    private fun lessonAvailable(lessons: List<LessonSuite>, position: Int): Boolean {
        if (position == 0) return true
        val prevLesson = lessons[position - 1]
        return prevLesson.totalProgress == 100.0
    }

    private suspend fun defineNextSection() {
        val lastSection = settingsRepository
            .get("last_section")
            ?.value
            ?.let { Section.of(it) } ?: Section.LEARN_SYMBOLS
        val lastLesson = settingsRepository.get("last_lesson")?.value?.toLong() ?: 1L

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

        _model.update {
            it.copy(
                nextLessonId = lastLesson + 1,
                nextSection = lastSection,
                nextState = sectionState
            )
        }
    }
}