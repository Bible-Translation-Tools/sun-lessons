package org.bibletranslationtools.sun.ui.components.lessons

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.LessonsIntent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.LessonMode
import org.bibletranslationtools.sun.utils.Section

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
}

class DefaultLessonsComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    intent: LessonsIntent,
    private val onNavigateHome: () -> Unit
) : LessonsComponent, AppComponent(componentContext, parentContext) {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, LessonsComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = when (intent) {
                is LessonsIntent.List -> Config.List(intent.selected)
                is LessonsIntent.Start -> Config.Start(intent.id, intent.section)
                is LessonsIntent.LearnSymbol -> Config.LearnSymbol(intent.lessonId)
                is LessonsIntent.TestSymbol -> Config.TestSymbol(intent.lessonId)
                is LessonsIntent.LearnSentence -> Config.LearnSentence(intent.lessonId)
                is LessonsIntent.TestSentence -> Config.TestSentence(intent.lessonId)
                is LessonsIntent.Complete -> Config.Complete(intent.id, intent.section)
            },
            handleBackButton = true,
            childFactory = ::createChild
        )

    private fun createChild(config: Config, context: ComponentContext): LessonsComponent.Child =
        when (config) {
            is Config.List -> LessonsComponent.Child.List(
                DefaultListComponent(
                    componentContext = context,
                    parentContext = this,
                    selectedLessonId = config.selected,
                    onStartLesson = { id, section, mode ->
                        navigation.bringToFront(Config.Start(id, section, mode))
                    }
                )
            )
            is Config.Start -> LessonsComponent.Child.Start(
                DefaultStartComponent(
                    componentContext = context,
                    parentContext = this,
                    lessonId = config.lessonId,
                    section = config.section,
                    mode = config.mode,
                    onFinishLesson = { id, section ->
                        navigation.bringToFront(Config.Complete(id, section))
                    },
                    onNextSection = ::navigateNextSection,
                    onNavigateList = {
                        navigation.replaceAll(Config.List(it))
                    },
                    onNavigateHome = onNavigateHome
                )
            )
            is Config.LearnSymbol -> LessonsComponent.Child.LearnSymbol(
                DefaultLearnSymbolComponent(
                    componentContext = context,
                    parentContext = this,
                    lessonId = config.lessonId,
                    onFinishSection = { lessonId, section, mode ->
                        navigation.bringToFront(
                            Config.Complete(lessonId, section, mode)
                        )
                    },
                    onNavigateList = {
                        navigation.replaceAll(Config.List(it))
                    },
                    onNavigateHome = onNavigateHome
                )
            )
            is Config.TestSymbol -> LessonsComponent.Child.TestSymbol(
                DefaultTestSymbolComponent(
                    componentContext = context,
                    parentContext = this,
                    lessonId = config.lessonId,
                    onFinishSection = { lessonId, section, mode ->
                        navigation.bringToFront(
                            Config.Complete(lessonId, section, mode)
                        )
                    },
                    onNavigateList = {
                        navigation.replaceAll(Config.List(it))
                    },
                    onNavigateHome = onNavigateHome
                )
            )
            is Config.LearnSentence -> LessonsComponent.Child.LearnSentence(
                DefaultLearnSentenceComponent(
                    componentContext = context,
                    parentContext = this,
                    lessonId = config.lessonId,
                    onFinishSection = { lessonId, section, mode ->
                        navigation.bringToFront(
                            Config.Complete(lessonId, section, mode)
                        )
                    },
                    onNavigateList = {
                        navigation.replaceAll(Config.List(it))
                    },
                    onNavigateHome = onNavigateHome
                )
            )
            is Config.TestSentence -> LessonsComponent.Child.TestSentence(
                DefaultTestSentenceComponent(
                    componentContext = context,
                    parentContext = this
                )
            )
            is Config.Complete -> LessonsComponent.Child.Complete(
                DefaultCompleteComponent(
                    componentContext = context,
                    parentContext = this,
                    lessonId = config.lessonId,
                    section = config.section,
                    mode = config.mode,
                    onStartLesson = { id, section ->
                        navigation.bringToFront(Config.Start(id, section))
                    },
                    onNextSection = ::navigateNextSection,
                    onNavigateList = {
                        navigation.replaceAll(Config.List(it))
                    },
                    onNavigateHome = onNavigateHome
                )
            )
        }

    private fun navigateNextSection(intent: LessonsIntent) {
        when (intent) {
            is LessonsIntent.LearnSymbol -> {
                navigation.bringToFront(Config.LearnSymbol(
                    intent.lessonId
                ))
            }
            is LessonsIntent.TestSymbol -> {
                navigation.bringToFront(Config.TestSymbol(
                    intent.lessonId
                ))
            }
            is LessonsIntent.LearnSentence -> {
                navigation.bringToFront(Config.LearnSentence(
                    intent.lessonId
                ))
            }
            is LessonsIntent.TestSentence -> {
                navigation.bringToFront(Config.TestSentence(
                    intent.lessonId
                ))
            }
            else -> {}
        }
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data class List(val selected: Int) : Config
        @Serializable
        data class Start(
            val lessonId: Int,
            val section: Section,
            val mode: LessonMode = LessonMode.NORMAL
        ) : Config
        @Serializable
        data class LearnSymbol(val lessonId: Int) : Config
        @Serializable
        data class TestSymbol(val lessonId: Int) : Config
        @Serializable
        data class LearnSentence(val lessonId: Int) : Config
        @Serializable
        data class TestSentence(val lessonId: Int) : Config
        @Serializable
        data class Complete(
            val lessonId: Int,
            val section: Section,
            val mode: LessonMode = LessonMode.NORMAL
        ) : Config
    }
}