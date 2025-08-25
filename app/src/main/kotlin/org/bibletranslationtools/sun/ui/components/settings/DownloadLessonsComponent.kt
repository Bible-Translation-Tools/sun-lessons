package org.bibletranslationtools.sun.ui.components.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnResume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.BookItem
import org.bibletranslationtools.sun.ui.model.DownloadStatus
import org.bibletranslationtools.sun.ui.model.LessonItem
import org.bibletranslationtools.sun.ui.model.emptyBookItem
import org.bibletranslationtools.sun.usecase.CalculateDownloadStatus
import org.bibletranslationtools.sun.usecase.DownloadLesson
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface DownloadLessonsComponent : ParentContext {

    val model: Value<Model>

    data class Model(
        val bookItem: BookItem = emptyBookItem(),
        val chapter: Int = 1,
        val lessons: List<LessonItem> = emptyList(),
        val selectedLesson: LessonItem? = null
    )

    fun onLessonSelected(lesson: LessonItem)
    fun dismissSelectedLesson()
    fun onDownloadLessonClick()
}

class DefaultDownloadLessonsComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    private val bookItem: BookItem,
    private val chapter: Int
) : DownloadLessonsComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val calculateDownloadStatus: CalculateDownloadStatus by inject()
    private val downloadLesson: DownloadLesson by inject()

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

        doOnResume {
            componentScope.launch {
                loadLessons()
            }
        }
    }

    override fun onLessonSelected(lesson: LessonItem) {
        _model.update { it.copy(selectedLesson = lesson) }
    }

    override fun dismissSelectedLesson() {
        _model.update { it.copy(selectedLesson = null) }
    }

    override fun onDownloadLessonClick() {
        componentScope.launch {
            model.value.selectedLesson?.let { lesson ->
                dismissSelectedLesson()

                downloadLesson(lesson.groupId) { progress ->
                    onDownloadProgress(lesson, progress)
                }

                _model.update { state ->
                    state.copy(lessons = state.lessons.map {
                        if (it.fingerprint == lesson.fingerprint) {
                            it.copy(
                                downloadStatus = DownloadStatus.DONE,
                                downloadProgress = -1f
                            )
                        } else {
                            it
                        }
                    })
                }
            }
        }
    }

    private suspend fun loadLessons() {
        val lessons = calculateDownloadStatus(bookItem.slug, chapter)
            .sortedBy { it.name }
        _model.update { it.copy(lessons = lessons) }
    }

    private fun onDownloadProgress(group: LessonItem, newProgress: Float) {
        _model.update { state ->
            state.copy(
                lessons = state.lessons.map { lesson ->
                    if (lesson.groupId == group.groupId) {
                        lesson.copy(downloadProgress = newProgress)
                    } else {
                        lesson
                    }
                }
            )
        }
    }
}