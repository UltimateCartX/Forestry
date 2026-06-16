package com.example.forestry.data.api.bodies

import com.example.forestry.data.models.Project
import com.google.gson.annotations.SerializedName
import org.osmdroid.util.GeoPoint
import java.util.UUID

class ProjectBody(
    val id: UUID,
    val name: String,
    val plot: String,
    @SerializedName("owner_id")
    val ownerId: UUID
) {
    fun toModel(ownerName: String): Project {
        val wkt = plot
        val geoPoints = wkt.let {
            wkt.removePrefix("POLYGON(").removeSuffix(")").split(",").map {
                val (lon, lat) = it.trim().split(" "); GeoPoint(
                lat.toDouble(),
                lon.toDouble()
            )
            }
        }
        return Project(id, name, geoPoints, ownerName)
    }
}
