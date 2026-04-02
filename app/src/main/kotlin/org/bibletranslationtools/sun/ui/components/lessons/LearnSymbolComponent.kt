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
import org.bibletranslationtools.sun.data.repositories.CardRepository
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.CardItem
import org.bibletranslationtools.sun.ui.model.DataMapper
import org.bibletranslationtools.sun.ui.model.LessonItem
import org.bibletranslationtools.sun.ui.model.LessonMode
import org.bibletranslationtools.sun.utils.Section
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.min

interface LearnSymbolComponent : ParentContext {
    val model: Value<Model>

    data class Model(
        val lesson: LessonItem? = null,
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

    private val dataMapper: DataMapper by inject()
    private val lessonRepository: LessonRepository by inject()
    private val cardRepository: CardRepository by inject()
    private val settingsRepository: SettingsRepository by inject()

    private val _model = MutableValue(LearnSymbolComponent.Model())
    override val model: Value<LearnSymbolComponent.Model> = _model

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        componentScope.launch {
            initialize()
        }
    }

    private suspend fun initialize() {
        val lessonEntity = lessonRepository.get(lessonId)
        val lesson = lessonEntity?.let(dataMapper::toItem)

        val allCardsCount = cardRepository.getByLessonCount(lessonId)
        val learnedCardsCount = cardRepository.getLearnedByLessonCount(lessonId)
        val mode = if (allCardsCount == learnedCardsCount && allCardsCount > 0) {
            LessonMode.REPEAT
        } else {
            LessonMode.NORMAL
        }

        val cards = cardRepository.getByLesson(lessonId).map(dataMapper::toItem)
        val lastPosSetting = settingsRepository.get(
            SettingEntity.lastSymbol(lesson?.groupId?.id ?: "0")
        )?.value?.toInt() ?: 0

        val lastPosition = if (cards.isNotEmpty()) {
            min(lastPosSetting, cards.size - 1)
        } else 0

        _model.update {
            it.copy(
                lesson = lesson,
                cards = cards,
                mode = mode,
                lastPosition = lastPosition
            )
        }
    }

    override suspend fun saveLastPosition(position: Int) {
        if (model.value.mode == LessonMode.NORMAL) {
            val lesson = model.value.lesson ?: return
            val lastSymbol = SettingEntity(
                SettingEntity.lastSymbol(lesson.groupId.id),
                position.toString()
            )
            settingsRepository.insertOrUpdate(lastSymbol)
        }
    }

    override fun onCardFlipped(card: CardItem) {
        if (model.value.mode == LessonMode.REPEAT) {
            setPassed(card)
        } else {
            componentScope.launch {
                saveCard(card)
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
        if (card.learned) return

        val lesson = model.value.lesson ?: return
        val updatedCardEntity = card.copy(learned = true).let(dataMapper::toEntity)

        cardRepository.update(updatedCardEntity)

        val lastSection = SettingEntity(
            SettingEntity.lastSection(lesson.groupId.id),
            Section.LEARN_SYMBOLS.id
        )
        val lastLesson = SettingEntity(
            SettingEntity.lastLesson(lesson.groupId.id),
            lessonId.toString()
        )

        settingsRepository.insertOrUpdate(lastSection)
        settingsRepository.insertOrUpdate(lastLesson)

        _model.update { state ->
            state.copy(cards = state.cards.map {
                if (it.id == card.id) it.copy(learned = true) else it
            })
        }
    }

    override fun finishLesson() {
        componentScope.launch {
            saveLastPosition(0)
            onFinishSection(lessonId, Section.LEARN_SYMBOLS)
        }
    }
}