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
import org.bibletranslationtools.sun.ui.model.BookItem
import org.bibletranslationtools.sun.ui.model.LessonItem
import org.bibletranslationtools.sun.ui.model.emptyBookItem
import org.bibletranslationtools.sun.utils.Utils
import org.koin.core.component.KoinComponent

interface DownloadLessonsComponent : ParentContext {

    val model: Value<Model>

    data class Model(
        val bookItem: BookItem = emptyBookItem(),
        val chapter: Int = 1,
        val lessons: List<LessonItem> = emptyList(),
        val selectedLesson: LessonItem? = null
    )

    fun onLessonSelected(lesson: LessonItem)
    fun onLessonCanceled()
    fun onDownloadLessonClick()
}

class DefaultDownloadLessonsComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    private val bookItem: BookItem,
    private val chapter: Int
) : DownloadLessonsComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val _model = MutableValue(DownloadLessonsComponent.Model())
    override val model: Value<DownloadLessonsComponent.Model> = _model

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        _model.update {
            it.copy(
                bookItem = bookItem,
                chapter = chapter
            )
        }

        componentScope.launch {
            loadLessons()
        }
    }

    override fun onLessonSelected(lesson: LessonItem) {
        _model.update { it.copy(selectedLesson = lesson) }
    }

    override fun onLessonCanceled() {
        _model.update { it.copy(selectedLesson = null) }
    }

    override fun onDownloadLessonClick() {
        componentScope.launch {
            model.value.selectedLesson?.let { lesson ->
                onLessonCanceled()
                println("downloading lessons ${lesson.name} started...")
            }
        }
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
                    Utils.getCurrentTime(),
                    0
                ),
                LessonItem(
                    "mat",
                    1,
                    1,
                    1,
                    "username1",
                    Utils.getCurrentTime(),
                    Utils.getCurrentTime(),
                    0
                ),
                LessonItem(
                    "jhn",
                    3,
                    16,
                    1,
                    "username1",
                    Utils.getCurrentTime(),
                    Utils.getCurrentTime(),
                    0
                ),
                LessonItem(
                    "act",
                    14,
                    2,
                    1,
                    "max",
                    Utils.getCurrentTime(),
                    Utils.getCurrentTime(),
                    0
                ),
                LessonItem(
                    "rev",
                    22,
                    12,
                    1,
                    "username2",
                    Utils.getCurrentTime(),
                    Utils.getCurrentTime(),
                    0
                ),
            )
            _model.update { it.copy(lessons = lessons) }
        }
    }
}