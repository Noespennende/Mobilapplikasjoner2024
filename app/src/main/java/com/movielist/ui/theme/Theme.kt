package com.movielist.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider


@Composable
fun ApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {LocalColor provides darkColorScheme } else { LocalColor provides lightColorScheme }
    val textFieldColors = if (darkTheme) {
        LocalTextFieldColors provides InputFieldColors()
    }
    else {
        LocalTextFieldColors provides InputFieldColors(textFieldColorsLightTheme())}

    CompositionLocalProvider(
        colorScheme,
        textFieldColors
    ) {
        MaterialTheme(
            typography = Typography,
            content = content
        )
    }


}