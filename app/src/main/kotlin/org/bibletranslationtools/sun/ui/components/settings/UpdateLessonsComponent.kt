package org.bibletranslationtools.sun.ui.components.settings

import com.arkivanov.decompose.ComponentContext
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext

interface UpdateLessonsComponent : ParentContext {
}

class DefaultUpdateLessonsComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext
) : UpdateLessonsComponent, AppComponent(componentContext, parentContext) {

}