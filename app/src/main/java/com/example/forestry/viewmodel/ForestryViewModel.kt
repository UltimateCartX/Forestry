package com.example.forestry.viewmodel

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forestry.data.models.BluetoothConnectionState
import com.example.forestry.data.models.GNSSState
import com.example.forestry.data.models.LoginRequest
import com.example.forestry.data.models.Project
import com.example.forestry.data.models.ThemeMode
import com.example.forestry.data.models.TokenResponse
import com.example.forestry.data.models.Tree
import com.example.forestry.data.models.TreeClass
import com.example.forestry.data.models.User
import com.example.forestry.data.repositories.APIRepository
import com.example.forestry.data.repositories.DataStoreRepository
import com.example.forestry.data.repositories.GNSSRepository
import com.example.forestry.ui.navigation.NavEvent
import com.example.forestry.ui.navigation.Screen
import com.example.forestry.ui.previews.APIRepositoryLike
import com.example.forestry.ui.previews.DataStoreRepositoryLike
import com.example.forestry.ui.previews.GNSSRepositoryLike
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.util.UUID

/**
    The main ViewModel of the application

    Handles communication between the UI layer and the data layer
 */
open class ForestryViewModel(context: Context? = null): ViewModel() {

    private val apiRepository = when (context) {
        is Context -> APIRepository(context)
        else -> object : APIRepositoryLike {
            override suspend fun login(login: LoginRequest): TokenResponse { return TokenResponse("") }
            override suspend fun getMe(token: String): User { return User("", "John", "Doe", "john.doe@example.com", "Standard") }
            override suspend fun getTrees(token: String): List<Tree> { return emptyList() }
            override suspend fun getProjects(token: String): List<Project> { return emptyList() }
            override suspend fun addProject(token: String, project: Project) {}
            override suspend fun addTree(token: String, tree: Tree) {}
        }
    }

    private val dataStoreRepository : DataStoreRepositoryLike = when (context) {
        is Context -> DataStoreRepository(context)
        else -> object : DataStoreRepositoryLike {
            override val themeMode: Flow<ThemeMode> = flow { emit(ThemeMode.SYSTEM) }
            override suspend fun setTheme(mode: ThemeMode) {}
        }
    }

    private val gnssRepository : GNSSRepositoryLike = when (context) {
        is Context -> GNSSRepository(context)
        else -> object : GNSSRepositoryLike {
            override val connectionState: StateFlow<BluetoothConnectionState> = MutableStateFlow<BluetoothConnectionState>(BluetoothConnectionState.Disconnected).asStateFlow()
            override val incomingData: StateFlow<String?> = MutableStateFlow<String?>(null).asStateFlow()
            override fun getBondedDevices(): List<BluetoothDevice> { return emptyList() }
            override fun connect(device: BluetoothDevice) {}
            override fun disconnect() {}
        }
    }

    // Preferences

    val themeMode = dataStoreRepository.themeMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch {
            dataStoreRepository.setTheme(mode)
        }
    }

    // Navigation

    private val _navEvents = MutableSharedFlow<NavEvent>()
    val navEvents = _navEvents.asSharedFlow()

    fun navigateTo(screen: Screen, popBackStack: Boolean = false) {
        viewModelScope.launch {
            _navEvents.emit(NavEvent.Navigate(screen.route, popBackStack))
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _navEvents.emit(NavEvent.NavigateBack)
        }
    }

    // Login

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _emailText = MutableStateFlow("")
    val emailText: StateFlow<String> = _emailText

    fun onEmailTextChange(newText: String) {
        _emailText.value = newText
    }

    private val _passwordText = MutableStateFlow("")
    val passwordText: StateFlow<String> = _passwordText

    fun onPasswordTextChange(newText: String) {
        _passwordText.value = newText
    }

    private val _token = MutableStateFlow("")

    private val _online = MutableStateFlow(false)
    val online: StateFlow<Boolean> = _online

    fun testAPI() {
        viewModelScope.launch {
            try {
                apiRepository.login(LoginRequest("a", "a"))
                _online.value = true
            } catch (e: Exception) {
                e.message?.let { Log.e("Forestry", it) }
                _online.value = false
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            try {
                val response = apiRepository.login(LoginRequest(_emailText.value, _passwordText.value))
                Log.d("Forestry", response.toString())
                _token.value = response.access_token
                _isConnected.value = true
                navigateTo(Screen.MAP, true)
            } catch (e: Exception) {
                e.message?.let { Log.e("Forestry", it) }
            }
        }
    }

    fun logoff() {
        _token.value = ""
        _isConnected.value = false
    }

    fun getMe() {
        viewModelScope.launch {
            try {
                val response = apiRepository.getMe("Bearer " + _token.value)
                Log.d("Forestry", response.toString())
            } catch (e: Exception) {
                e.message?.let { Log.e("Forestry", it) }
            }
        }
    }

    private val _trees = MutableStateFlow<List<Tree>>(emptyList())
    val trees: StateFlow<List<Tree>> = _trees

    fun loadTrees() {
        viewModelScope.launch {
            _trees.value = apiRepository.getTrees("Bearer " + _token.value)
        }
    }

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects

    fun loadProjects() {
        viewModelScope.launch {
            _projects.value = apiRepository.getProjects("Bearer " + _token.value)
        }
    }

    fun addProject(project: Project) {
        viewModelScope.launch {
            apiRepository.addProject("Bearer " + _token.value, project)
        }
        loadProjects()
    }

    fun addTree(tree: Tree) {
        viewModelScope.launch {
            apiRepository.addTree("Bearer " + _token.value, tree)
        }
    }

    // GNSS

    open val gnssConnectionState: StateFlow<BluetoothConnectionState> = gnssRepository.connectionState

    private val _gnssState = MutableStateFlow<GNSSState>(GNSSState.DISCONNECTED)
    val gnssState: StateFlow<GNSSState> = _gnssState
    fun setGnssState(state: GNSSState) {
        _gnssState.value = state
    }

    private val _gnssPos = MutableStateFlow(GeoPoint(0.0, 0.0))
    val gnssPos: StateFlow<GeoPoint> = _gnssPos
    fun setGnssPos(lat: Double, lon: Double) {
        _gnssPos.value = GeoPoint(lat, lon)
    }

    private val _bondedDevices = MutableStateFlow<List<BluetoothDevice>>(gnssRepository.getBondedDevices())
    val bondedDevices: StateFlow<List<BluetoothDevice>> = _bondedDevices.asStateFlow()
    fun refreshBondedDevices() {
        _bondedDevices.value = gnssRepository.getBondedDevices()
    }

    val incomingData: StateFlow<String?> = gnssRepository.incomingData

    fun connect(device: BluetoothDevice) {
        gnssRepository.connect(device)
    }

    override fun onCleared() {
        gnssRepository.disconnect()
    }

    // Project

    private val _currentProject = MutableStateFlow<Project?>(null)
    val currentProject: StateFlow<Project?> = _currentProject

    fun setCurrentProject(project: Project) {
        _currentProject.value = project
    }

    private val _projectCreationMode = MutableStateFlow(false)
    val projectCreationMode: StateFlow<Boolean> = _projectCreationMode
    fun setProjectCreationMode(mode: Boolean) {
        _projectCreationMode.value = mode
    }

    private val _newProjectName = MutableStateFlow("")
    val newProjectName: StateFlow<String> = _newProjectName
    fun setNewProjectName(newText: String) {
        _newProjectName.value = newText
    }

    private val _projectDelimiterPoints = MutableStateFlow(mutableListOf<GeoPoint>())
    val projectDelimiterPoints: StateFlow<MutableList<GeoPoint>> = _projectDelimiterPoints

    fun openProjectDelimiter() {
        _projectCreationMode.value = true
        navigateTo(Screen.MAP)
    }

    fun closeProjectDelimiter() {
        _projectCreationMode.value = false
        val newProject = Project(UUID.randomUUID(), _newProjectName.value, _projectDelimiterPoints.value.toList())
        addProject(newProject)
        setCurrentProject(newProject)
        _newProjectName.value = ""
        _projectDelimiterPoints.value.clear()
        navigateTo(Screen.CONFIGURATION)
    }

    // New tree

    private val _newTreeLat = MutableStateFlow(0.0)
    val newTreeLat: StateFlow<Double> = _newTreeLat
    fun setNewTreeLat(lat: Double) {
        _newTreeLat.value = lat
    }

    private val _newTreeLon = MutableStateFlow(0.0)
    val newTreeLon: StateFlow<Double> = _newTreeLon
    fun setNewTreeLon(lon: Double) {
        _newTreeLon.value = lon
    }

    private val _newTreeEssence = MutableStateFlow("")
    val newTreeEssence: StateFlow<String> = _newTreeEssence
    fun setNewTreeEssence(essence: String) {
        _newTreeEssence.value = essence
    }

    private val _newTreeDiameter = MutableStateFlow("")
    val newTreeDiameter: StateFlow<String> = _newTreeDiameter
    fun setNewTreeDiameter(diameter: String) {
        _newTreeDiameter.value = diameter
    }

    private val _newTreeHeight = MutableStateFlow("")
    val newTreeHeight: StateFlow<String> = _newTreeHeight
    fun setNewTreeHeight(height: String) {
        _newTreeHeight.value = height
    }

    private val _newTreeState = MutableStateFlow("")
    val newTreeState: StateFlow<String> = _newTreeState
    fun setNewTreeState(state: String) {
        _newTreeState.value = state
    }

    private val _newTreeClass = MutableStateFlow<TreeClass>(TreeClass.SMALL)
    val newTreeClass: StateFlow<TreeClass> = _newTreeClass
    fun setNewTreeClass(treeClass: TreeClass) {
        _newTreeClass.value = treeClass
    }

    fun createNewTree() {
        val newTree = Tree(UUID.randomUUID(), _newTreeLat.value, _newTreeLon.value, _newTreeEssence.value, _newTreeDiameter.value.toDouble(), _newTreeHeight.value.toDouble(), _newTreeClass.value, _newTreeState.value, _currentProject.value!!.id)
        addTree(newTree)
        _newTreeEssence.value = ""
        _newTreeDiameter.value = ""
        _newTreeHeight.value = ""
        _newTreeClass.value = TreeClass.SMALL
        _newTreeState.value = ""
    }
}