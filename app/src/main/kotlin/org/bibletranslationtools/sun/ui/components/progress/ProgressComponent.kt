package org.bibletranslationtools.sun.ui.components.progress

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.components.lessons.LessonType
import org.bibletranslationtools.sun.ui.model.LessonSuite
import org.bibletranslationtools.sun.ui.model.toItem
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ProgressComponent : ParentContext {

    val model: Value<Model>

    data class Model(
        val lessons: List<LessonSuite> = emptyList()
    )
}

class DefaultProgressComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext
) : ProgressComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val lessonRepository: LessonRepository by inject()

    private val _model = MutableValue(ProgressComponent.Model())
    override val model: Value<ProgressComponent.Model> = _model

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        componentScope.launch {
            loadLessons()
        }
    }

    private  suspend fun loadLessons() {
        val lessons = lessonRepository.getAllWithData(LessonType.BASIC).map { it.toItem() }
        _model.update {
            it.copy(lessons = lessons.mapIndexed { index, lesson ->
                lesson.copy(isAvailable = lessonAvailable(lessons, index))
            })
        }
    }

    private fun lessonAvailable(lessons: List<LessonSuite>, position: Int): Boolean {
        if (position == 0) return true
        val prevLesson = lessons[position - 1]
        return prevLesson.totalProgress == 100.0
    }
}