package com.example.forestry.ui.previews

import com.example.forestry.data.models.LoginRequest
import com.example.forestry.data.models.Project
import com.example.forestry.data.models.TokenResponse
import com.example.forestry.data.models.Tree
import com.example.forestry.data.models.User

interface APIRepositoryLike {
    suspend fun login(login: LoginRequest): TokenResponse
    suspend fun getMe(token: String): User
    suspend fun getTrees(token: String): List<Tree>
    suspend fun getProjects(token: String): List<Project>
    suspend fun addProject(token: String, project: Project)
    suspend fun addTree(token: String, tree: Tree)
}