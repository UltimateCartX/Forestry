package com.example.forestry.data.repositories

import com.example.forestry.data.datastore.DataStoreManager
import com.example.forestry.data.enums.ThemeMode

class DataStoreRepository(private val dataStoreManager: DataStoreManager) {

    val themeMode = dataStoreManager.themeModeFlow
    suspend fun setTheme(mode: ThemeMode) {
        dataStoreManager.setThemeMode(mode)
    }
}