package org.bibletranslationtools.sun.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.bibletranslationtools.sun.utils.Utils.getCurrentTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

enum class Section(val id: String) {
    LEARN_SYMBOLS("learn_symbols"),
    TEST_SYMBOLS("test_symbols"),
    LEARN_SENTENCES("learn_sentences"),
    TEST_SENTENCES("test_sentences"),
    TEST_ALL("test_all");

    companion object {
        private val map = entries.toTypedArray().associateBy { it.id.lowercase() }
        fun of(id: String) = map[id.lowercase()] ?: LEARN_SYMBOLS
    }
}

@OptIn(ExperimentalTime::class)
fun Long.toLocalDateTime(): LocalDateTime {
    val timeZone = TimeZone.currentSystemDefault()
    val instant = Instant.fromEpochSeconds(this)
    return instant.toLocalDateTime(timeZone)
}

@OptIn(ExperimentalTime::class)
fun LocalDateTime.toTimestamp(): Long {
    val timeZone = TimeZone.currentSystemDefault()
    return this.toInstant(timeZone).epochSeconds
}

@OptIn(ExperimentalTime::class)
fun String.toLocalDateTime(): LocalDateTime {
    runCatching {
        val instant = Instant.parse(this)
        return instant.toLocalDateTime(TimeZone.UTC)
    }

    runCatching {
        return LocalDateTime.parse(this)
    }

    runCatching {
        val date = LocalDate.parse(this)
        return date.atTime(0, 0)
    }

    return getCurrentTime()
}