package org.bibletranslationtools.sun.ui.components.lessons

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.data.entity.SettingEntity
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.DataMapper
import org.bibletranslationtools.sun.ui.model.LessonItem
import org.bibletranslationtools.sun.ui.model.LessonMode
import org.bibletranslationtools.sun.ui.model.SentenceItem
import org.bibletranslationtools.sun.utils.Section
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.min

interface LearnSentenceComponent : ParentContext {
    val model: Value<Model>

    data class Model(
        val lesson: LessonItem? = null,
        val sentences: List<SentenceItem> = emptyList(),
        val mode: LessonMode = LessonMode.NORMAL,
        val lastPosition: Int = 0
    )

    suspend fun saveLastPosition(position: Int)
    fun onCardFlipped(sentence: SentenceItem)
    fun finishLesson()
}

class DefaultLearnSentenceComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    private val lessonId: Long,
    private val onFinishSection: (Long, Section) -> Unit
) : LearnSentenceComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val dataMapper: DataMapper by inject()
    private val lessonRepository: LessonRepository by inject()
    private val sentenceRepository: SentenceRepository by inject()
    private val settingsRepository: SettingsRepository by inject()

    private val _model = MutableValue(LearnSentenceComponent.Model())
    override val model: Value<LearnSentenceComponent.Model> = _model

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        componentScope.launch {
            initialize()
        }
    }

    private suspend fun initialize() {
        val lesson = lessonRepository.get(lessonId)?.let(dataMapper::toItem)

        val allSentencesCount = sentenceRepository.getByLessonCount(lessonId)
        val learnedSentencesCount = sentenceRepository.getLearnedByLessonCount(lessonId)
        val mode = if (allSentencesCount > 0 && allSentencesCount == learnedSentencesCount) {
            LessonMode.REPEAT
        } else {
            LessonMode.NORMAL
        }

        val sentences = sentenceRepository.getAllWithSymbols(lessonId).map {
            it.sentence.let(dataMapper::toItem).copy(
                symbols = it.symbols.map(dataMapper::toItem)
            )
        }

        val lastPosSetting = settingsRepository.get(
            SettingEntity.lastSentence(lesson?.groupId?.id ?: "0")
        )?.value?.toInt() ?: 0

        val lastPosition = if (sentences.isNotEmpty()) {
            min(lastPosSetting, sentences.size - 1)
        } else 0

        _model.update {
            it.copy(
                lesson = lesson,
                sentences = sentences,
                mode = mode,
                lastPosition = lastPosition
            )
        }
    }

    override suspend fun saveLastPosition(position: Int) {
        if (model.value.mode == LessonMode.NORMAL) {
            val lesson = model.value.lesson ?: return
            val lastSentence = SettingEntity(
                SettingEntity.lastSentence(lesson.groupId.id),
                position.toString()
            )
            settingsRepository.insertOrUpdate(lastSentence)
        }
    }

    override fun onCardFlipped(sentence: SentenceItem) {
        if (model.value.mode == LessonMode.REPEAT) {
            setPassed(sentence)
        } else {
            componentScope.launch {
                saveSentence(sentence)
            }
        }
    }

    private fun setPassed(sentence: SentenceItem) {
        _model.update { state ->
            state.copy(
                sentences = state.sentences.map {
                    if (it.id == sentence.id) it.copy(passed = true) else it
                }
            )
        }
    }

    private suspend fun saveSentence(sentence: SentenceItem) {
        if (sentence.learned) return

        val lesson = model.value.lesson ?: return
        val updatedSentenceEntity = sentence.copy(learned = true).let(dataMapper::toEntity)

        sentenceRepository.update(updatedSentenceEntity)

        val lastSection = SettingEntity(SettingEntity.lastSection(lesson.groupId.id), Section.LEARN_SENTENCES.id)
        val lastLesson = SettingEntity(SettingEntity.lastLesson(lesson.groupId.id), lessonId.toString())

        settingsRepository.insertOrUpdate(lastSection)
        settingsRepository.insertOrUpdate(lastLesson)

        _model.update { state ->
            state.copy(sentences = state.sentences.map {
                if (it.id == sentence.id) it.copy(learned = true) else it
            })
        }
    }

    override fun finishLesson() {
        componentScope.launch {
            saveLastPosition(0)
            onFinishSection(lessonId, Section.LEARN_SENTENCES)
        }
    }
}