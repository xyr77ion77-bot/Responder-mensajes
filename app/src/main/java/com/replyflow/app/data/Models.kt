package com.replyflow.app.data

enum class MatchType { EXACT, CONTAINS, PATTERN, ANY }

data class Rule(
    val id: Long,
    val name: String,
    val trigger: String,
    val match: MatchType,
    val reply: String,
    val channels: List<String>,
    val active: Boolean,
    val aiEnabled: Boolean,
    val scheduled: Boolean,
    val start: String,
    val end: String,
    val allow: List<String>,
    val deny: List<String>,
    val sent: Int
)

data class AiConfig(
    val provider: String,
    val platform: String,
    val url: String,
    val apiKey: String,
    val model: String,
    val prompt: String
)

data class AppSettings(
    val serviceEnabled: Boolean = true,
    val strictMode: Boolean = false,
    val avoidDuplicates: Boolean = true,
    val replyDelayMs: Long = 0,
    val themeDark: Boolean = true
)

data class IncomingMessage(
    val packageName: String,
    val channel: String,
    val sender: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class MatchResult(val rule: Rule, val fallbackReply: String)
