package org.bibletranslationtools.sun.ui.components.progress

import com.arkivanov.decompose.ComponentContext

interface ProgressComponent {

}

class DefaultProgressComponent(
    componentContext: ComponentContext
) : ProgressComponent, ComponentContext by componentContext {

}