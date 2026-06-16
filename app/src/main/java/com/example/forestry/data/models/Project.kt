package com.example.forestry.data.models

import com.example.forestry.data.api.bodies.ProjectBody
import com.example.forestry.data.database.entities.ProjectEntity
import org.osmdroid.util.GeoPoint
import java.util.UUID

data class Project(
    val id: UUID,
    val name: String,
    val points: List<GeoPoint>,
    val ownerName: String
) {
    fun toEntity(): ProjectEntity {
        return ProjectEntity(id, name, points)
    }

    fun toBody(ownerId: UUID): ProjectBody {
        val wkt = "POLYGON(${points.joinToString(", ") { "${it.longitude} ${it.latitude}" } })"
        return ProjectBody(id, name, wkt, ownerId)
    }
}
