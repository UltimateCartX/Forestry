package com.example.forestry.data.api

import com.example.forestry.data.api.requests.LoginRequest
import com.example.forestry.data.api.responses.TokenResponse
import com.example.forestry.data.models.Project
import com.example.forestry.data.models.Tree
import com.example.forestry.data.models.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ForestryAPI {
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): TokenResponse

    @GET("@me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): User

    @GET("trees")
    suspend fun getTrees(
        @Header("Authorization") token: String
    ): List<Tree>

    @GET("projects")
    suspend fun getProjects(
        @Header("Authorization") token: String
    ): List<Project>

    @POST("projects")
    suspend fun addProject(
        @Header("Authorization") token: String,
        @Body project: Project
    )

    @POST("trees")
    suspend fun addTree(
        @Header("Authorization") token: String,
        @Body tree: Tree
    )
}