package com.replyflow.app.util

import java.time.LocalTime

object TimeWindow {
    fun contains(start: String, end: String, now: LocalTime = LocalTime.now()): Boolean {
        val from = runCatching { LocalTime.parse(start) }.getOrNull() ?: return true
        val to = runCatching { LocalTime.parse(end) }.getOrNull() ?: return true
        if (from == to) return true
        return if (from < to) !now.isBefore(from) && now.isBefore(to)
        else !now.isBefore(from) || now.isBefore(to) // Cruce de medianoche.
    }
}
