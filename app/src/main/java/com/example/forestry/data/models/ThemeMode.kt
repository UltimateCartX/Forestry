package com.example.forestry.data.models

import android.content.Context

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

fun ThemeMode.getDisplayName(): String = when (this) {
    ThemeMode.SYSTEM -> "Système"
    ThemeMode.LIGHT -> "Clair"
    ThemeMode.DARK -> "Sombre"
}