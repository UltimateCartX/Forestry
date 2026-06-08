package com.example.forestry.data.repositories

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.forestry.data.api.ForestryAPI
import com.example.forestry.data.dao.ForestryDao
import com.example.forestry.data.database.ForestryDatabase
import com.example.forestry.data.mapper.toEntity
import com.example.forestry.data.mapper.toModel
import com.example.forestry.data.models.LoginRequest
import com.example.forestry.data.models.Project
import com.example.forestry.data.models.TokenResponse
import com.example.forestry.data.models.Tree
import com.example.forestry.data.models.User
import com.example.forestry.ui.previews.APIRepositoryLike
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Handles communication with the Forestry API
 */
class APIRepository(private val context: Context) : APIRepositoryLike {
    private val dao = Room.databaseBuilder(context, ForestryDatabase::class.java, "app_db").build().forestryDao()

    private val api: ForestryAPI by lazy {
        Retrofit.Builder()
            .baseUrl("https://gnssv2.alwaysdata.net/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ForestryAPI::class.java)
    }

    override suspend fun login(login: LoginRequest): TokenResponse {
        Log.d("Forestry", login.toString())
        return api.login(login)
    }

    override suspend fun getMe(token: String): User {
        return api.me(token)
    }

    override suspend fun getTrees(token: String): List<Tree> {
        try {
            return api.getTrees(token)
        } catch (_: Exception) {
            Log.d("Forestry", "Unable to contact distant API, getting trees from local db...")
            return dao.getAllTrees().map { it.toModel() }
        }
    }

    override suspend fun getProjects(token: String): List<Project> {
        try {
            return api.getProjects(token)
        } catch (_: Exception) {
            Log.d("Forestry", "Unable to contact distant API, getting projects from local db...")
            return dao.getAllProjects().map { it.toModel() }
        }
    }

    override suspend fun addProject(token: String, project: Project) {
        try {
            throw Exception()
        } catch (_: Exception) {
            Log.d("Forestry", "Unable to contact distant API, adding project to local db...")
            dao.insertProject(project.toEntity())
        }
    }

    override suspend fun addTree(token: String, tree: Tree) {
        try {
            throw Exception()
        } catch (_: Exception) {
            Log.d("Forestry", "Unable to contact distant API, adding tree to local db...")
            dao.insertTree(tree.toEntity())
        }
    }
}