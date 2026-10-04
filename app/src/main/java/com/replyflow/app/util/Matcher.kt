package com.replyflow.app.util

import com.replyflow.app.data.MatchType
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

object Matcher {
    private val patterns = ConcurrentHashMap<String, Regex>()

    fun matches(type: MatchType, trigger: String, message: String): Boolean {
        val input = message.trim().lowercase(Locale.ROOT)
        val needle = trigger.trim().lowercase(Locale.ROOT)
        return when (type) {
            MatchType.EXACT -> input == needle
            MatchType.CONTAINS -> input.contains(needle)
            MatchType.ANY -> true
            MatchType.PATTERN -> needle.split('|').any { alternative ->
                val part = alternative.trim()
                if (part.isEmpty()) false else patterns.getOrPut(part) {
                    Regex(part.split('*').joinToString(".*") { Regex.escape(it) }, RegexOption.IGNORE_CASE)
                }.containsMatchIn(message)
            }
        }
    }
}
