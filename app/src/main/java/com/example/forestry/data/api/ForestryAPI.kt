package com.example.forestry.data.api

import com.example.forestry.data.api.bodies.ProjectBody
import com.example.forestry.data.api.bodies.TreeBody
import com.example.forestry.data.api.bodies.UserBody
import com.example.forestry.data.api.requests.LoginRequest
import com.example.forestry.data.api.responses.TokenResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface ForestryAPI {
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): TokenResponse

    @GET("@me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): UserBody

    @GET("user/{id}")
    suspend fun getUser(
        @Header("Authorization") token: String,
        @Path("id") id: UUID
    ): UserBody

    @GET("trees")
    suspend fun getTrees(
        @Header("Authorization") token: String
    ): List<TreeBody>

    @GET("projects")
    suspend fun getProjects(
        @Header("Authorization") token: String
    ): List<ProjectBody>

    @POST("projects")
    suspend fun addProject(
        @Header("Authorization") token: String,
        @Body project: ProjectBody
    )

    @POST("trees")
    suspend fun addTree(
        @Header("Authorization") token: String,
        @Body tree: TreeBody
    )
}