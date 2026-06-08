package com.example.forestry.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.forestry.data.mapper.Converters
import com.example.forestry.data.dao.ForestryDao
import com.example.forestry.data.entities.ProjectEntity
import com.example.forestry.data.entities.TreeEntity

@Database(
    entities = [ProjectEntity::class, TreeEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ForestryDatabase : RoomDatabase() {
    abstract fun forestryDao(): ForestryDao
}