package com.example.forestry.viewmodel

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forestry.data.api.requests.LoginRequest
import com.example.forestry.data.enums.BluetoothConnectionState
import com.example.forestry.data.enums.GNSSState
import com.example.forestry.data.enums.ThemeMode
import com.example.forestry.data.enums.TreeClass
import com.example.forestry.data.models.Project
import com.example.forestry.data.models.Tree
import com.example.forestry.data.repositories.DataStoreRepository
import com.example.forestry.data.repositories.ForestryRepository
import com.example.forestry.data.repositories.GNSSRepository
import com.example.forestry.ui.navigation.NavEvent
import com.example.forestry.ui.navigation.Screen
import com.example.forestry.utils.DegreeConverter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.util.UUID

class ForestryViewModel(
    private val forestryRepository: ForestryRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val gnssRepository: GNSSRepository
): ViewModel() {
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

    private val _onlineMode = MutableStateFlow(false)
    val onlineMode: StateFlow<Boolean> = _onlineMode

    fun onLoginClick() {
        viewModelScope.launch {
            try {
                val response = forestryRepository.login(LoginRequest(_emailText.value, _passwordText.value))
                _token.value = response.token
                _onlineMode.value = true
                forestryRepository.syncData(response.token)
                navigateTo(Screen.MAP, true)
            } catch (e: Exception) {
                e.message?.let { Log.e("Forestry", it) }
            }
        }
    }

    fun onOfflineClick() {
        _onlineMode.value = false
        navigateTo(Screen.MAP, true)
    }

    fun logoff() {
        _token.value = ""
        _onlineMode.value = false
    }

    fun getMe() {
        viewModelScope.launch {
            try {
                val response = forestryRepository.getMe(_token.value)
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
            _trees.value = forestryRepository.getTrees(_token.value)
        }
    }

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects

    fun loadProjects() {
        viewModelScope.launch {
            _projects.value = forestryRepository.getProjects(_token.value)
        }
    }

    fun addProject(project: Project) {
        viewModelScope.launch {
            forestryRepository.addProject(_token.value, project)
        }
        loadProjects()
    }

    fun addTree(tree: Tree) {
        viewModelScope.launch {
            forestryRepository.addTree(_token.value, tree)
        }
    }

    // Map control

    private val _mapPos = MutableStateFlow(GeoPoint(48.294025, 4.01190814))
    val mapCenter: StateFlow<GeoPoint> = _mapPos
    fun setMapPos(pos: GeoPoint) {
        _mapPos.value = pos
    }

    private val _mapZoom = MutableStateFlow(17.0)
    val mapZoom: StateFlow<Double> = _mapZoom
    fun setMapZoom(zoom: Double) {
        _mapZoom.value = zoom
    }

    fun onGnssMarkerClick() {
            setNewTreeLat(_gnssPos.value.latitude)
            setNewTreeLon(_gnssPos.value.longitude)
            navigateTo(Screen.NEWTREE)
    }

    // GNSS

    val gnssConnectionState: StateFlow<BluetoothConnectionState> = gnssRepository.connectionState

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

    val gnssData = gnssRepository.incomingData
    init {
        viewModelScope.launch {
            gnssData.collect { data ->
                val startIndex = data.indexOf("\$GNGGA")
                if (startIndex != -1) {
                    try {
                        val startData = data.substring(startIndex + 1)
                        val endIndex = startData.indexOf("$")
                        val gngga = if (endIndex != -1) startData.substring(0, endIndex) else startData

                        val gnggaValues = gngga.split(",")

                        if (gnggaValues.size > 4 && gnggaValues[2].isNotEmpty() && gnggaValues[4].isNotEmpty()) {
                            val lat = DegreeConverter.toDecimalDegree(gnggaValues[2])
                            val lon = DegreeConverter.toDecimalDegree(gnggaValues[4])

                            setGnssPos(lat, lon)
                            setGnssState(GNSSState.WORKING)
                        } else {
                            setGnssState(GNSSState.ERROR)
                        }
                    } catch (e: Exception) {
                        Log.e("Forestry", "Parsing error: ${e.message}")
                        setGnssState(GNSSState.ERROR)
                    }
                }
            }
        }
    }

    fun onBluetoothDeviceClick(device: BluetoothDevice) {
        viewModelScope.launch {
            gnssRepository.connect(device)
        }
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
    fun onProjectNameChange(newText: String) {
        _newProjectName.value = newText
    }

    private val _projectPerimeterPoints = MutableStateFlow<List<GeoPoint>>(emptyList())
    val projectPerimeterPoints = _projectPerimeterPoints.asStateFlow()
    fun addProjectPerimeterPoint(point: GeoPoint) {
        _projectPerimeterPoints.update { currentList -> currentList + point }
    }
    fun removeLastProjectPerimeterPoint() {
        _projectPerimeterPoints.update { currentList ->
            if (currentList.isNotEmpty()) currentList.dropLast(1) else currentList
        }
    }

    fun openProjectDelimiter() {
        _projectCreationMode.value = true
        navigateTo(Screen.MAP)
    }

    fun onConfirmProjectCreationClick() {
        _projectCreationMode.value = false
        val newProject = Project(UUID.randomUUID(), _newProjectName.value, _projectPerimeterPoints.value.toList(), "Forestier Hors Ligne")
        addProject(newProject)
        setCurrentProject(newProject)
        _newProjectName.value = ""
        _projectPerimeterPoints.value = emptyList()
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
        val newTree = Tree(
            id = UUID.randomUUID(),
            latitude = _newTreeLat.value,
            longitude = _newTreeLon.value,
            essence = _newTreeEssence.value,
            diameter = _newTreeDiameter.value.toDouble(),
            height = _newTreeHeight.value.toDouble(),
            treeClass = _newTreeClass.value,
            state = _newTreeState.value,
            projectId = _currentProject.value!!.id
        )
        addTree(newTree)
        _newTreeEssence.value = ""
        _newTreeDiameter.value = ""
        _newTreeHeight.value = ""
        _newTreeClass.value = TreeClass.SMALL
        _newTreeState.value = ""
    }
}