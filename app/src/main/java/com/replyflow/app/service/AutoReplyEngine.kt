package com.replyflow.app.service

import com.replyflow.app.data.IncomingMessage
import com.replyflow.app.data.MatchResult
import com.replyflow.app.data.Rule
import com.replyflow.app.util.Matcher
import com.replyflow.app.util.TimeWindow

/** Motor sin dependencias Android: recibe datos y devuelve la primera regla válida. */
class AutoReplyEngine {
    fun evaluate(rules: List<Rule>, incoming: IncomingMessage): MatchResult? {
        for (rule in rules) {
            if (!rule.active || incoming.channel !in rule.channels) continue
            if (rule.scheduled && !TimeWindow.contains(rule.start, rule.end)) continue
            if (rule.deny.any { sameIdentity(it, incoming.sender) }) continue
            if (rule.allow.isNotEmpty() && rule.allow.none { sameIdentity(it, incoming.sender) }) continue
            if (Matcher.matches(rule.match, rule.trigger, incoming.text)) return MatchResult(rule, rule.reply)
        }
        return null
    }

    private fun sameIdentity(filter: String, sender: String): Boolean =
        filter.trim().equals(sender.trim(), ignoreCase = true)
}
