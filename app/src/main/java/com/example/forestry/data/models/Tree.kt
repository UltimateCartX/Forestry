package com.example.forestry.data.models

import java.util.UUID

data class Tree(
    val id: UUID,
    val latitude: Double,
    val longitude: Double,
    val essence: String,
    val diameter: Double,
    val height: Double,
    val cclass: TreeClass,
    val state: String,
    val projectId: UUID
)
