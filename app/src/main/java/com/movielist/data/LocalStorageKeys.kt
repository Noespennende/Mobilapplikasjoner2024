package com.movielist.data

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

data class LocalStorageKeys(
    val colorTheme: Preferences.Key<String> = stringPreferencesKey("colorTheme"),
    val followerCount: Preferences.Key<Int> = intPreferencesKey("followerCount")
)
