package org.bibletranslationtools.sun.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class BookItem(
    val slug: String,
    val name: String,
    val sort: Int,
    val chapters: Int
)

fun emptyBookItem() =
    BookItem("", "", 0, 0)
