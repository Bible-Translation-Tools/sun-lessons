package org.bibletranslationtools.sun.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bibletranslationtools.sun.data.AppDatabase
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.model.CardEntity
import org.bibletranslationtools.sun.data.model.SettingEntity
import org.bibletranslationtools.sun.data.repositories.CardRepositoryImpl
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.LessonRepositoryImpl
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.data.repositories.SentenceRepositoryImpl
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepositoryImpl
import org.bibletranslationtools.sun.ui.model.LessonMode
import org.bibletranslationtools.sun.utils.Section

class TestSymbolsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CardRepository
    private val sentenceRepository: SentenceRepository
    private val lessonRepository: LessonRepository
    private val settingsRepository: SettingsRepository

    private val _cards = MutableStateFlow<List<CardEntity>>(listOf())
    val cards: StateFlow<List<CardEntity>> = _cards

    val questionDone = MutableStateFlow(false)
    val lessonId = MutableStateFlow(1)
    val mode = MutableStateFlow(LessonMode.NORMAL)

    init {
        val cardDao = AppDatabase.getDatabase(application).getCardDao()
        repository = CardRepositoryImpl(cardDao)
        val sentenceDao = AppDatabase.getDatabase(application).getSentenceDao()
        val symbolDao = AppDatabase.getDatabase(application).getSymbolDao()
        sentenceRepository = SentenceRepositoryImpl(sentenceDao, symbolDao)
        val lessonDao = AppDatabase.getDatabase(application).getLessonDao()
        lessonRepository = LessonRepositoryImpl(lessonDao)
        val settingDao = AppDatabase.getDatabase(application).getSettingDao()
        settingsRepository = SettingsRepositoryImpl(settingDao)
    }

    fun loadLessonCards() {
        viewModelScope.launch {
            _cards.value = repository.getByLesson(lessonId.value)
        }
    }

    suspend fun getSentencesCount(): Int {
        return sentenceRepository.getByLessonCount(lessonId.value)
    }

    suspend fun updateCard(card: CardEntity) {
        repository.update(card)

        val lastSection = SettingEntity(SettingEntity.LAST_SECTION, Section.TEST_SYMBOLS.id)
        val lastLesson = SettingEntity(SettingEntity.LAST_LESSON, lessonId.value.toString())
        settingsRepository.insertOrUpdate(lastSection)
        settingsRepository.insertOrUpdate(lastLesson)
    }

    fun initializeLessonMode() {
        runBlocking {
            val all = repository.getByLessonCount(lessonId.value)
            val done = repository.getTestedByLessonCount(lessonId.value)

            if (all == done) {
                mode.value = LessonMode.REPEAT
            } else {
                mode.value = LessonMode.NORMAL
            }
        }
    }
}