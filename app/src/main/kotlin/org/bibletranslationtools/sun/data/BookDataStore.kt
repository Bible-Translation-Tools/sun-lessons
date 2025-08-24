package org.bibletranslationtools.sun.data

import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.model.BookItem
import org.bibletranslationtools.sun.utils.AssetReader
import org.bibletranslationtools.sun.utils.Utils

interface BookDataStore {
    fun getBook(slug: String): BookItem?
    fun getBooks(): List<BookItem>
}

class BookDataStoreImpl(
    private val assetReader: AssetReader
) : BookDataStore {
    private val books: Map<String, BookItem> by lazy {
        val json = assetReader.readRaw(R.raw.books)
        Utils.JsonLenient.decodeFromString<List<BookItem>>(json)
            .associateBy { it.slug }
    }

    override fun getBook(slug: String): BookItem? {
        return books[slug]
    }

    override fun getBooks(): List<BookItem> {
        return books.values.toList()
    }
}