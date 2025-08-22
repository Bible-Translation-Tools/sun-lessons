package org.bibletranslationtools.sun.ui.components.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.LessonItem
import org.bibletranslationtools.sun.utils.Utils

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
        withContext(Dispatchers.Default) {
            val lessons = listOf(
                LessonItem(
                    "gen",
                    4,
                    21,
                    1,
                    "username1",
                    Utils.getCurrentTime(),
                    Utils.getCurrentTime()
                ),
                LessonItem(
                    "psa",
                    144,
                    37,
                    1,
                    "username2",
                    Utils.getCurrentTime(),
                    Utils.getCurrentTime()
                ),
                LessonItem(
                    "mat",
                    1,
                    1,
                    1,
                    "username1",
                    Utils.getCurrentTime(),
                    Utils.getCurrentTime()
                ),
                LessonItem(
                    "jhn",
                    3,
                    16,
                    1,
                    "username1",
                    Utils.getCurrentTime(),
                    Utils.getCurrentTime()
                ),
                LessonItem(
                    "act",
                    14,
                    2,
                    1,
                    "max",
                    Utils.getCurrentTime(),
                    Utils.getCurrentTime()
                ),
                LessonItem(
                    "rev",
                    22,
                    12,
                    1,
                    "username2",
                    Utils.getCurrentTime(),
                    Utils.getCurrentTime()
                ),
            )
            _model.update { it.copy(lessons = lessons) }
        }
    }
}