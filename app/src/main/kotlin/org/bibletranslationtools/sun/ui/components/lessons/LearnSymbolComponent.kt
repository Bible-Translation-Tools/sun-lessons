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
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.data.model.Setting
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.LessonMode
import org.bibletranslationtools.sun.utils.Section
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.min

interface LearnSymbolComponent : ParentContext {
    val model: Value<Model>

    data class Model(
        val lessonId: Int = 1,
        val cards: List<Card> = emptyList(),
        val mode: LessonMode = LessonMode.NORMAL,
        val lastPosition: Int = 0
    )

    fun saveLastPosition(position: Int)
    fun saveCard(position: Int)
    fun finishLesson()
}

class DefaultLearnSymbolComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    private val lessonId: Int,
    private val onFinishSection: (Int, Section, LessonMode) -> Unit,
    private val onNavigateList: (Int) -> Unit,
    private val onNavigateHome: () -> Unit
) : LearnSymbolComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val cardRepository: CardRepository by inject()
    private val settingsRepository: SettingsRepository by inject()

    private val _model = MutableValue(LearnSymbolComponent.Model())
    override val model: Value<LearnSymbolComponent.Model> = _model

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val backCallback = BackCallback(onBack = ::onNavigateBack)

    init {
        backHandler.register(backCallback)

        initializeLessonMode()
        loadCards()
    }

    override fun saveLastPosition(position: Int) {
        componentScope.launch {
            val lastSymbol = Setting(Setting.LAST_SYMBOL, position.toString())
            settingsRepository.insertOrUpdate(lastSymbol)
        }
    }

    override fun saveCard(position: Int) {
        componentScope.launch {
            if (position >= 0) {
                val card = _model.value.cards[position]
                if (!card.learned) {
                    card.learned = true

                    cardRepository.update(card)

                    val lastSection = Setting(
                        Setting.LAST_SECTION,
                        Section.LEARN_SYMBOLS.id
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
        onFinishSection(lessonId, Section.LEARN_SYMBOLS, _model.value.mode)
    }

    private suspend fun getLastPosition(): Int {
        val pos = settingsRepository.get(Setting.LAST_SYMBOL)?.value?.toInt() ?: 0
        return min(pos, _model.value.cards.size - 1)
    }

    private fun loadCards() {
        componentScope.launch {
            val cards = cardRepository.getByLesson(lessonId)
            _model.update { it.copy(cards = cards) }

            if (_model.value.mode == LessonMode.NORMAL) {
                launch(Dispatchers.Main) {
                    delay(100)

                    val lastPosition = getLastPosition()
                    _model.update { it.copy(lastPosition = lastPosition) }
                }
            }
        }
    }

    private fun initializeLessonMode() {
        componentScope.launch {
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

    private fun onNavigateBack() {
        if (_model.value.mode == LessonMode.REPEAT) {
            onNavigateList(_model.value.lessonId)
        } else {
            onNavigateHome()
        }
    }
}