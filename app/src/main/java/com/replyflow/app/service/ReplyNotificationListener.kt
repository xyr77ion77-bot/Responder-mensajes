package com.replyflow.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.RemoteInput
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import com.replyflow.app.ReplyFlowApp
import com.replyflow.app.ai.AiClient
import com.replyflow.app.data.IncomingMessage
import com.replyflow.app.util.ChannelMapper
import com.replyflow.app.util.DuplicateCache
import com.replyflow.app.util.EventLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.ConcurrentHashMap

class ReplyNotificationListener : NotificationListenerService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val engine = AutoReplyEngine()
    private val aiClient = AiClient()
    private val duplicates = DuplicateCache()
    private val lastReplyByPackage = ConcurrentHashMap<String, Long>()
    private lateinit var app: ReplyFlowApp
    private lateinit var logger: EventLogger

    override fun onCreate() {
        super.onCreate()
        app = application as ReplyFlowApp
        logger = EventLogger(this)
        if (app.settings.settings.value.strictMode) runCatching { startStrictForeground() }
            .onFailure { logger.append("error_modo_estricto", it.message.orEmpty()) }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        logger.append("listener", "Conectado")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notification = sbn.notification
        if (notification.flags and Notification.FLAG_GROUP_SUMMARY != 0) return
        val channel = ChannelMapper.fromPackage(sbn.packageName) ?: return
        val extras = notification.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()?.trim().orEmpty()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()?.trim().orEmpty()
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()?.trim().orEmpty()
        if (text.isBlank()) return
        val sender = title.ifBlank { subText.ifBlank { "Desconocido" } }
        val minute = sbn.postTime / 60_000L
        val duplicateKey = 31 * (31 * sbn.packageName.hashCode() + sender.hashCode()) + 31 * text.hashCode() + minute.hashCode()
        if (app.settings.settings.value.avoidDuplicates && duplicates.seen(duplicateKey)) return

        scope.launch { process(sbn.packageName, channel, sender, text, notification) }
    }

    private suspend fun process(packageName: String, channel: String, sender: String, text: String, notification: Notification) {
        val settings = app.settings.settings.value
        if (!settings.serviceEnabled) return
        val now = System.currentTimeMillis()
        val last = lastReplyByPackage[packageName] ?: 0L
        if (now - last < 2_000L) return
        val incoming = IncomingMessage(packageName, channel, sender, text, now)
        val match = engine.evaluate(app.rules.rules.value, incoming) ?: return
        val action = findReplyAction(notification) ?: run {
            logger.append("sin_remote_input", "$packageName | $sender")
            return
        }
        val config = app.aiConfig.config.value
        val reply = if (match.rule.aiEnabled && config != null) {
            withTimeoutOrNull(6_000L) { runCatching { aiClient.complete(config, match.rule, incoming) }.getOrNull() }
                ?.takeIf { it.isNotBlank() } ?: match.fallbackReply
        } else match.fallbackReply
        if (settings.replyDelayMs > 0) delay(settings.replyDelayMs)
        if (sendRemoteInput(action, reply)) {
            lastReplyByPackage[packageName] = System.currentTimeMillis()
            app.rules.incrementSent(match.rule.id)
            logger.append("enviado", "${match.rule.name} | $packageName | $sender")
        }
    }

    private fun findReplyAction(notification: Notification): Notification.Action? {
        val candidates = notification.actions.orEmpty().filter { it.remoteInputs?.any { input -> input.resultKey.isNotBlank() } == true }
        return candidates.firstOrNull { action ->
            val title = action.title?.toString().orEmpty()
            title.contains("responder", true) || title.contains("reply", true) || action.semanticAction == Notification.Action.SEMANTIC_ACTION_REPLY
        } ?: candidates.firstOrNull()
    }

    private fun sendRemoteInput(action: Notification.Action, reply: String): Boolean = try {
        val inputs = action.remoteInputs.orEmpty()
        if (inputs.isEmpty()) return false
        val intent = Intent().addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        val results = Bundle().apply { inputs.forEach { putCharSequence(it.resultKey, reply) } }
        RemoteInput.addResultsToIntent(inputs, intent, results)
        action.actionIntent.send(this, 0, intent)
        true
    } catch (error: PendingIntent.CanceledException) {
        logger.append("error_envio", error.message.orEmpty())
        false
    }

    private fun startStrictForeground() {
        val manager = getSystemService(NotificationManager::class.java)
        val id = "replyflow_strict"
        manager.createNotificationChannel(NotificationChannel(id, "Servicio estricto", NotificationManager.IMPORTANCE_MIN))
        val notification = NotificationCompat.Builder(this, id)
            .setSmallIcon(com.replyflow.app.R.drawable.ic_replyflow)
            .setContentTitle("ReplyFlow activo")
            .setContentText("Escuchando mensajes en modo estricto")
            .setOngoing(true).setSilent(true).build()
        startForeground(41, notification)
    }

    override fun onDestroy() {
        scope.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    companion object {
        fun requestReconnect(context: android.content.Context) {
            requestRebind(ComponentName(context, ReplyNotificationListener::class.java))
        }
    }
}
