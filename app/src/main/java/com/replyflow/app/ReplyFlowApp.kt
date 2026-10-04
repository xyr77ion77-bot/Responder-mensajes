package com.replyflow.app

import android.app.Application
import com.replyflow.app.data.AiConfigRepository
import com.replyflow.app.data.RuleRepository
import com.replyflow.app.data.SettingsRepository

class ReplyFlowApp : Application() {
    val rules by lazy { RuleRepository(this) }
    val aiConfig by lazy { AiConfigRepository(this) }
    val settings by lazy { SettingsRepository(this) }
}
