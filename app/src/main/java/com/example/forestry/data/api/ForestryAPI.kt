package com.example.forestry.data.api

import com.example.forestry.data.models.LoginRequest
import com.example.forestry.data.models.Project
import com.example.forestry.data.models.TokenResponse
import com.example.forestry.data.models.Tree
import com.example.forestry.data.models.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


/**
 * Define Forestry API routes to be used by the application
 */
interface ForestryAPI {
    @POST("login")
    suspend fun login(@Body login: LoginRequest): TokenResponse

    @GET("@me")
    suspend fun me(
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
}