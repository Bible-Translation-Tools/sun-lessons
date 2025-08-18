package org.bibletranslationtools.sun.ui.components.lessons

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.data.model.SettingEntity
import org.bibletranslationtools.sun.data.repositories.CardRepository
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
import kotlin.math.min

interface LearnSymbolComponent : ParentContext {
    val model: Value<Model>

    data class Model(
        val lessonId: Long = 1,
        val cards: List<CardItem> = emptyList(),
        val mode: LessonMode = LessonMode.NORMAL,
        val lastPosition: Int = 0
    )

    suspend fun saveLastPosition(position: Int)
    fun onCardFlipped(card: CardItem)
    fun finishLesson()
}

class DefaultLearnSymbolComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    private val lessonId: Long,
    private val onFinishSection: (Long, Section) -> Unit
) : LearnSymbolComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val cardRepository: CardRepository by inject()
    private val settingsRepository: SettingsRepository by inject()

    private val _model = MutableValue(LearnSymbolComponent.Model())
    override val model: Value<LearnSymbolComponent.Model> = _model

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        componentScope.launch {
            initializeLessonMode()
            loadCards()
        }
    }

    override suspend fun saveLastPosition(position: Int) {
        if (model.value.mode == LessonMode.NORMAL) {
            val lastSymbol = SettingEntity(SettingEntity.LAST_SYMBOL, position.toString())
            settingsRepository.insertOrUpdate(lastSymbol)
        }
    }

    override fun onCardFlipped(card: CardItem) {
        componentScope.launch {
            model.value.cards.let { cards ->
                if (model.value.mode == LessonMode.REPEAT) {
                    setPassed(card)
                } else {
                    saveCard(card)
                }
            }
        }
    }

    private fun setPassed(card: CardItem) {
        _model.update { state ->
            state.copy(cards = state.cards.map {
                if (it == card) card.copy(passed = true) else it
            })
        }
    }

    private suspend fun saveCard(card: CardItem) {
        if (!card.learned) {
            cardRepository.update(card.copy(learned = true).toEntity())

            _model.update { state ->
                state.copy(cards = state.cards.map {
                    if (it == card) card.copy(learned = true) else it
                })
            }

            val lastSection = SettingEntity(
                SettingEntity.LAST_SECTION,
                Section.LEARN_SYMBOLS.id
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
            onFinishSection(lessonId, Section.LEARN_SYMBOLS)
        }
    }

    private suspend fun getLastPosition(): Int {
        val pos = settingsRepository.get(SettingEntity.LAST_SYMBOL)?.value?.toInt() ?: 0
        return min(pos, model.value.cards.size - 1)
    }

    private suspend fun loadCards() {
        val cards = cardRepository.getByLesson(lessonId).map { it.toItem() }
        _model.update { it.copy(cards = cards) }

        if (model.value.mode == LessonMode.NORMAL) {
            delay(100)

            val lastPosition = getLastPosition()
            _model.update { it.copy(lastPosition = lastPosition) }
        }
    }

    private suspend fun initializeLessonMode() {
        val all = cardRepository.getByLessonCount(lessonId)
        val done = cardRepository.getLearnedByLessonCount(lessonId)

        val mode = if (all == done) {
            LessonMode.REPEAT
        } else {
            LessonMode.NORMAL
        }

        _model.update { it.copy(mode = mode) }
    }
}