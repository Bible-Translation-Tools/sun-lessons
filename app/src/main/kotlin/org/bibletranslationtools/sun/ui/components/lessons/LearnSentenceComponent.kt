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
import org.bibletranslationtools.sun.data.model.SentenceWithSymbols
import org.bibletranslationtools.sun.data.model.Setting
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.LessonMode
import org.bibletranslationtools.sun.utils.Section
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.min

interface LearnSentenceComponent : ParentContext {
    val model: Value<Model>

    data class Model(
        val lessonId: Int = 1,
        val sentences: List<SentenceWithSymbols> = emptyList(),
        val mode: LessonMode = LessonMode.NORMAL,
        val lastPosition: Int = 0
    )

    fun saveLastPosition(position: Int)
    fun saveSentence(position: Int)
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

        initializeLessonMode()
        loadSentences()
    }

    override fun saveLastPosition(position: Int) {
        componentScope.launch {
            val lastSentence = Setting(Setting.LAST_SENTENCE, position.toString())
            settingsRepository.insertOrUpdate(lastSentence)
        }
    }

    override fun saveSentence(position: Int) {
        componentScope.launch {
            if (position >= 0) {
                val sentence = model.value.sentences[position]
                if (!sentence.sentence.learned) {
                    sentence.sentence.learned = true

                    sentenceRepository.update(sentence.sentence)

                    val lastSection = Setting(
                        Setting.LAST_SECTION,
                        Section.LEARN_SENTENCES.id
                    )
                    val lastLesson = Setting(
                        Setting.LAST_LESSON,
                        lessonId.toString()
                    )
                    settingsRepository.insertOrUpdate(lastSection)
                    settingsRepository.insertOrUpdate(lastLesson)
                }
            }
        }
    }

    override fun finishLesson() {
        onFinishSection(lessonId, Section.LEARN_SENTENCES, _model.value.mode)
    }

    private fun initializeLessonMode() {
        componentScope.launch {
            val all = sentenceRepository.getByLessonCount(lessonId)
            val done = sentenceRepository.getLearnedByLessonCount(lessonId)

            val mode = if (all == done) {
                LessonMode.REPEAT
            } else {
                LessonMode.NORMAL
            }

            _model.update { it.copy(mode = mode) }
        }
    }

    private fun loadSentences() {
        componentScope.launch {
            val sentences = sentenceRepository.getAllWithSymbols(lessonId)
            _model.update { it.copy(sentences = sentences) }

            if (_model.value.mode == LessonMode.NORMAL) {
                launch(Dispatchers.Main) {
                    delay(100)

                    val lastPosition = getLastPosition()
                    _model.update { it.copy(lastPosition = lastPosition) }
                }
            }
        }
    }

    private suspend fun getLastPosition(): Int {
        val pos = settingsRepository.get(Setting.LAST_SENTENCE)?.value?.toInt() ?: 0
        return min(pos, model.value.sentences.size - 1)
    }

    private fun onNavigateBack() {
        if (_model.value.mode == LessonMode.REPEAT) {
            onNavigateList(_model.value.lessonId)
        } else {
            onNavigateHome()
        }
    }
}