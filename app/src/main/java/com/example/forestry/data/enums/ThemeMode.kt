package com.example.forestry.data.enums

enum class ThemeMode {
    SYSTEM, LIGHT, DARK;

    fun getDisplayName(): String = when (this) {
        SYSTEM -> "Système"
        LIGHT -> "Clair"
        DARK -> "Sombre"
    }
}