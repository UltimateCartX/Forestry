package com.example.forestry.data.models

sealed class TreeClass(val toString: String) {
    data object SMALL: TreeClass("small")
    data object MEDIUM: TreeClass("medium")
    data object BIG: TreeClass("big")
}