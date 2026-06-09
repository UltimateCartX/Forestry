package com.example.forestry.data.models

import org.osmdroid.util.GeoPoint
import java.util.UUID

data class Project(
    val id: UUID,
    val name: String,
    val points: List<GeoPoint>
)
