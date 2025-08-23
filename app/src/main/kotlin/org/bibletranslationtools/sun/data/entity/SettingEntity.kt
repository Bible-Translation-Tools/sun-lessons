package org.bibletranslationtools.sun.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "settings", primaryKeys = ["name"])
data class SettingEntity(
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "value")
    val value: String
) {
    companion object Companion {
        const val VERSION = "version"
        const val LAST_SECTION = "last_section"
        const val LAST_LESSON = "last_lesson"
        const val LAST_SYMBOL = "last_symbol"
        const val LAST_SENTENCE = "last_sentence"

    }
}