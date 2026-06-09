package com.example.forestry.data.database.mapper

import com.example.forestry.data.database.entities.ProjectEntity
import com.example.forestry.data.models.Project

fun ProjectEntity.toModel() = Project(
    id = id,
    name = name,
    points = points,
)