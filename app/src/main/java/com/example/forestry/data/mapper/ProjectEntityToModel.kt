package com.example.forestry.data.mapper

import com.example.forestry.data.entities.ProjectEntity
import com.example.forestry.data.models.Project

fun ProjectEntity.toModel() = Project(
    id = id,
    name = name,
    points = points,
)