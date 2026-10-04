package com.replyflow.app.data

import org.json.JSONArray
import org.json.JSONObject

internal fun JSONArray.strings(): List<String> = buildList {
    for (i in 0 until length()) add(optString(i))
}

internal fun Rule.toJson() = JSONObject().apply {
    put("id", id); put("name", name); put("trigger", trigger); put("match", match.name)
    put("reply", reply); put("channels", JSONArray(channels)); put("active", active)
    put("aiEnabled", aiEnabled); put("scheduled", scheduled); put("start", start); put("end", end)
    put("allow", JSONArray(allow)); put("deny", JSONArray(deny)); put("sent", sent)
}

internal fun JSONObject.toRule() = Rule(
    id = optLong("id", System.currentTimeMillis()), name = optString("name"),
    trigger = optString("trigger"), match = runCatching { MatchType.valueOf(optString("match")) }.getOrDefault(MatchType.CONTAINS),
    reply = optString("reply"), channels = optJSONArray("channels")?.strings().orEmpty(),
    active = optBoolean("active", true), aiEnabled = optBoolean("aiEnabled"),
    scheduled = optBoolean("scheduled"), start = optString("start", "09:00"), end = optString("end", "18:00"),
    allow = optJSONArray("allow")?.strings().orEmpty(), deny = optJSONArray("deny")?.strings().orEmpty(), sent = optInt("sent")
)

internal fun AiConfig.toJson(includeKey: Boolean = false) = JSONObject().apply {
    put("provider", provider); put("platform", platform); put("url", url)
    if (includeKey) put("apiKey", apiKey)
    put("model", model); put("prompt", prompt)
}

internal fun JSONObject.toAiConfig(apiKey: String) = AiConfig(
    provider = optString("provider"), platform = optString("platform"), url = optString("url"),
    apiKey = apiKey, model = optString("model"), prompt = optString("prompt")
)

internal fun AppSettings.toJson() = JSONObject().apply {
    put("serviceEnabled", serviceEnabled); put("strictMode", strictMode)
    put("avoidDuplicates", avoidDuplicates); put("replyDelayMs", replyDelayMs); put("themeDark", themeDark)
}

internal fun JSONObject.toSettings() = AppSettings(
    serviceEnabled = optBoolean("serviceEnabled", true), strictMode = optBoolean("strictMode"),
    avoidDuplicates = optBoolean("avoidDuplicates", true), replyDelayMs = optLong("replyDelayMs"),
    themeDark = optBoolean("themeDark", true)
)
