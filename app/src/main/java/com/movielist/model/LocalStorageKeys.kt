package com.movielist.model

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey

data class LocalStorageKeys(
    val colorTheme: Preferences.Key<String> = stringPreferencesKey("colorTheme")
)
