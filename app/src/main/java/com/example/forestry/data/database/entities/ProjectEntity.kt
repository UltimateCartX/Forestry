package com.example.forestry.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.forestry.data.api.bodies.ProjectBody
import com.example.forestry.data.models.Project
import org.osmdroid.util.GeoPoint
import java.util.UUID

@Entity(tableName = "projects")
class ProjectEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val points: List<GeoPoint>
) {
    fun toModel(ownerName: String): Project {
        return Project(id, name, points, ownerName)
    }

    fun toBody(ownerId: UUID): ProjectBody {
        val wkt = "POLYGON(${points.joinToString(", ") { "${it.longitude} ${it.latitude}" } })"
        return ProjectBody(id, name, wkt, ownerId)
    }
}
