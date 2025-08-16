package org.bibletranslationtools.sun.ui.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.bibletranslationtools.sun.data.repositories.SettingsRepository
import org.bibletranslationtools.sun.ui.components.home.DefaultHomeComponent
import org.bibletranslationtools.sun.ui.components.home.HomeComponent
import org.bibletranslationtools.sun.ui.components.lessons.DefaultLessonsComponent
import org.bibletranslationtools.sun.ui.components.lessons.LessonsComponent
import org.bibletranslationtools.sun.ui.components.progress.DefaultProgressComponent
import org.bibletranslationtools.sun.ui.components.progress.ProgressComponent
import org.bibletranslationtools.sun.ui.components.settings.DefaultSettingsComponent
import org.bibletranslationtools.sun.ui.components.settings.SettingsComponent
import org.bibletranslationtools.sun.ui.navigation.MainTab
import org.bibletranslationtools.sun.utils.Section
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface RootComponent : ParentContext {
    val stack: Value<ChildStack<*, Child>>

    val topBarSlot: Value<ComposableSlot>

    sealed class Child {
        class Home(val component: HomeComponent) : Child()
        class Progress(val component: ProgressComponent) : Child()
        class Lessons(val component: LessonsComponent) : Child()
        class Settings(val component: SettingsComponent) : Child()
    }

    fun onTabClicked(tab: MainTab)
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit
) : RootComponent, KoinComponent, ComponentContext by componentContext {

    private val settingsRepository: SettingsRepository by inject()

    private val navigation = StackNavigation<Config>()

    private val _topBarSlot = MutableValue(NoOpSlot)
    override val topBarSlot: Value<ComposableSlot> = _topBarSlot

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Home,
            handleBackButton = true,
            childFactory = ::createChild
        )

    override fun onTabClicked(tab: MainTab) {
        when (tab) {
            MainTab.Home -> {
                navigation.replaceAll(Config.Home)
            }
            MainTab.Progress -> {
                navigation.replaceAll(Config.Progress)
            }
            MainTab.Lessons -> {
                componentScope.launch {
                    val lastLesson = getLastLesson()
                    navigation.replaceAll(Config.Lessons(
                        LessonsIntent.List(lastLesson)
                    ))
                }
            }
            MainTab.Settings -> {
                navigation.replaceAll(Config.Settings)
            }
        }
    }

    override fun setTopAppBar(slot: ComposableSlot?) {
        _topBarSlot.value = slot ?: NoOpSlot
    }

    override fun onBackClick() {
        onFinished()
    }

    private fun createChild(config: Config, context: ComponentContext): RootComponent.Child =
        when (config) {
            is Config.Home -> RootComponent.Child.Home(
                DefaultHomeComponent(
                    componentContext = context,
                    parentContext = this,
                    onNavigateLearn = ::navigateSection
                )
            )
            is Config.Progress -> RootComponent.Child.Progress(
                DefaultProgressComponent(
                    componentContext = context,
                    parentContext = this
                )
            )
            is Config.Lessons -> RootComponent.Child.Lessons(
                DefaultLessonsComponent(
                    componentContext = context,
                    parentContext = this,
                    intent = config.intent,
                    onNavigateHome = {
                        navigation.replaceAll(Config.Home)
                    }
                )
            )
            is Config.Settings -> RootComponent.Child.Settings(
                DefaultSettingsComponent(context)
            )
        }

    private fun navigateSection(
        lastSection: Section,
        lastLesson: Int,
        state: DefaultHomeComponent.SectionState
    ) {
        val intent = when (state) {
            DefaultHomeComponent.SectionState.NOT_STARTED -> {
                LessonsIntent.Start(lastLesson, lastSection)
            }
            DefaultHomeComponent.SectionState.IN_PROGRESS -> {
                when (lastSection) {
                    Section.LEARN_SYMBOLS -> LessonsIntent.LearnSymbol(lastLesson)
                    Section.TEST_SYMBOLS -> LessonsIntent.TestSymbol(lastLesson)
                    Section.LEARN_SENTENCES -> LessonsIntent.LearnSentence(lastLesson)
                    else -> LessonsIntent.TestSentence(lastLesson)
                }
            }
            DefaultHomeComponent.SectionState.COMPLETED -> {
                when (lastSection) {
                    Section.LEARN_SYMBOLS -> {
                        LessonsIntent.Start(lastLesson, Section.TEST_SYMBOLS)
                    }
                    Section.TEST_SYMBOLS -> {
                        LessonsIntent.Start(lastLesson, Section.LEARN_SENTENCES)
                    }
                    Section.LEARN_SENTENCES -> {
                        LessonsIntent.Start(lastLesson, Section.TEST_SENTENCES)
                    }
                    else -> {
                        // When we complete test sentences, we land on completed page
                        // instead of starting page
                        LessonsIntent.Complete(lastLesson, Section.TEST_SENTENCES)
                    }
                }
            }
        }
        navigation.bringToFront(Config.Lessons(intent))
    }

    suspend fun getLastLesson(): Int {
        return settingsRepository.get("last_lesson")?.value?.toInt() ?: 1
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Home : Config
        @Serializable
        data object Progress : Config
        @Serializable
        data class Lessons(val intent: LessonsIntent) : Config
        @Serializable
        data object Settings : Config
    }
}

@Serializable
sealed class LessonsIntent {
    @Serializable
    data class List(val selected: Int) : LessonsIntent()
    @Serializable
    data class Start(val id: Int, val section: Section) : LessonsIntent()
    @Serializable
    data class LearnSymbol(val lessonId: Int) : LessonsIntent()
    @Serializable
    data class TestSymbol(val lessonId: Int) : LessonsIntent()
    @Serializable
    data class LearnSentence(val lessonId: Int) : LessonsIntent()
    @Serializable
    data class TestSentence(val lessonId: Int) : LessonsIntent()
    @Serializable
    data class Complete(val id: Int, val section: Section) : LessonsIntent()
}