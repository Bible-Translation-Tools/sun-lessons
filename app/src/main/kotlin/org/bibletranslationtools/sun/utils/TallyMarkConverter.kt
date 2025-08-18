package org.bibletranslationtools.sun.utils

object TallyMarkConverter {
    fun toText(number: Long): String {
        var text = ""
        val fivesCount = number.toInt() / 5

        repeat(fivesCount) {
            text += "5"
        }

        val remainder = number % 5

        if (remainder > 0) {
            text += number % 5
        }

        return text
    }
}