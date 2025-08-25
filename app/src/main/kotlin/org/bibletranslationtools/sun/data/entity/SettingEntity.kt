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
        const val LAST_SENTENCE = "last_sentence"
        const val LAST_SYMBOL = "last_symbol"

        fun lastSection(groupId: String): String {
            return "${LAST_SECTION}_$groupId"
        }

        fun lastLesson(groupId: String): String {
            return "${LAST_LESSON}_$groupId"
        }

        fun lastSentence(groupId: String): String {
            return "${LAST_SENTENCE}_$groupId"
        }

        fun lastSymbol(groupId: String): String {
            return "${LAST_SYMBOL}_$groupId"
        }
    }
}