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
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.GroupId
import org.bibletranslationtools.sun.ui.model.LessonGroup
import org.bibletranslationtools.sun.ui.model.DataMapper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ScriptureLessonsComponent : ParentContext {

    val model: Value<Model>

    data class Model(
        val lessons: List<LessonGroup> = emptyList(),
        val lessonToDelete: LessonGroup? = null
    )

    fun onLessonClick(lesson: LessonGroup)
    fun onLessonDelete(lesson: LessonGroup, confirm: Boolean)
    fun clearLessonToDelete()
}

class DefaultScriptureLessonsComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    private val onNavigateLesson: (GroupId) -> Unit
) : ScriptureLessonsComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val dataMapper: DataMapper by inject()
    private val lessonRepository: LessonRepository by inject()

    private val _model = MutableValue(ScriptureLessonsComponent.Model())
    override val model: Value<ScriptureLessonsComponent.Model> = _model

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        doOnResume {
            componentScope.launch {
                loadLessons()
            }
        }
    }

    override fun onLessonClick(lesson: LessonGroup) {
        onNavigateLesson(lesson.groupId)
    }

    override fun onLessonDelete(lesson: LessonGroup, confirm: Boolean) {
        if (confirm) {
            componentScope.launch {
                clearLessonToDelete()
                lessonRepository.delete(lesson.groupId)
                loadLessons()
            }
        } else {
            _model.update { it.copy(lessonToDelete = lesson) }
        }
    }

    override fun clearLessonToDelete() {
        _model.update { it.copy(lessonToDelete = null) }
    }

    private suspend fun loadLessons() {
        val lessons = lessonRepository.getScriptureWithData().map(dataMapper::toItem)
        val group = lessons.groupBy { it.groupId }
        _model.update { it.copy(
            lessons = group.map { (key, value) ->
                LessonGroup(groupId = key, lessons = value)
            })
        }
    }
}