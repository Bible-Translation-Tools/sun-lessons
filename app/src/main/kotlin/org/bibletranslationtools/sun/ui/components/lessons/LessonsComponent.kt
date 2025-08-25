package org.bibletranslationtools.sun.ui.components.lessons

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.GroupId
import org.bibletranslationtools.sun.utils.Section

enum class SectionState {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED
}

interface LessonsComponent: ParentContext {

    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        class List(val component: ListComponent) : Child()
        class Start(val component: StartComponent) : Child()
        class LearnSymbol(val component: LearnSymbolComponent) : Child()
        class TestSymbol(val component: TestSymbolComponent) : Child()
        class LearnSentence(val component: LearnSentenceComponent) : Child()
        class TestSentence(val component: TestSentenceComponent) : Child()
        class Complete(val component: CompleteComponent) : Child()
    }

    @Serializable
    sealed class Intent {
        @Serializable
        data class List(val selected: Long) : Intent()
        @Serializable
        data class LearnSymbol(val lessonId: Long) : Intent()
        @Serializable
        data class TestSymbol(val lessonId: Long) : Intent()
        @Serializable
        data class LearnSentence(val lessonId: Long) : Intent()
        @Serializable
        data class TestSentence(val lessonId: Long) : Intent()
    }
}

class DefaultLessonsComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    private val groupId: GroupId
) : LessonsComponent, AppComponent(componentContext, parentContext) {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, LessonsComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.List,
            handleBackButton = true,
            childFactory = ::createChild
        )

    override fun onBackClick() {
        if (stack.value.backStack.isEmpty()) {
            super.onBackClick()
        } else {
            navigation.pop()
        }
    }

    private fun createChild(config: Config, context: ComponentContext): LessonsComponent.Child =
        when (config) {
            is Config.List -> LessonsComponent.Child.List(
                DefaultListComponent(
                    componentContext = context,
                    parentContext = this,
                    groupId = groupId,
                    onContinueLesson = ::navigateContinueLesson,
                    onStartLesson = { id, section, mode ->
                        navigation.bringToFront(Config.Start(id, section))
                    }
                )
            )
            is Config.Start -> LessonsComponent.Child.Start(
                DefaultStartComponent(
                    componentContext = context,
                    parentContext = this,
                    lessonId = config.lessonId,
                    section = config.section,
                    onFinishLesson = { id, section ->
                        navigation.replaceCurrent(Config.Complete(id, section))
                    },
                    onNextSection = ::navigateNextSection
                )
            )
            is Config.LearnSymbol -> LessonsComponent.Child.LearnSymbol(
                DefaultLearnSymbolComponent(
                    componentContext = context,
                    parentContext = this,
                    lessonId = config.lessonId,
                    onFinishSection = { lessonId, section ->
                        navigation.replaceCurrent(
                            Config.Complete(lessonId, section)
                        )
                    }
                )
            )
            is Config.TestSymbol -> LessonsComponent.Child.TestSymbol(
                DefaultTestSymbolComponent(
                    componentContext = context,
                    parentContext = this,
                    lessonId = config.lessonId,
                    onFinishSection = { lessonId, section ->
                        navigation.replaceCurrent(
                            Config.Complete(lessonId, section)
                        )
                    }
                )
            )
            is Config.LearnSentence -> LessonsComponent.Child.LearnSentence(
                DefaultLearnSentenceComponent(
                    componentContext = context,
                    parentContext = this,
                    lessonId = config.lessonId,
                    onFinishSection = { lessonId, section ->
                        navigation.replaceCurrent(
                            Config.Complete(lessonId, section)
                        )
                    }
                )
            )
            is Config.TestSentence -> LessonsComponent.Child.TestSentence(
                DefaultTestSentenceComponent(
                    componentContext = context,
                    parentContext = this,
                    lessonId = config.lessonId,
                    onFinishSection = { lessonId, section ->
                        navigation.replaceCurrent(
                            Config.Complete(lessonId, section)
                        )
                    }
                )
            )
            is Config.Complete -> LessonsComponent.Child.Complete(
                DefaultCompleteComponent(
                    componentContext = context,
                    parentContext = this,
                    lessonId = config.lessonId,
                    section = config.section,
                    groupId = groupId,
                    onStartLesson = { id, section ->
                        navigation.replaceCurrent(Config.Start(id, section))
                    },
                    onNextSection = ::navigateNextSection
                )
            )
        }

    private fun navigateContinueLesson(lessonId: Long, section: Section, state: SectionState) {
        val config = when (state) {
            SectionState.NOT_STARTED -> Config.Start(lessonId, section)

            SectionState.IN_PROGRESS -> when (section) {
                Section.LEARN_SYMBOLS -> Config.LearnSymbol(lessonId)
                Section.TEST_SYMBOLS -> Config.TestSymbol(lessonId)
                Section.LEARN_SENTENCES -> Config.LearnSentence(lessonId)
                else -> Config.TestSentence(lessonId)
            }

            SectionState.COMPLETED -> when (section) {
                Section.LEARN_SYMBOLS -> {
                    Config.Start(lessonId, Section.TEST_SYMBOLS)
                }
                Section.TEST_SYMBOLS -> {
                    Config.Start(lessonId, Section.LEARN_SENTENCES)
                }
                Section.LEARN_SENTENCES -> {
                    Config.Start(lessonId, Section.TEST_SENTENCES)
                }
                else -> {
                    // When we complete test sentences, we land on completed page
                    // instead of starting page
                    Config.Complete(lessonId, Section.TEST_SENTENCES)
                }
            }
        }
        navigation.bringToFront(config)
    }

    private fun navigateNextSection(intent: LessonsComponent.Intent) {
        when (intent) {
            is LessonsComponent.Intent.LearnSymbol -> {
                navigation.replaceCurrent(Config.LearnSymbol(
                    intent.lessonId
                ))
            }
            is LessonsComponent.Intent.TestSymbol -> {
                navigation.replaceCurrent(Config.TestSymbol(
                    intent.lessonId
                ))
            }
            is LessonsComponent.Intent.LearnSentence -> {
                navigation.replaceCurrent(Config.LearnSentence(
                    intent.lessonId
                ))
            }
            is LessonsComponent.Intent.TestSentence -> {
                navigation.replaceCurrent(Config.TestSentence(
                    intent.lessonId
                ))
            }
            else -> {}
        }
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object List : Config
        @Serializable
        data class Start(val lessonId: Long, val section: Section) : Config
        @Serializable
        data class LearnSymbol(val lessonId: Long) : Config
        @Serializable
        data class TestSymbol(val lessonId: Long) : Config
        @Serializable
        data class LearnSentence(val lessonId: Long) : Config
        @Serializable
        data class TestSentence(val lessonId: Long) : Config
        @Serializable
        data class Complete(val lessonId: Long, val section: Section) : Config
    }
}