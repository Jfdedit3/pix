package com.jfdedit3.pix.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PixColors = darkColorScheme()

@Composable
fun PixTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PixColors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
