package org.bibletranslationtools.sun.ui.components.settings

import com.arkivanov.decompose.ComponentContext
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext

interface SettingsListComponent : ParentContext {

    fun onDownloadsClick()
    fun onCheckUpdatesClick()
}

class DefaultSettingsListComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    private val onNavigateDownloads: () -> Unit,
    private val onNavigateCheckUpdates: () -> Unit
) : SettingsListComponent, AppComponent(componentContext, parentContext) {

    override fun onDownloadsClick() {
        onNavigateDownloads()
    }

    override fun onCheckUpdatesClick() {
        onNavigateCheckUpdates()
    }
}