package com.example.forestry.ui.navigation

sealed class Screen(val route: String) {
    data object MAP: Screen("map")
    data object LOGIN : Screen("login")
    data object CONFIGURATION : Screen("configuration")
    data object PROJECTS : Screen("projects")
    data object PREFERENCES : Screen("preferences")
    data object NEWTREE : Screen("newTree")
}