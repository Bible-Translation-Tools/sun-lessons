package org.bibletranslationtools.sun.ui.components.lessons

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.backhandler.BackCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.data.model.SettingEntity
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.LessonMode
import org.bibletranslationtools.sun.ui.model.SentenceItem
import org.bibletranslationtools.sun.ui.model.toEntity
import org.bibletranslationtools.sun.ui.model.toItem
import org.bibletranslationtools.sun.utils.Section
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.min

interface LearnSentenceComponent : ParentContext {
    val model: Value<Model>

    data class Model(
        val lessonId: Int = 1,
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
    private val lessonId: Int,
    private val onFinishSection: (Int, Section, LessonMode) -> Unit,
    private val onNavigateList: (Int) -> Unit,
    private val onNavigateHome: () -> Unit
) : LearnSentenceComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val sentenceRepository: SentenceRepository by inject()
    private val settingsRepository: SettingsRepository by inject()

    private val _model = MutableValue(LearnSentenceComponent.Model())
    override val model: Value<LearnSentenceComponent.Model> = _model

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val backCallback = BackCallback(onBack = ::onNavigateBack)

    init {
        backHandler.register(backCallback)

        componentScope.launch {
            initializeLessonMode()
            loadSentences()
        }
    }

    override suspend fun saveLastPosition(position: Int) {
        if (model.value.mode == LessonMode.NORMAL) {
            val lastSentence = SettingEntity(SettingEntity.LAST_SENTENCE, position.toString())
            settingsRepository.insertOrUpdate(lastSentence)
        }
    }

    override fun onCardFlipped(sentence: SentenceItem) {
        componentScope.launch {
            if (model.value.mode == LessonMode.REPEAT) {
                setPassed(sentence)
            } else {
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
        if (!sentence.learned) {
            sentenceRepository.update(sentence.copy(learned = true).toEntity())

            _model.update { state ->
                state.copy(
                    sentences = state.sentences.map {
                        if (it.id == sentence.id) it.copy(learned = true) else it
                    }
                )
            }

            val lastSection = SettingEntity(
                SettingEntity.LAST_SECTION,
                Section.LEARN_SENTENCES.id
            )
            val lastLesson = SettingEntity(
                SettingEntity.LAST_LESSON,
                lessonId.toString()
            )
            settingsRepository.insertOrUpdate(lastSection)
            settingsRepository.insertOrUpdate(lastLesson)
        }
    }

    override fun finishLesson() {
        componentScope.launch {
            saveLastPosition(0)
            onFinishSection(lessonId, Section.LEARN_SENTENCES, model.value.mode)
        }
    }

    private suspend fun loadSentences() {
        val sentencesWithSymbols = sentenceRepository.getAllWithSymbols(lessonId)
        val sentences = sentencesWithSymbols.map {
            it.sentence.toItem().copy(symbols = it.symbols.map { symbol -> symbol.toItem() })
        }

        _model.update { it.copy(sentences = sentences) }

        if (model.value.mode == LessonMode.NORMAL) {
            delay(100)

            val lastPosition = getLastPosition()
            _model.update { it.copy(lastPosition = lastPosition) }
        }
    }

    private suspend fun initializeLessonMode() {
        val all = sentenceRepository.getByLessonCount(lessonId)
        val done = sentenceRepository.getLearnedByLessonCount(lessonId)

        val mode = if (all == done) {
            LessonMode.REPEAT
        } else {
            LessonMode.NORMAL
        }

        _model.update { it.copy(mode = mode) }
    }

    private suspend fun getLastPosition(): Int {
        val pos = settingsRepository.get(SettingEntity.LAST_SENTENCE)?.value?.toInt() ?: 0
        return min(pos, model.value.sentences.size - 1)
    }

    private fun onNavigateBack() {
        if (model.value.mode == LessonMode.REPEAT) {
            onNavigateList(model.value.lessonId)
        } else {
            onNavigateHome()
        }
    }
}