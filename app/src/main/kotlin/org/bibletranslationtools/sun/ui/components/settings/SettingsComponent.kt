package org.bibletranslationtools.sun.ui.components.settings

import com.arkivanov.decompose.ComponentContext

interface SettingsComponent {

}

class DefaultSettingsComponent(
    componentContext: ComponentContext
) : SettingsComponent, ComponentContext by componentContext {

}