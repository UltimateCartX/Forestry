package com.example.forestry.data.repositories

import android.content.Context
import com.example.forestry.data.datastore.DataStoreManager
import com.example.forestry.data.models.ThemeMode
import com.example.forestry.ui.previews.DataStoreRepositoryLike

class DataStoreRepository(private val context: Context) : DataStoreRepositoryLike {

    private val dataStoreManager = DataStoreManager(context)

    override val themeMode = dataStoreManager.themeModeFlow

    override suspend fun setTheme(mode: ThemeMode) {
        dataStoreManager.setThemeMode(mode)
    }
}