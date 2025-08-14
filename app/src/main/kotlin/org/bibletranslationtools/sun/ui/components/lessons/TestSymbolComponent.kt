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
import org.bibletranslationtools.sun.data.model.Answer
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.data.model.Setting
import org.bibletranslationtools.sun.data.model.TestCard
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.LessonMode
import org.bibletranslationtools.sun.utils.Section
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface TestSymbolComponent : ParentContext {
    val model: Value<Model>

    data class Model(
        val lessonId: Int = 1,
        val correctCard: Card? = null,
        val cards: List<Card> = emptyList(),
        val answerChoices: List<TestCard> = emptyList(),
        val questionDone: Boolean = false,
        val mode: LessonMode = LessonMode.NORMAL
    )

    fun setNextQuestion()
    fun checkAnswer(card: Card)
}

class DefaultTestSymbolComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    private val lessonId: Int,
    private val onFinishSection: (Int, Section, LessonMode) -> Unit,
    private val onNavigateList: (Int) -> Unit,
    private val onNavigateHome: () -> Unit
) : TestSymbolComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val cardRepository: CardRepository by inject()
    private val sentenceRepository: SentenceRepository by inject()
    private val settingsRepository: SettingsRepository by inject()

    private val _model = MutableValue(TestSymbolComponent.Model())
    override val model: Value<TestSymbolComponent.Model> = _model

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val backCallback = BackCallback(onBack = ::onNavigateBack)

    init {
        backHandler.register(backCallback)

        _model.update { it.copy(lessonId = lessonId) }

        componentScope.launch {
            initializeLessonMode()
            loadLessonCards()
            setNextQuestion()
        }
    }

    override fun setNextQuestion() {
        val inProgressCards = model.value.cards.filter {
            if (model.value.mode == LessonMode.REPEAT) !it.passed else !it.tested
        }

        if (inProgressCards.isEmpty()) {
            componentScope.launch {
                val section = if (getSentencesCount() == 0) {
                    Section.TEST_SENTENCES
                } else Section.TEST_SYMBOLS
                onFinishSection(lessonId, section, _model.value.mode)
            }
            return
        }

        val correctCard = inProgressCards.random()
        val incorrectCards = model.value.cards
            .filter { it.id != correctCard.id }
            .shuffled()
            .take(3)

        val finalChoices = (listOf(correctCard) + incorrectCards)
            .shuffled()
            .onEach { it.correct = null }

        _model.update {
            it.copy(
                correctCard = correctCard,
                answerChoices = finalChoices,
                questionDone = false
            )
        }
    }

    override fun checkAnswer(card: Card) {
        componentScope.launch {
            if (model.value.questionDone) return@launch

            model.value.correctCard?.let { correctCard ->
                val isCorrect = card.id == correctCard.id

                if (isCorrect) {
                    if (model.value.mode == LessonMode.REPEAT) {
                        correctCard.passed = true
                    } else {
                        correctCard.tested = true
                        updateCard(correctCard)
                    }
                }

                val choices = if (isCorrect) {
                    listOf(
                        Answer(correct = true),
                        correctCard.also { it.correct = true }
                    )
                } else {
                    listOf(
                        Answer(correct = false),
                        card.also { it.correct = false },
                        Answer(correct = true),
                        correctCard.also { it.correct = true }
                    )
                }

                _model.update {
                    it.copy(
                        answerChoices = choices,
                        questionDone = true
                    )
                }
            }
        }
    }

    private suspend fun getSentencesCount(): Int {
        return sentenceRepository.getByLessonCount(lessonId)
    }

    private suspend fun updateCard(card: Card) {
        cardRepository.update(card)

        val lastSection = Setting(Setting.LAST_SECTION, Section.TEST_SYMBOLS.id)
        val lastLesson = Setting(Setting.LAST_LESSON, lessonId.toString())
        settingsRepository.insertOrUpdate(lastSection)
        settingsRepository.insertOrUpdate(lastLesson)
    }

    private suspend fun initializeLessonMode() {
        val all = cardRepository.getByLessonCount(lessonId)
        val done = cardRepository.getTestedByLessonCount(lessonId)

        val mode = if (all == done) LessonMode.REPEAT else LessonMode.NORMAL
        _model.update { it.copy(mode = mode) }
    }

    private suspend fun loadLessonCards() {
        val cards = cardRepository.getByLesson(lessonId)
        _model.update { it.copy(cards = cards) }
    }

    private fun onNavigateBack() {
        if (_model.value.mode == LessonMode.REPEAT) {
            onNavigateList(lessonId)
        } else {
            onNavigateHome()
        }
    }
}