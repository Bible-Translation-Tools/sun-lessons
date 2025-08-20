package org.bibletranslationtools.sun.ui.components.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.BookItem

interface SettingsComponent : ParentContext {

    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        class SettingsList(val component: SettingsListComponent) : Child()
        class SelectChapter(val component: SelectChapterComponent) : Child()
        class DownloadLessons(val component: DownloadLessonsComponent) : Child()
        class UpdateLessons(val component: UpdateLessonsComponent) : Child()
    }
}

class DefaultSettingsComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext
) : SettingsComponent, AppComponent(componentContext, parentContext) {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, SettingsComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.SettingsList,
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

    private fun createChild(config: Config, context: ComponentContext): SettingsComponent.Child =
        when (config) {
            is Config.SettingsList -> SettingsComponent.Child.SettingsList(
                DefaultSettingsListComponent(
                    componentContext = context,
                    parentContext = this,
                    onNavigateDownloads = {
                        navigation.bringToFront(Config.SelectChapter)
                    },
                    onNavigateCheckUpdates = {
                        navigation.bringToFront(Config.UpdateLessons)
                    }
                )
            )
            is Config.SelectChapter -> SettingsComponent.Child.SelectChapter(
                DefaultSelectChapterComponent(
                    componentContext = context,
                    parentContext = this,
                    onNavigateSearchChapter = { book, chapter ->
                        navigation.bringToFront(
                            Config.DownloadLessons(book, chapter)
                        )
                    }
                )
            )
            is Config.DownloadLessons -> SettingsComponent.Child.DownloadLessons(
                DefaultDownloadLessonsComponent(
                    componentContext = context,
                    parentContext = this,
                    bookItem = config.bookItem,
                    chapter = config.chapter
                )
            )
            is Config.UpdateLessons -> SettingsComponent.Child.UpdateLessons(
                DefaultUpdateLessonsComponent(
                    componentContext = context,
                    parentContext = this
                )
            )
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object SettingsList : Config
        @Serializable
        data object SelectChapter : Config
        @Serializable
        data class DownloadLessons(val bookItem: BookItem, val chapter: Int) : Config
        @Serializable
        data object UpdateLessons : Config
    }
}