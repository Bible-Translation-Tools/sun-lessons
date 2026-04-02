package org.bibletranslationtools.sun.utils

import android.content.Context

interface AssetReader {
    fun readText(fileName: String): String
    fun readRaw(resource: Int): String
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

    override fun readRaw(resource: Int): String {
        return context.resources.openRawResource(resource).use { input ->
            input.bufferedReader().use { reader ->
                reader.readText()
            }
        }
    }
}
