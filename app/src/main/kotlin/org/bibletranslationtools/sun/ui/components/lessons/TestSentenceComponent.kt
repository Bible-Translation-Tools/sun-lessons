package org.bibletranslationtools.sun.ui.components.lessons

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext

interface TestSentenceComponent : ParentContext {

    val model: Value<Model>

    data class Model(
        val cards: List<Card> = emptyList()
    )
}

class DefaultTestSentenceComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext
) : TestSentenceComponent, AppComponent(componentContext, parentContext) {

    private val _model = MutableValue(TestSentenceComponent.Model())
    override val model: Value<TestSentenceComponent.Model> = _model
}