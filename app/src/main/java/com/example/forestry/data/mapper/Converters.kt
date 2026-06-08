package com.example.forestry.data.mapper

import androidx.room.TypeConverter
import com.example.forestry.data.models.TreeClass
import org.osmdroid.util.GeoPoint
import java.util.UUID

class Converters {

    @TypeConverter
    fun fromUuid(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUuid(uuid: String?): UUID? {
        return uuid?.let { UUID.fromString(it) }
    }

    @TypeConverter
    fun fromPlot(points: List<GeoPoint>?): String? {
        return points?.let { "POLYGON(${points.joinToString(", ") { "${it.longitude} ${it.latitude}" } }" }
    }

    @TypeConverter
    fun toPlot(wkt: String?): List<GeoPoint>? {
        return wkt?.let { wkt.removePrefix("POLYGON(").removeSuffix(")").split(",").map { val (lon, lat) = it.trim().split(" "); GeoPoint(lat.toDouble(), lon.toDouble()) } }
    }

    @TypeConverter
    fun fromTreeClass(treeClass: TreeClass?): String? {
        return treeClass?.toString
    }

    @TypeConverter
    fun toTreeClass(name: String?): TreeClass? {
        return name?.let { when(name) {
            "small" -> TreeClass.SMALL
            "medium" -> TreeClass.MEDIUM
            "big" -> TreeClass.BIG
            else -> null
        } }
    }
}