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
import org.bibletranslationtools.sun.data.repositories.SentenceRepository
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.LessonsIntent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.utils.Section
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface StartComponent : ParentContext {

    val model: Value<Model>

    data class Model(
        val lessonId: Int = 0,
        val sectionTitle: Int = 0,
        val imageResource: Int = 0,
        val onNext: () -> Unit = {}
    )

    fun onNextClicked()
}

class DefaultStartComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    private val lessonId: Int,
    private val section: Section,
    private val onFinishLesson: (Int, Section) -> Unit,
    private val onNextSection: (LessonsIntent) -> Unit
) : StartComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val sentenceRepository: SentenceRepository by inject()

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _model = MutableValue(StartComponent.Model())
    override val model: Value<StartComponent.Model> = _model

    init {
        _model.update { it.copy(lessonId = lessonId) }

        componentScope.launch {
            if ((section == Section.LEARN_SENTENCES || section == Section.TEST_SENTENCES) &&
                sentencesByLessonCount(lessonId) == 0
            ) {
                finishLesson()
                return@launch
            }
        }

        setupNextAction()
    }

    override fun onNextClicked() {
        _model.value.onNext()
    }

    suspend fun sentencesByLessonCount(lessonId: Int): Int {
        return sentenceRepository.getByLessonCount(lessonId)
    }

    private suspend fun finishLesson() {
        if (sentencesByLessonCount(lessonId) == 0) {
            onFinishLesson(lessonId, Section.TEST_SENTENCES)
        }
    }

    private fun setupNextAction() {
        when (section) {
            Section.TEST_SYMBOLS -> {
                _model.update {
                    it.copy(
                        sectionTitle = R.string.test_symbols,
                        imageResource = R.drawable.test,
                        onNext = { onNextSection(LessonsIntent.TestSymbol(lessonId)) }
                    )
                }
            }
            Section.LEARN_SENTENCES -> {
                _model.update {
                    it.copy(
                        sectionTitle = R.string.learn_sentences,
                        imageResource = R.drawable.learn,
                        onNext = { onNextSection(LessonsIntent.LearnSentence(lessonId)) }
                    )
                }
            }
            Section.TEST_SENTENCES -> {
                _model.update {
                    it.copy(
                        sectionTitle = R.string.test_sentences,
                        imageResource = R.drawable.test,
                        onNext = { onNextSection(LessonsIntent.TestSentence(lessonId)) }
                    )
                }
            }
            else -> {
                _model.update {
                    it.copy(
                        sectionTitle = R.string.learn_symbols,
                        imageResource = R.drawable.learn,
                        onNext = { onNextSection(LessonsIntent.LearnSymbol(lessonId)) }
                    )
                }
            }
        }
    }
}