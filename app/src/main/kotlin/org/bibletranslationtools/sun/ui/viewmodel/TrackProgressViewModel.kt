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
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepositoryImpl
import org.bibletranslationtools.sun.ui.model.LessonItem
import org.bibletranslationtools.sun.ui.model.toItem

class TrackProgressViewModel(application: Application) : AndroidViewModel(application) {
    private val lessonRepository: LessonRepository
    private val settingsRepository: SettingsRepository

    val lessons: StateFlow<List<LessonItem>> get() = mutableLessons
    private val mutableLessons = MutableStateFlow<List<LessonItem>>(listOf())

    init {
        val lessonDao = AppDatabase.getDatabase(application).getLessonDao()
        lessonRepository = LessonRepositoryImpl(lessonDao)
        val settingsDao = AppDatabase.getDatabase(application).getSettingDao()
        settingsRepository = SettingsRepositoryImpl(settingsDao)
    }

    fun loadLessons(): Job {
        return viewModelScope.launch {
            val lessons = lessonRepository.getAllWithData().map { it.toItem() }
//            lessons.forEachIndexed { index, lesson ->
//                lesson.isAvailable = lessonAvailable(lessons, index)
//            }
            mutableLessons.value = lessons
        }
    }

    suspend fun getLastLesson(): Int {
        return settingsRepository.get("last_lesson")?.value?.toInt() ?: 1
    }

    private fun lessonAvailable(lessons: List<LessonItem>, position: Int): Boolean {
        if (position == 0) return true
        val prevLesson = lessons[position - 1]
        return prevLesson.totalProgress == 100.0
    }

}