package com.example.forestry.ui.previews

import com.example.forestry.data.models.ThemeMode
import kotlinx.coroutines.flow.Flow

interface DataStoreRepositoryLike {
    val themeMode: Flow<ThemeMode>

    suspend fun setTheme(mode: ThemeMode)
}