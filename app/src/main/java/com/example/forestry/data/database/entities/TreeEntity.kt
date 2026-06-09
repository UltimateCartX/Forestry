package com.example.forestry.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.forestry.data.enums.TreeClass
import java.util.UUID

@Entity(
    tableName = "trees",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId")])
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
