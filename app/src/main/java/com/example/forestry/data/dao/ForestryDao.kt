package com.example.forestry.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.forestry.data.entities.ProjectEntity
import com.example.forestry.data.entities.TreeEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ForestryDao {

    @Query("SELECT * FROM projects")
    suspend fun getAllProjects(): List<ProjectEntity>

    @Insert
    suspend fun insertProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: UUID): ProjectEntity?

    @Query("SELECT * FROM trees")
    suspend fun getAllTrees(): List<TreeEntity>

    @Insert
    suspend fun insertTree(tree: TreeEntity)

    @Delete
    suspend fun deleteTree(tree: TreeEntity)
}