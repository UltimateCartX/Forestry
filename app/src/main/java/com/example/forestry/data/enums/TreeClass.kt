package com.example.forestry.data.enums

enum class TreeClass {
    SMALL, MEDIUM, BIG;

    fun getDisplayName(): String = when (this) {
        SMALL -> "small"
        MEDIUM -> "medium"
        BIG -> "big"
    }

    companion object {
        fun getTreeClass(name: String): TreeClass = when (name) {
            "small" -> SMALL
            "medium" -> MEDIUM
            "big" -> BIG
            else -> throw IllegalArgumentException("Unknown tree class: $name")
        }
    }
}