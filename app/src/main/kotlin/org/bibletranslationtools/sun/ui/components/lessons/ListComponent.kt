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
import org.bibletranslationtools.sun.ui.mapper.LessonMapper
import org.bibletranslationtools.sun.ui.model.LessonMode
import org.bibletranslationtools.sun.ui.model.LessonModel
import org.bibletranslationtools.sun.utils.Section
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ListComponent : ParentContext {

    val model: Value<Model>

    data class Model(
        val lessons: List<LessonModel> = emptyList(),
        val selectedId: Int = 1
    )

    fun onLessonSelected(lesson: LessonModel, position: Int)
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

    override fun onLessonSelected(lesson: LessonModel, position: Int) {
        setSelectedLesson(lesson.lesson.id)

        _model.value.lessons.indexOfFirst { it.isSelected }.let { prevPosition ->
            if (prevPosition >= 0 && prevPosition != position) {
                _model.value.lessons[prevPosition].let { prevLesson ->
                    prevLesson.isSelected = false
                }
            }
        }

        lesson.isSelected = !lesson.isSelected
    }

    override fun onLessonAction(lessonId: Int, action: Section) {
        onStartLesson(lessonId, action, LessonMode.REPEAT)
    }

    private fun loadLessons(): Job {
        return componentScope.launch {
            val lessons = lessonRepository.getAllWithData().map(LessonMapper::map)
            lessons.forEachIndexed { index, lesson ->
                lesson.isAvailable = lessonAvailable(lessons, index)
                if (lesson.lesson.id == _model.value.selectedId) {
                    lesson.isSelected = true
                }
            }
            _model.update { it.copy(lessons = lessons) }
        }
    }

    private fun setSelectedLesson(lessonId: Int) {
        _model.update { it.copy(selectedId = lessonId) }
    }

    private fun lessonAvailable(lessons: List<LessonModel>, position: Int): Boolean {
        if (position == 0) return true
        val prevLesson = lessons[position - 1]
        return prevLesson.totalProgress == 100.0
    }
}