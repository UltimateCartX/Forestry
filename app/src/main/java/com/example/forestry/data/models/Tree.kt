package com.example.forestry.data.models

import com.example.forestry.data.api.bodies.TreeBody
import com.example.forestry.data.database.entities.TreeEntity
import com.example.forestry.data.enums.TreeClass
import java.util.UUID

data class Tree(
    val id: UUID,
    val latitude: Double,
    val longitude: Double,
    val essence: String,
    val diameter: Double,
    val height: Double,
    val treeClass: TreeClass,
    val state: String,
    val projectId: UUID
) {
    fun toEntity(): TreeEntity {
        return TreeEntity(id, latitude, longitude, essence, diameter, height, treeClass, state, projectId)
    }

    fun toBody(): TreeBody {
        return TreeBody(id, latitude, longitude, essence, diameter, height, treeClass.getDisplayName(), state, projectId)
    }
}
