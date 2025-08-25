package org.bibletranslationtools.sun.ui.components.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.components.AppComponent
import org.bibletranslationtools.sun.ui.components.ParentContext
import org.bibletranslationtools.sun.ui.model.BookItem
import org.bibletranslationtools.sun.utils.AssetReader
import org.bibletranslationtools.sun.utils.Utils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface SelectChapterComponent : ParentContext {

    val model: Value<Model>

    data class Model(
        val books: List<BookItem> = emptyList()
    )

    fun onChapterSelected(book: BookItem, chapter: Int)
}

class DefaultSelectChapterComponent(
    componentContext: ComponentContext,
    parentContext: ParentContext,
    private val onNavigateSearchChapter: (BookItem, Int) -> Unit
) : SelectChapterComponent, KoinComponent, AppComponent(componentContext, parentContext) {

    private val assetReader: AssetReader by inject()

    private val _model = MutableValue(SelectChapterComponent.Model())
    override val model: Value<SelectChapterComponent.Model> = _model

    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        componentScope.launch {
            loadBooks()
        }
    }

    override fun onChapterSelected(book: BookItem, chapter: Int) {
        onNavigateSearchChapter(book, chapter)
    }

    private suspend fun loadBooks() {
        val books = withContext(Dispatchers.IO) {
            val json = assetReader.readRaw(R.raw.books)
            Utils.JsonLenient.decodeFromString<List<BookItem>>(json)
        }
        _model.update { it.copy(books = books) }
    }
}