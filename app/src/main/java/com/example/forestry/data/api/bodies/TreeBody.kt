package com.example.forestry.data.api.bodies

import com.example.forestry.data.enums.TreeClass
import com.example.forestry.data.models.Tree
import com.google.gson.annotations.SerializedName
import java.util.UUID

class TreeBody(
    val id: UUID,
    val latitude: Double,
    val longitude: Double,
    val essence: String,
    val diameter: Double,
    val height: Double,
    @SerializedName("class")
    val treeClass: String,
    val state: String,
    @SerializedName("project_id")
    val projectId: UUID
) {
    fun toModel(): Tree {
        return Tree(id, latitude, longitude, essence, diameter, height, TreeClass.getTreeClass(treeClass), state, projectId)
    }
}