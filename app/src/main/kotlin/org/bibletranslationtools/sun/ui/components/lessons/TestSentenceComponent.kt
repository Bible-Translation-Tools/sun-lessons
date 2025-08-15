package org.bibletranslationtools.sun.ui.components.lessons

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.backhandler.BackCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.data.model.SettingEntity
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.CardItem
import org.bibletranslationtools.sun.ui.model.LessonMode
import org.bibletranslationtools.sun.ui.model.SentenceItem
import org.bibletranslationtools.sun.ui.model.SymbolItem
import org.bibletranslationtools.sun.ui.model.toEntity
import org.bibletranslationtools.sun.ui.model.toItem
import org.bibletranslationtools.sun.utils.Section
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface TestSentenceComponent : ParentContext {

    val model: Value<Model>

    data class Model(
        val lessonId: Int = 1,
        val currentSentence: SentenceItem? = null,
        val sentences: List<SentenceItem> = emptyList(),
        val cards: List<CardItem> = emptyList(),
        val imageUri: String = "",
        val correctAnswer: List<SymbolItem> = emptyList(),
        val answerSlots: List<SymbolItem> = emptyList(),
        val optionChoices: List<SymbolItem> = emptyList(),
        val answer: List<SymbolItem> = emptyList(),
        val questionDone: Boolean = false,
        val isCorrect: Boolean = false,
        val mode: LessonMode = LessonMode.NORMAL
    )

    fun setNextSentence()
    fun onSymbolSelected(symbol: SymbolItem)
}

class DefaultTestSentenceComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    private val lessonId: Int,
    private val onFinishSection: (Int, Section, LessonMode) -> Unit,
    private val onNavigateList: (Int) -> Unit,
    private val onNavigateHome: () -> Unit
) : TestSentenceComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val sentenceRepository: SentenceRepository by inject()
    private val cardsRepository: CardRepository by inject()
    private val settingsRepository: SettingsRepository by inject()

    private val _model = MutableValue(TestSentenceComponent.Model())
    override val model: Value<TestSentenceComponent.Model> = _model

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val backCallback = BackCallback(onBack = ::onNavigateBack)

    private var lastAnswerPosition = -1

    init {
        backHandler.register(backCallback)

        _model.update { it.copy(lessonId = lessonId) }

        initialize()
    }

    private fun initialize() {
        componentScope.launch {
            val sentences = sentenceRepository.getAllWithSymbols(lessonId).map {
                it.sentence.toItem().copy(symbols = it.symbols.map { symbol -> symbol.toItem() })
            }
            val cards = cardsRepository.getByLesson(lessonId).map { it.toItem() }

            _model.update { it.copy(sentences = sentences, cards = cards) }

            initializeLessonMode()
            setNextSentence()
        }
    }

    override fun setNextSentence() {
        lastAnswerPosition = -1

        val inProgressSentences = model.value.sentences.filter {
            if (model.value.mode == LessonMode.REPEAT) !it.passed else !it.tested
        }

        if (inProgressSentences.isEmpty()) {
            componentScope.launch {
                onFinishSection(lessonId, Section.TEST_SENTENCES, _model.value.mode)
            }
            return
        }

        val correctSentence = inProgressSentences.random()
        val correctSymbols = correctSentence.symbols

        val options = buildOptions(correctSymbols)
        val answerSlots = correctSymbols.map { it.copy(name = "") }

        val imageUri = "file:///android_asset/images/sentences/${correctSentence.correct}"

        _model.update {
            it.copy(
                currentSentence = correctSentence,
                answerSlots = answerSlots,
                optionChoices = options,
                imageUri = imageUri,
                correctAnswer = correctSymbols,
                isCorrect = false,
                questionDone = false
            )
        }
    }

    override fun onSymbolSelected(symbol: SymbolItem) {
        if (model.value.questionDone) return

        componentScope.launch {
            val currentAnswer = model.value.answerSlots.toMutableList()

            lastAnswerPosition++
            if (lastAnswerPosition < currentAnswer.size) {
                currentAnswer[lastAnswerPosition] = symbol.copy(selected = true)

                val newOptions = model.value.optionChoices.map {
                    if (it == symbol) it.copy(selected = true) else it
                }

                _model.update {
                    it.copy(
                        answerSlots = currentAnswer.toList(),
                        optionChoices = newOptions
                    )
                }
            }

            if (lastAnswerPosition >= currentAnswer.size - 1) {
                checkAnswer()
            }
        }
    }

    private suspend fun checkAnswer() {
        model.value.currentSentence?.let { currentSentence ->
            val submittedAnswer = model.value.answerSlots.toMutableList()
            val correctAnswer = model.value.correctAnswer.map { it.copy(correct = true) }

            val isCorrect = submittedAnswer.map { it.id } == correctAnswer.map { it.id }

            val answer = submittedAnswer.zip(correctAnswer).map { (submitted, correct) ->
                submitted.copy(correct = submitted.name == correct.name)
            }

            var updatedSentence = currentSentence

            if (isCorrect) {
                if (model.value.mode == LessonMode.REPEAT) {
                    updatedSentence = currentSentence.copy(passed = true)
                } else {
                    updatedSentence = currentSentence.copy(tested = true)
                    updateSentence(updatedSentence)
                }
            }

            _model.update { state ->
                state.copy(
                    answer = answer,
                    correctAnswer = correctAnswer,
                    isCorrect = isCorrect,
                    questionDone = true,
                    sentences = state.sentences.map {
                        if (it.id == updatedSentence.id) updatedSentence else it
                    }
                )
            }
        }
    }

    private fun buildOptions(correctSymbols: List<SymbolItem>): List<SymbolItem> {
        val totalOptions = if (correctSymbols.size > 4) 8 else 4

        val cardSymbols = model.value.cards.map {
            SymbolItem(
                0,
                it.symbol,
                0,
                null,
                selected = false,
                correct = false
            )
        }
        val sentenceSymbols = model.value.sentences.flatMap { it.symbols }
        val allPossibleSymbols = cardSymbols + sentenceSymbols

        val correctSymbolNames = correctSymbols.map { it.name }.toSet()

        val incorrectSymbols = allPossibleSymbols
            .filter { it.name !in correctSymbolNames }
            .distinctBy { it.name }
            .shuffled()
            .take(totalOptions - correctSymbols.size)

        val finalOptions = (correctSymbols + incorrectSymbols).shuffled()

        return finalOptions.map { it.copy(selected = false, correct = null) }
    }

    private suspend fun updateSentence(sentence: SentenceItem) {
        sentenceRepository.update(sentence.toEntity())

        val lastSection = SettingEntity(SettingEntity.LAST_SECTION, Section.TEST_SYMBOLS.id)
        val lastLesson = SettingEntity(SettingEntity.LAST_LESSON, lessonId.toString())
        settingsRepository.insertOrUpdate(lastSection)
        settingsRepository.insertOrUpdate(lastLesson)
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

    private fun onNavigateBack() {
        if (_model.value.mode == LessonMode.REPEAT) {
            onNavigateList(lessonId)
        } else {
            onNavigateHome()
        }
    }
}