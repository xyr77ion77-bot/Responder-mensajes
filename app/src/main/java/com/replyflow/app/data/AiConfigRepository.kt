package com.replyflow.app.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject

class AiConfigRepository(context: Context) {
    private val store = AtomicJsonStore(context, "ai_config.json")
    private val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    private val secure = EncryptedSharedPreferences.create(
        context, "replyflow_secure", masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    private val _config = MutableStateFlow(load())
    val config: StateFlow<AiConfig?> = _config

    private fun load(): AiConfig? = runCatching {
        val raw = store.readOrNull() ?: return null
        JSONObject(raw).toAiConfig(secure.getString("ai_api_key", "").orEmpty())
    }.getOrNull()

    @Synchronized fun save(config: AiConfig) {
        secure.edit().putString("ai_api_key", config.apiKey).apply()
        store.write(config.toJson(includeKey = false).toString(2))
        _config.value = config
    }

    @Synchronized fun clear() {
        secure.edit().remove("ai_api_key").apply()
        store.rawFile().delete()
        _config.value = null
    }
}
