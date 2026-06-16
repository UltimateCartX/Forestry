package com.example.forestry.data.repositories

import android.util.Log
import com.example.forestry.data.api.ForestryAPI
import com.example.forestry.data.api.requests.LoginRequest
import com.example.forestry.data.api.responses.TokenResponse
import com.example.forestry.data.database.ForestryDao
import com.example.forestry.data.models.Project
import com.example.forestry.data.models.Tree
import com.example.forestry.data.models.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Main repository for the application. Handles tree, project and user data.
 *
 * In online mode, the app will store and fetch data from the API.
 * In offline mode, the app will store and fetch data locally.
 * Once the user logs on, a synchronization process will be triggered, which will upload all the data to the API, and delete it locally.
 */
class ForestryRepository(
    private val api: ForestryAPI,
    private val dao: ForestryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    companion object {
        private const val TAG = "ForestryRepository"
    }

    private fun ensureBearer(token: String) =
        if (token.startsWith("Bearer ")) token else "Bearer $token"

    private suspend fun <T> executeWithOfflineSupport(
        remoteCall: suspend () -> T,
        localCall: suspend () -> T,
        fallbackMessage: String
    ): T = withContext(ioDispatcher) {
        try {
            remoteCall()
        } catch (e: Exception) {
            Log.w(TAG, fallbackMessage, e)
            localCall()
        }
    }

    suspend fun login(login: LoginRequest): TokenResponse = withContext(ioDispatcher) {
        api.login(login)
    }

    suspend fun getMe(token: String): User = withContext(ioDispatcher) {
        api.getCurrentUser(ensureBearer(token)).toModel()
    }

    suspend fun getUser(token: String, id: UUID): User = withContext(ioDispatcher) {
        api.getUser(ensureBearer(token), id).toModel()
    }

    suspend fun getTrees(token: String): List<Tree> = executeWithOfflineSupport(
        remoteCall = { api.getTrees(ensureBearer(token)).map { it.toModel() } },
        localCall = { dao.getAllTrees().map { it.toModel() } },
        fallbackMessage = "Offline: Tree fetched locally."
    )

    suspend fun getProjects(token: String): List<Project> = executeWithOfflineSupport(
        remoteCall = {
            api.getProjects(ensureBearer(token)).map {
                val owner = api.getUser(ensureBearer(token), it.ownerId)
                it.toModel(owner.firstname + " " + owner.lastname)
            }
        },
        localCall = { dao.getAllProjects().map { it.toModel("Forestier Hors Ligne") } },
        fallbackMessage = "Offline: Project fetched locally."
    )

    suspend fun addProject(token: String, project: Project) = executeWithOfflineSupport(
        remoteCall = {
            api.addProject(
                ensureBearer(token),
                project.toBody(api.getCurrentUser(ensureBearer(token)).id)
            )
        },
        localCall = { dao.insertProject(project.toEntity()) },
        fallbackMessage = "Offline: Project saved locally for later synchronization."
    )

    suspend fun addTree(token: String, tree: Tree) = executeWithOfflineSupport(
        remoteCall = { api.addTree(ensureBearer(token), tree.toBody()) },
        localCall = { dao.insertTree(tree.toEntity()) },
        fallbackMessage = "Offline: Tree saved locally for later synchronization."
    )

    suspend fun syncData(token: String) = withContext(ioDispatcher) {
        val bearerToken = ensureBearer(token)
        val currentUserId = api.getCurrentUser(ensureBearer(token)).id

        dao.getAllProjects().forEach { entity ->
            try {
                api.addProject(bearerToken, entity.toBody(currentUserId))
                dao.deleteProject(entity)
                Log.d(TAG, "Synced project: ${entity.name}")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to sync project ${entity.name}", e)
            }
        }

        dao.getAllTrees().forEach { entity ->
            try {
                api.addTree(bearerToken, entity.toBody())
                dao.deleteTree(entity)
                Log.d(TAG, "Synced tree: ${entity.id}")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to sync tree ${entity.id}", e)
            }
        }
    }
}
