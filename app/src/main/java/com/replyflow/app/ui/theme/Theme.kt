package com.replyflow.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Bg = Color(0xFF313338)
val Nav = Color(0xFF1E1F22)
val Surface = Color(0xFF2B2D31)
val SurfaceHigh = Color(0xFF383A40)
val Line = Color(0xFF3F4147)
val Text = Color(0xFFF2F3F5)
val Muted = Color(0xFFB5BAC1)
val Blurple = Color(0xFF5865F2)
val Green = Color(0xFF23A559)
val Red = Color(0xFFF23F42)
val Amber = Color(0xFFF0B232)
val Violet = Color(0xFF9B84EE)

private val Colors = darkColorScheme(
    primary = Blurple, onPrimary = Color.White, background = Bg, onBackground = Text,
    surface = Surface, onSurface = Text, surfaceVariant = SurfaceHigh, onSurfaceVariant = Muted,
    outline = Line, error = Red
)

@Composable fun ReplyFlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colors, typography = androidx.compose.material3.Typography(), content = content)
}
