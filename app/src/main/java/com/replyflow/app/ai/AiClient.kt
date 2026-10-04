package com.replyflow.app.ai

import com.replyflow.app.data.AiConfig
import com.replyflow.app.data.IncomingMessage
import com.replyflow.app.data.Rule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class AiClient {
    suspend fun complete(config: AiConfig, rule: Rule, incoming: IncomingMessage): String = withContext(Dispatchers.IO) {
        if (config.provider.equals("google", true)) gemini(config, rule, incoming)
        else openAiCompatible(config, rule, incoming)
    }

    private fun openAiCompatible(config: AiConfig, rule: Rule, incoming: IncomingMessage): String {
        val endpoint = if (config.provider.equals("openrouter", true))
            config.url.trimEnd('/') + "/chat/completions" else config.url
        val body = JSONObject().apply {
            put("model", config.model)
            put("messages", JSONArray().put(JSONObject().put("role", "system").put("content", PromptBuilder.system(config, rule)))
                .put(JSONObject().put("role", "user").put("content", PromptBuilder.user(incoming))))
        }
        val response = post(endpoint, body, mapOf("Authorization" to "Bearer ${config.apiKey}"))
        return JSONObject(response).getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content").trim()
    }

    private fun gemini(config: AiConfig, rule: Rule, incoming: IncomingMessage): String {
        val endpoint = config.url.trimEnd('/') + "/v1beta/models/${config.model}:generateContent?key=${config.apiKey}"
        val text = PromptBuilder.system(config, rule) + "\n\nMensaje: " + PromptBuilder.user(incoming)
        val body = JSONObject().put("contents", JSONArray().put(JSONObject().put("parts", JSONArray().put(JSONObject().put("text", text)))))
        val response = post(endpoint, body, emptyMap())
        return JSONObject(response).getJSONArray("candidates").getJSONObject(0).getJSONObject("content")
            .getJSONArray("parts").getJSONObject(0).getString("text").trim()
    }

    private fun post(endpoint: String, body: JSONObject, headers: Map<String, String>): String {
        val connection = URL(endpoint).openConnection() as HttpURLConnection
        return try {
            connection.requestMethod = "POST"
            connection.connectTimeout = 8_000; connection.readTimeout = 8_000
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            headers.forEach(connection::setRequestProperty)
            connection.outputStream.use { it.write(body.toString().toByteArray()) }
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val response = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (code !in 200..299) error("HTTP $code: ${response.take(180)}")
            response
        } finally { connection.disconnect() }
    }
}
