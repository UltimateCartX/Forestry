package com.example.forestry.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.osmdroid.util.GeoPoint
import java.util.UUID

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val points: List<GeoPoint>
)
