package com.example.forestry.data.database.mapper

import com.example.forestry.data.database.entities.TreeEntity
import com.example.forestry.data.models.Tree

fun Tree.toEntity() = TreeEntity(
    id = id,
    latitude = latitude,
    longitude = longitude,
    essence = essence,
    diameter = diameter,
    height = height,
    cclass = cclass,
    state = state,
    projectId = projectId
)