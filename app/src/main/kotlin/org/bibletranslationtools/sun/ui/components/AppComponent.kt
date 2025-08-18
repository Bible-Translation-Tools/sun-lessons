package org.bibletranslationtools.sun.ui.components

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy

typealias ComposableSlot = @Composable () -> Unit
val NoOpSlot: ComposableSlot = {}

interface ParentContext {
    fun setTopAppBar(slot: ComposableSlot?)
    fun onBackClick()
}

abstract class AppComponent(
    componentContext: ComponentContext,
    private val parentContext: ParentContext
) : ParentContext, ComponentContext by componentContext {

    init {
        parentContext.setTopAppBar(null)

        doOnDestroy {
            parentContext.setTopAppBar(null)
        }
    }

    final override fun setTopAppBar(slot: ComposableSlot?) {
        parentContext.setTopAppBar(slot)
    }

    override fun onBackClick() {
        parentContext.onBackClick()
    }
}