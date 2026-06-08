package com.example.forestry.ui.navigation

sealed class NavEvent {
    data class Navigate(
        val route: String,
        val popBackStack: Boolean = false
    ): NavEvent()
    data object NavigateBack: NavEvent()
}