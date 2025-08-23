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
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.LessonItem
import org.bibletranslationtools.sun.ui.model.toItem
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ScriptureLessonsComponent : ParentContext {

    val model: Value<Model>

    data class Model(
        val lessons: List<LessonItem> = emptyList()
    )

    fun onLessonClick(lesson: LessonItem)
    fun onLessonDelete(lesson: LessonItem)
}

class DefaultScriptureLessonsComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext
) : ScriptureLessonsComponent, KoinComponent, AppComponent(componentContext, parentContext) {

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

    override fun onLessonClick(lesson: LessonItem) {
        println("open lesson: ${lesson.groupId}")
    }

    override fun onLessonDelete(lesson: LessonItem) {
        println("delete lesson: ${lesson.name}")
    }

    private suspend fun loadLessons() {
        val lessons = withContext(Dispatchers.Default) {
            lessonRepository.getScriptureWithData().map { it.toItem() }
        }
        _model.update { it.copy(lessons = lessons) }
    }
}