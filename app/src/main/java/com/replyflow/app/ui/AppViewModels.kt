package com.replyflow.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.replyflow.app.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(rules: RuleRepository) : ViewModel() {
    val state: StateFlow<HomeState> = rules.rules.map { list -> HomeState(list.size, list.sumOf { it.sent }) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeState())
}
data class HomeState(val rules: Int = 0, val sent: Int = 0)

class RulesViewModel(private val repository: RuleRepository, private val settingsRepository: SettingsRepository) : ViewModel() {
    val rules = repository.rules
    val settings = settingsRepository.settings
    fun serviceEnabled(value: Boolean) = settingsRepository.update { it.copy(serviceEnabled = value) }
    fun setActive(id: Long, value: Boolean) = viewModelScope.launch { repository.setActive(id, value) }
    fun delete(id: Long) = viewModelScope.launch { repository.delete(id) }
}

data class EditorState(
    val id: Long = 0, val name: String = "", val trigger: String = "", val match: MatchType = MatchType.CONTAINS,
    val reply: String = "", val channels: Set<String> = setOf("sms", "wa", "business"), val active: Boolean = true,
    val aiEnabled: Boolean = false, val scheduled: Boolean = false, val start: String = "09:00", val end: String = "18:00",
    val allow: List<String> = emptyList(), val deny: List<String> = emptyList(), val sent: Int = 0
)

class EditorViewModel(private val repository: RuleRepository, private val aiRepository: AiConfigRepository) : ViewModel() {
    private val _state = MutableStateFlow(EditorState())
    val state: StateFlow<EditorState> = _state
    val aiConfig = aiRepository.config

    fun load(id: Long) {
        val rule = repository.rules.value.firstOrNull { it.id == id }
        _state.value = if (rule == null) EditorState(id = System.currentTimeMillis()) else EditorState(
            rule.id, rule.name, rule.trigger, rule.match, rule.reply, rule.channels.toSet(), rule.active,
            rule.aiEnabled, rule.scheduled, rule.start, rule.end, rule.allow, rule.deny, rule.sent
        )
    }
    fun update(block: EditorState.() -> EditorState) { _state.update(block) }
    fun save(onDone: () -> Unit) = viewModelScope.launch {
        val s = _state.value
        if (s.name.isBlank() || s.trigger.isBlank() || s.reply.isBlank() || s.channels.isEmpty()) return@launch
        repository.upsert(Rule(s.id, s.name.trim(), if (s.match == MatchType.ANY) "*" else s.trigger.trim(), s.match,
            s.reply.trim(), s.channels.toList(), s.active, s.aiEnabled && aiRepository.config.value != null,
            s.scheduled, s.start, s.end, s.allow, s.deny, s.sent))
        onDone()
    }
    fun delete(onDone: () -> Unit) = viewModelScope.launch { repository.delete(_state.value.id); onDone() }
}

data class AiWizardState(
    val step: Int = 1, val provider: String = "openrouter", val platform: String = "OpenRouter",
    val url: String = "https://openrouter.ai/api/v1/", val apiKey: String = "",
    val model: String = "openai/gpt-4.1-mini", val prompt: String = "Responde de forma breve, amable y profesional.",
    val showKey: Boolean = false
)
class AiViewModel(private val repository: AiConfigRepository) : ViewModel() {
    private val _state = MutableStateFlow(AiWizardState())
    val state: StateFlow<AiWizardState> = _state
    val saved = repository.config
    fun load() { repository.config.value?.let { c -> _state.value = AiWizardState(1, c.provider, c.platform, c.url, c.apiKey, c.model, c.prompt) } }
    fun update(block: AiWizardState.() -> AiWizardState) { _state.update(block) }
    fun provider(value: String) {
        _state.value = when (value) {
            "google" -> _state.value.copy(provider=value, platform="Google Gemini", url="https://generativelanguage.googleapis.com/", model="gemini-2.5-flash")
            "custom" -> _state.value.copy(provider=value, platform="", url="", model="")
            else -> _state.value.copy(provider="openrouter", platform="OpenRouter", url="https://openrouter.ai/api/v1/", model="openai/gpt-4.1-mini")
        }
    }
    fun next(): Boolean {
        val s = _state.value
        if (s.step == 1 && (s.platform.isBlank() || s.url.isBlank())) return false
        if (s.step == 2 && s.apiKey.isBlank()) return false
        if (s.step < 3) { _state.value = s.copy(step = s.step + 1); return false }
        if (s.model.isBlank()) return false
        repository.save(AiConfig(s.provider, s.platform, s.url.let { if (it.endsWith('/')) it else "$it/" }, s.apiKey, s.model, s.prompt))
        return true
    }
    fun back(): Boolean { val s=_state.value; return if(s.step>1){_state.value=s.copy(step=s.step-1);false}else true }
    fun clear(){ repository.clear(); _state.value=AiWizardState() }
}

class ReportsViewModel(rules: RuleRepository) : ViewModel() {
    val rules = rules.rules
    val total = rules.rules.map { list -> list.sumOf { it.sent } }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}
class HelpViewModel : ViewModel()
class SettingsViewModel(private val rules: RuleRepository, private val settingsRepository: SettingsRepository) : ViewModel() {
    val settings = settingsRepository.settings
    fun serviceEnabled(value:Boolean)=settingsRepository.update{it.copy(serviceEnabled=value)}
    fun strict(value:Boolean)=settingsRepository.update{it.copy(strictMode=value)}
    fun duplicates(value:Boolean)=settingsRepository.update{it.copy(avoidDuplicates=value)}
    fun delay(ms:Long)=settingsRepository.update{it.copy(replyDelayMs=ms)}
    fun themeDark(value:Boolean)=settingsRepository.update{it.copy(themeDark=value)}
    fun restore()=viewModelScope.launch{rules.restoreDemo()}
    fun importJson(raw: String)=viewModelScope.launch{rules.importJson(raw)}
    fun exportJson():String=rules.exportJson()
}
