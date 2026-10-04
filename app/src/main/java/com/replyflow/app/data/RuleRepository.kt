package com.replyflow.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray

class RuleRepository(private val context: Context) {
    private val store = AtomicJsonStore(context, "rules.json")
    private val mutex = Mutex()
    private val _rules = MutableStateFlow(load())
    val rules: StateFlow<List<Rule>> = _rules

    private fun load(): List<Rule> = runCatching {
        val raw = store.readOrNull() ?: context.assets.open("rules.json").bufferedReader().use { it.readText() }
        val array = JSONArray(raw)
        buildList { for (i in 0 until array.length()) add(array.getJSONObject(i).toRule()) }
    }.getOrDefault(emptyList())

    suspend fun upsert(rule: Rule) = mutex.withLock {
        val list = _rules.value.toMutableList()
        val index = list.indexOfFirst { it.id == rule.id }
        if (index >= 0) list[index] = rule else list.add(rule)
        commit(list)
    }

    suspend fun delete(id: Long) = mutex.withLock { commit(_rules.value.filterNot { it.id == id }) }

    suspend fun setActive(id: Long, active: Boolean) = mutex.withLock {
        commit(_rules.value.map { if (it.id == id) it.copy(active = active) else it })
    }

    suspend fun incrementSent(id: Long) = mutex.withLock {
        commit(_rules.value.map { if (it.id == id) it.copy(sent = it.sent + 1) else it })
    }

    suspend fun restoreDemo() = mutex.withLock {
        val raw = context.assets.open("rules.json").bufferedReader().use { it.readText() }
        val arr = JSONArray(raw)
        commit(buildList { for (i in 0 until arr.length()) add(arr.getJSONObject(i).toRule()) })
    }

    suspend fun importJson(raw: String) = mutex.withLock {
        val array = JSONArray(raw)
        val parsed = buildList { for (i in 0 until array.length()) add(array.getJSONObject(i).toRule()) }
        require(parsed.isNotEmpty()) { "El archivo no contiene reglas" }
        commit(parsed)
    }

    fun exportJson(): String = JSONArray(_rules.value.map { it.toJson() }).toString(2)

    private fun commit(list: List<Rule>) {
        store.write(JSONArray(list.map { it.toJson() }).toString(2))
        _rules.value = list
    }
}
