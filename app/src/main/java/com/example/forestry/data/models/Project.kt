package com.example.forestry.data.models

import com.example.forestry.data.entities.ProjectEntity
import org.osmdroid.util.GeoPoint
import java.util.UUID

data class Project(
    val id: UUID,
    val name: String,
    val points: List<GeoPoint>
)
