package com.replyflow.app.util

import android.content.Context
import java.io.File
import java.time.Instant

class EventLogger(context: Context) {
    private val file = File(context.filesDir, "events.log")
    @Synchronized fun append(type: String, detail: String) {
        if (file.exists() && file.length() > 128 * 1024) file.writeText("")
        file.appendText("${Instant.now()}\t$type\t${detail.replace('\n', ' ').take(240)}\n")
    }
    fun read(): List<String> = runCatching { file.readLines().takeLast(200) }.getOrDefault(emptyList())
}
