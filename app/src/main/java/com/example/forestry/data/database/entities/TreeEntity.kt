package com.example.forestry.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.forestry.data.api.bodies.TreeBody
import com.example.forestry.data.enums.TreeClass
import com.example.forestry.data.models.Tree
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
    val treeClass: TreeClass,
    val state: String,
    val projectId: UUID
) {
    fun toModel(): Tree {
        return Tree(id, latitude, longitude, essence, diameter, height, treeClass, state, projectId)
    }

    fun toBody(): TreeBody {
        return TreeBody(id, latitude, longitude, essence, diameter, height, treeClass.getDisplayName(), state, projectId)
    }
}
