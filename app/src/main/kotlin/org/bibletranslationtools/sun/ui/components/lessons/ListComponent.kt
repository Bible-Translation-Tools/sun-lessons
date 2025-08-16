package org.bibletranslationtools.sun.ui.components.lessons

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.LessonItem
import org.bibletranslationtools.sun.ui.model.LessonMode
import org.bibletranslationtools.sun.ui.model.toItem
import org.bibletranslationtools.sun.utils.Section
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ListComponent : ParentContext {

    val model: Value<Model>

    data class Model(
        val lessons: List<LessonItem> = emptyList(),
        val selectedId: Int = 1
    )

    fun onLessonAction(lessonId: Int, action: Section)
}

class DefaultListComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    selectedLessonId: Int,
    private val onStartLesson: (Int, Section, LessonMode) -> Unit
) : ListComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val lessonRepository: LessonRepository by inject()

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _model = MutableValue(ListComponent.Model())
    override val model: Value<ListComponent.Model> = _model

    init {
        loadLessons()
        setSelectedLesson(selectedLessonId)
    }

    override fun onLessonAction(lessonId: Int, action: Section) {
        onStartLesson(lessonId, action, LessonMode.REPEAT)
    }

    private fun loadLessons(): Job {
        return componentScope.launch {
            val lessons = lessonRepository.getAllWithData().map { it.toItem() }
            _model.update {
                it.copy(lessons = lessons.mapIndexed { index, lesson ->
                    lesson.copy(
                        isAvailable = lessonAvailable(lessons, index),
                        isSelected = lesson.lesson.id == _model.value.selectedId
                    )
                })
            }
        }
    }

    private fun setSelectedLesson(lessonId: Int) {
        _model.update { it.copy(selectedId = lessonId) }
    }

    private fun lessonAvailable(lessons: List<LessonItem>, position: Int): Boolean {
        if (position == 0) return true
        val prevLesson = lessons[position - 1]
        return prevLesson.totalProgress == 100.0
    }
}