package com.movielist.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.movielist.model.ColorThemes
import com.movielist.model.LocalStorageKeys
import com.movielist.model.createDataStore
import kotlinx.coroutines.flow.map


@Composable
fun isAppInDarkTheme(): Boolean {
    val context = LocalContext.current
    val dataStore = createDataStore(context)

    val colorThemeState by dataStore.data
        .map { it[LocalStorageKeys().colorTheme] ?: ColorThemes.DARKMODE.toString() }
        .collectAsState(initial = ColorThemes.DARKMODE.toString())

    if(colorThemeState == ColorThemes.SYSTEM.toString()){
        return isSystemInDarkTheme()
    }

    return colorThemeState == ColorThemes.DARKMODE.toString()
}

@Composable
fun ApplicationTheme(
    darkTheme: Boolean = isAppInDarkTheme(),
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