package com.example.forestry.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.forestry.data.database.entities.ProjectEntity
import com.example.forestry.data.database.entities.TreeEntity
import com.example.forestry.data.database.mapper.Converters

@Database(
    entities = [ProjectEntity::class, TreeEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ForestryDatabase : RoomDatabase() {
    abstract fun forestryDao(): ForestryDao

    companion object {
        private const val DATABASE_NAME = "forestry_database"

        @Volatile
        private var INSTANCE: ForestryDatabase? = null

        fun getDatabase(context: Context): ForestryDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ForestryDatabase::class.java,
                    DATABASE_NAME
                )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
