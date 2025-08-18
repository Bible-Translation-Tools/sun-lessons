package org.bibletranslationtools.sun.ui.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.bibletranslationtools.sun.ui.components.lessons.DefaultLessonsComponent
import org.bibletranslationtools.sun.ui.components.lessons.LessonType
import org.bibletranslationtools.sun.ui.components.lessons.LessonsComponent
import org.bibletranslationtools.sun.ui.components.progress.DefaultProgressComponent
import org.bibletranslationtools.sun.ui.components.progress.ProgressComponent
import org.bibletranslationtools.sun.ui.components.settings.DefaultSettingsComponent
import org.bibletranslationtools.sun.ui.components.settings.SettingsComponent
import org.bibletranslationtools.sun.ui.components.splash.DefaultSplashComponent
import org.bibletranslationtools.sun.ui.components.splash.SplashComponent
import org.bibletranslationtools.sun.ui.navigation.MainTab

interface RootComponent : ParentContext {
    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        class Splash(val component: SplashComponent) : Child()
        class Home(val component: LessonsComponent) : Child()
        class Progress(val component: ProgressComponent) : Child()
        class Lessons(val component: LessonsComponent) : Child()
        class Settings(val component: SettingsComponent) : Child()
    }

    fun onTabClicked(tab: MainTab)
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val backCallback = BackCallback(onBack = ::onNavigateBack)

    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Splash,
            handleBackButton = true,
            childFactory = ::createChild
        )

    init {
        backHandler.register(backCallback)
    }

    override fun onTabClicked(tab: MainTab) {
        when (tab) {
            MainTab.Home -> {
                navigation.bringToFront(Config.Home)
            }
            MainTab.Progress -> {
                navigation.bringToFront(Config.Progress)
            }
            MainTab.Lessons -> {
                componentScope.launch {
                    navigation.bringToFront(Config.Lessons)
                }
            }
            MainTab.Settings -> {
                navigation.bringToFront(Config.Settings)
            }
        }
    }

    override fun onBackClick() {
        navigation.pop()
    }

    private fun createChild(config: Config, context: ComponentContext): RootComponent.Child =
        when (config) {
            is Config.Splash -> RootComponent.Child.Splash(
                DefaultSplashComponent(
                    componentContext = context,
                    onInitDone = {
                        navigation.replaceCurrent(Config.Home)
                    }
                )
            )
            is Config.Home -> RootComponent.Child.Home(
                DefaultLessonsComponent(
                    componentContext = context,
                    parentContext = this,
                    lessonType = LessonType.BASIC
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
                    lessonType = LessonType.SCRIPTURE
                )
            )
            is Config.Settings -> RootComponent.Child.Settings(
                DefaultSettingsComponent(context)
            )
        }

    private fun onNavigateBack() {
        val config = stack.value.active.configuration
        when (config) {
            !is Config.Home -> navigation.replaceAll(Config.Home)
            else -> onFinished()
        }
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Splash : Config
        @Serializable
        data object Home : Config
        @Serializable
        data object Progress : Config
        @Serializable
        data object Lessons : Config
        @Serializable
        data object Settings : Config
    }
}