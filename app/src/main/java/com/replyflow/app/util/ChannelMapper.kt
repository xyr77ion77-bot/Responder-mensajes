package com.replyflow.app.util

object ChannelMapper {
    private val exact = mapOf(
        "com.google.android.apps.messaging" to "sms",
        "com.android.messaging" to "sms",
        "com.samsung.android.messaging" to "sms",
        "com.whatsapp" to "wa",
        "com.whatsapp.w4b" to "business",
        "com.facebook.orca" to "messenger",
        "com.instagram.android" to "instagram",
        "org.telegram.messenger" to "telegram",
        "org.thunderdog.challegram" to "telegram",
        "com.linkedin.android" to "linkedin"
    )
    fun fromPackage(packageName: String): String? = exact[packageName]
        ?: if (packageName.contains("chat", ignoreCase = true)) "chat" else null
}
