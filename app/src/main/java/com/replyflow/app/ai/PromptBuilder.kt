package com.replyflow.app.ai

import com.replyflow.app.data.AiConfig
import com.replyflow.app.data.IncomingMessage
import com.replyflow.app.data.Rule

object PromptBuilder {
    fun system(config: AiConfig, rule: Rule): String = buildString {
        append(config.prompt.trim())
        append("\nEstás respondiendo mediante la regla: ")
        append(rule.name)
        append(". No inventes datos que no estén en el mensaje o las instrucciones.")
    }

    fun user(incoming: IncomingMessage): String = "${incoming.sender}: ${incoming.text}"
}
