package org.bibletranslationtools.sun.utils

import android.content.Context

interface AssetReader {
    fun readText(fileName: String): String
}

class AssetReaderImpl(private val context: Context) : AssetReader {
    override fun readText(fileName: String): String {
        return context.assets
            .open(fileName)
            .bufferedReader()
            .use {
                it.readText()
            }
    }
}
