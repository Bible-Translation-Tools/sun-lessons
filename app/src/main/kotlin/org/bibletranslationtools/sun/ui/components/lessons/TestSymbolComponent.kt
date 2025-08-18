package org.bibletranslationtools.sun.ui.components.lessons

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
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
import org.bibletranslationtools.sun.ui.model.toEntity
import org.bibletranslationtools.sun.ui.model.toItem
import org.bibletranslationtools.sun.utils.Section
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface TestSymbolComponent : ParentContext {
    val model: Value<Model>

    data class Model(
        val lessonId: Int = 1,
        val currentCard: CardItem? = null,
        val cards: List<CardItem> = emptyList(),
        val choices: List<CardItem> = emptyList(),
        val answer: List<CardItem> = emptyList(),
        val questionDone: Boolean = false,
        val mode: LessonMode = LessonMode.NORMAL
    )

    fun setNextQuestion()
    fun checkAnswer(card: CardItem)
}

class DefaultTestSymbolComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    private val lessonId: Int,
    private val onFinishSection: (Int, Section) -> Unit
) : TestSymbolComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val cardRepository: CardRepository by inject()
    private val sentenceRepository: SentenceRepository by inject()
    private val settingsRepository: SettingsRepository by inject()

    private val _model = MutableValue(TestSymbolComponent.Model())
    override val model: Value<TestSymbolComponent.Model> = _model

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
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
                onFinishSection(lessonId, section)
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
            .onEach { it.copy(correct = null) }

        _model.update {
            it.copy(
                currentCard = correctCard,
                choices = finalChoices,
                questionDone = false,
                answer = emptyList()
            )
        }
    }

    override fun checkAnswer(card: CardItem) {
        componentScope.launch {
            if (model.value.questionDone) return@launch

            model.value.currentCard?.let { currentCard ->
                var updatedCard = currentCard
                val isCorrect = card.id == currentCard.id

                if (isCorrect) {
                    if (model.value.mode == LessonMode.REPEAT) {
                        updatedCard = currentCard.copy(passed = true)
                    } else {
                        updatedCard = currentCard.copy(tested = true)
                        updateCard(updatedCard)
                    }
                }

                val answer = if (isCorrect) {
                    listOf(currentCard.copy(correct = true))
                } else {
                    listOf(
                        card.copy(correct = false),
                        currentCard.copy(correct = true)
                    )
                }

                _model.update { state ->
                    state.copy(
                        answer = answer,
                        questionDone = true,
                        cards = state.cards.map {
                            if (it.id == updatedCard.id) updatedCard else it
                        }
                    )
                }
            }
        }
    }

    private suspend fun getSentencesCount(): Int {
        return sentenceRepository.getByLessonCount(lessonId)
    }

    private suspend fun updateCard(card: CardItem) {
        cardRepository.update(card.toEntity())

        val lastSection = SettingEntity(SettingEntity.LAST_SECTION, Section.TEST_SYMBOLS.id)
        val lastLesson = SettingEntity(SettingEntity.LAST_LESSON, lessonId.toString())
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
        val cards = cardRepository.getByLesson(lessonId).map { it.toItem() }
        _model.update { it.copy(cards = cards) }
    }
}