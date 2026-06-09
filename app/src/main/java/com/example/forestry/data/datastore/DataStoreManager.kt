package com.example.forestry.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.forestry.data.enums.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {
    
    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val value = prefs[PreferencesKeys.THEME_MODE]
            ThemeMode.entries.find { it.name == value } ?: ThemeMode.SYSTEM
        }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[PreferencesKeys.THEME_MODE] = mode.name }
    }
}