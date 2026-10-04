package com.replyflow.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsRepository(context: Context) {
    private val store = AtomicJsonStore(context, "settings.json")
    private val _settings = MutableStateFlow(runCatching { store.readOrNull()?.let { org.json.JSONObject(it).toSettings() } }.getOrNull() ?: AppSettings())
    val settings: StateFlow<AppSettings> = _settings

    @Synchronized fun update(transform: (AppSettings) -> AppSettings) {
        val next = transform(_settings.value)
        store.write(next.toJson().toString(2))
        _settings.value = next
    }
}
