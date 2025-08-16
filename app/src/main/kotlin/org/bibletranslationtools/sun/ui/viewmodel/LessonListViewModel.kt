package org.bibletranslationtools.sun.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.data.AppDatabase
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.LessonRepositoryImpl
import org.bibletranslationtools.sun.ui.model.LessonItem

class LessonListViewModel(application: Application) : AndroidViewModel(application) {
    private val lessonRepository: LessonRepository

    private val activeLessonId = MutableStateFlow(1)

    val lessons: StateFlow<List<LessonItem>> get() = mutableLessons
    private val mutableLessons = MutableStateFlow<List<LessonItem>>(listOf())

    init {
        val lessonDao = AppDatabase.getDatabase(application).getLessonDao()
        lessonRepository = LessonRepositoryImpl(lessonDao)
    }

    fun loadLessons(): Job {
        return viewModelScope.launch {
//            val lessons = lessonRepository.getAllWithData().map(LessonMapper::map)
//            lessons.forEachIndexed { index, lesson ->
//                lesson.isAvailable = lessonAvailable(lessons, index)
//                if (lesson.lesson.id == activeLessonId.value) {
//                    lesson.isSelected = true
//                }
//            }
//            mutableLessons.value = lessons
        }
    }

    fun setActiveLesson(lessonId: Int) {
        activeLessonId.value = lessonId
    }

    private fun lessonAvailable(lessons: List<LessonItem>, position: Int): Boolean {
        if (position == 0) return true
        val prevLesson = lessons[position - 1]
        return prevLesson.totalProgress == 100.0
    }
}