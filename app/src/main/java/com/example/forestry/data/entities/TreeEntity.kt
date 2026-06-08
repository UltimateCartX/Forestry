package com.example.forestry.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.forestry.data.models.TreeClass
import org.osmdroid.util.GeoPoint
import java.util.UUID

@Entity(tableName = "trees")
data class TreeEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val latitude: Double,
    val longitude: Double,
    val essence: String,
    val diameter: Double,
    val height: Double,
    val cclass: TreeClass,
    val state: String,
    val projectId: UUID
)
