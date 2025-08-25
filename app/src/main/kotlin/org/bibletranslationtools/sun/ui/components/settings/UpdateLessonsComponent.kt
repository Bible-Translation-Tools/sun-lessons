package org.bibletranslationtools.sun.ui.components.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.LessonItem

interface UpdateLessonsComponent : ParentContext {

    val model: Value<Model>

    data class Model(
        val lessons: List<LessonItem> = emptyList(),
        val selectedLesson: LessonItem? = null
    )

    fun onLessonSelected(lesson: LessonItem)
    fun onLessonCanceled()
    fun onDownloadLessonClick()
}

class DefaultUpdateLessonsComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext
) : UpdateLessonsComponent, AppComponent(componentContext, parentContext) {

    private val _model = MutableValue(UpdateLessonsComponent.Model())
    override val model: Value<UpdateLessonsComponent.Model> = _model

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        componentScope.launch {
            loadLessons()
        }
    }

    override fun onLessonSelected(lesson: LessonItem) {

    }

    override fun onLessonCanceled() {

    }

    override fun onDownloadLessonClick() {

    }

    private suspend fun loadLessons() {
    }
}