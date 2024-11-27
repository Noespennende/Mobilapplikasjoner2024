package com.movielist.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.movielist.model.ColorThemes
import com.movielist.data.LocalStorageKeys
import com.movielist.data.createDataStore
import kotlinx.coroutines.flow.map

@Composable
fun isAppInPortraitMode(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}

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
    portraitMode: Boolean = isAppInPortraitMode(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {LocalColor provides darkColorScheme } else { LocalColor provides lightColorScheme }
    val textFieldColors = if (darkTheme) {
        LocalTextFieldColors provides InputFieldColors()
    }    else {
        LocalTextFieldColors provides InputFieldColors(textFieldColorsLightTheme())}


    val constraints = if(portraitMode) { LocalConstraints provides portraitConstraints} else {LocalConstraints provides landscapeConstraints}



    CompositionLocalProvider(
        colorScheme,
        textFieldColors,
        constraints
    ) {
        MaterialTheme(
            typography = Typography,
            content = content
        )
    }
}