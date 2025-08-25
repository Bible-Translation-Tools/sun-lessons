package org.bibletranslationtools.sun.ui.components.lessons

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.data.repositories.LessonRepository
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.DataMapper
import org.bibletranslationtools.sun.ui.model.LessonItem
import org.bibletranslationtools.sun.utils.Section
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface StartComponent : ParentContext {

    val model: Value<Model>

    data class Model(
        val lesson: LessonItem? = null,
        val sectionTitle: Int = R.string.learn_symbols,
        val imageResource: Int = R.drawable.learn,
        val onNext: () -> Unit = {}
    )

    fun onNextClicked()
}

class DefaultStartComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    private val lessonId: Long,
    private val section: Section,
    private val onFinishLesson: (Long, Section) -> Unit,
    private val onNextSection: (LessonsComponent.Intent) -> Unit
) : StartComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val dataMapper: DataMapper by inject()
    private val lessonRepository: LessonRepository by inject()
    private val sentenceRepository: SentenceRepository by inject()

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _model = MutableValue(StartComponent.Model())
    override val model: Value<StartComponent.Model> = _model

    init {
        componentScope.launch {
            val lesson = lessonRepository.get(lessonId)
            val sentenceCount = sentenceRepository.getByLessonCount(lessonId)
            _model.update { it.copy(lesson = lesson?.let(dataMapper::toItem)) }

            val shouldSkipSection = (section == Section.LEARN_SENTENCES
                    || section == Section.TEST_SENTENCES)
                    && sentenceCount == 0

            if (shouldSkipSection) {
                onFinishLesson(lessonId, Section.TEST_SENTENCES)
            } else {
                setupNextAction()
            }
        }
    }

    override fun onNextClicked() {
        _model.value.onNext()
    }

    private fun setupNextAction() {
        when (section) {
            Section.TEST_SYMBOLS -> {
                _model.update {
                    it.copy(
                        sectionTitle = R.string.test_symbols,
                        imageResource = R.drawable.test,
                        onNext = { onNextSection(LessonsComponent.Intent.TestSymbol(lessonId)) }
                    )
                }
            }
            Section.LEARN_SENTENCES -> {
                _model.update {
                    it.copy(
                        sectionTitle = R.string.learn_sentences,
                        imageResource = R.drawable.learn,
                        onNext = { onNextSection(LessonsComponent.Intent.LearnSentence(lessonId)) }
                    )
                }
            }
            Section.TEST_SENTENCES -> {
                _model.update {
                    it.copy(
                        sectionTitle = R.string.test_sentences,
                        imageResource = R.drawable.test,
                        onNext = { onNextSection(LessonsComponent.Intent.TestSentence(lessonId)) }
                    )
                }
            }
            else -> {
                _model.update {
                    it.copy(
                        sectionTitle = R.string.learn_symbols,
                        imageResource = R.drawable.learn,
                        onNext = { onNextSection(LessonsComponent.Intent.LearnSymbol(lessonId)) }
                    )
                }
            }
        }
    }
}