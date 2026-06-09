package com.example.forestry.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.forestry.R
import com.example.forestry.data.enums.GNSSState
import com.example.forestry.data.models.Project
import com.example.forestry.data.models.Tree
import com.example.forestry.ui.composables.StatusBanner
import com.example.forestry.ui.composables.StatusType
import com.example.forestry.ui.navigation.Screen
import com.example.forestry.ui.previews.PreviewLightDarkCombo
import com.example.forestry.ui.theme.ForestryTheme
import com.example.forestry.utils.scaledDrawable
import com.example.forestry.viewmodel.ForestryViewModel
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

@Composable
fun MapScreen(
    viewModel: ForestryViewModel,
    modifier: Modifier = Modifier
) {
    val isOnline by viewModel.online.collectAsState()
    val currentProject by viewModel.currentProject.collectAsState()
    val gnssPos by viewModel.gnssPos.collectAsState()
    val gnssState by viewModel.gnssState.collectAsState()
    val isCreatingProject by viewModel.projectCreationMode.collectAsState()
    val projectPerimeterPoints by viewModel.projectPerimeterPoints.collectAsState()
    val mapCenter by viewModel.mapCenter.collectAsState()
    val mapZoom by viewModel.mapZoom.collectAsState()
    val trees by viewModel.trees.collectAsState()
    val projects by viewModel.projects.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProjects()
        viewModel.loadTrees()
    }

    MapScreenContent(
        isOnline = isOnline,
        currentProject = currentProject,
        gnssPos = gnssPos,
        gnssState = gnssState,
        isCreatingProject = isCreatingProject,
        projectPerimeterPoints = projectPerimeterPoints,
        mapCenter = mapCenter,
        mapZoom = mapZoom,
        trees = trees,
        projects = projects,
        onFabClick = { viewModel.navigateTo(Screen.CONFIGURATION) },
        onConfirmProjectCreationClick = viewModel::onConfirmProjectCreationClick,
        setMapPos = viewModel::setMapPos,
        setMapZoom = viewModel::setMapZoom,
        onGnssMarkerClick = viewModel::onGnssMarkerClick,
        addProjectPerimeterPoint = viewModel::addProjectPerimeterPoint,
        removeLastProjectPerimeterPoint = viewModel::removeLastProjectPerimeterPoint,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreenContent(
    isOnline: Boolean,
    currentProject: Project?,
    gnssPos: GeoPoint,
    gnssState: GNSSState,
    isCreatingProject: Boolean,
    projectPerimeterPoints: List<GeoPoint>,
    mapCenter: GeoPoint,
    mapZoom: Double,
    trees: List<Tree>,
    projects: List<Project>,
    onFabClick: () -> Unit,
    onConfirmProjectCreationClick: () -> Unit,
    setMapPos: (GeoPoint) -> Unit,
    setMapZoom: (Double) -> Unit,
    onGnssMarkerClick: () -> Unit,
    addProjectPerimeterPoint: (GeoPoint) -> Unit,
    removeLastProjectPerimeterPoint: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val mapView = remember {
        MapView(context)
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    title = {
                        Text("Forestry")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                if (!isOnline) { StatusBanner("Hors-Ligne", StatusType.WARNING) }
                when (gnssState) {
                    GNSSState.DISCONNECTED -> StatusBanner("Le récepteur GNSS n'est pas connecté", StatusType.ERROR)
                    GNSSState.ERROR -> StatusBanner("Le récepteur GNSS ne capte pas les satellites", StatusType.ERROR)
                    GNSSState.WORKING -> {}
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (isCreatingProject) {
                Button(
                    onClick = onConfirmProjectCreationClick,
                    modifier = Modifier
                ) {
                    Text("Valider le projet")
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = { mapView.controller.animateTo(gnssPos) },
                        shape = FloatingActionButtonDefaults.shape,
                        colors = buttonColors(
                            containerColor = FloatingActionButtonDefaults.containerColor,
                            contentColor = contentColorFor(FloatingActionButtonDefaults.containerColor),
                        ),
                        contentPadding = PaddingValues(0.dp),
                        enabled = gnssState == GNSSState.WORKING,
                        modifier = Modifier
                            .padding(16.dp)
                            .height(56.dp)
                            .width(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Centrez vers le récepteur GNSS"
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    FloatingActionButton(
                        onClick = onFabClick,
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forest,
                            contentDescription = null
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) {}

            LaunchedEffect(Unit) {
                launcher.launch(permission)
            }
        } else {
            Map(
                mapView = mapView,
                currentProject = currentProject,
                gnssPos = gnssPos,
                gnssState = gnssState,
                isCreatingProject = isCreatingProject,
                projectPerimeterPoints = projectPerimeterPoints,
                mapCenter = mapCenter,
                mapZoom = mapZoom,
                trees = trees,
                projects = projects,
                setMapPos = setMapPos,
                setMapZoom = setMapZoom,
                onGnssMarkerClick = onGnssMarkerClick,
                addProjectPerimeterPoint = addProjectPerimeterPoint,
                removeLastProjectPerimeterPoint = removeLastProjectPerimeterPoint,
                modifier = modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun Map(
    mapView: MapView,
    currentProject: Project?,
    gnssPos: GeoPoint,
    gnssState: GNSSState,
    isCreatingProject: Boolean,
    projectPerimeterPoints: List<GeoPoint>,
    mapCenter: GeoPoint,
    mapZoom: Double,
    trees: List<Tree>,
    projects: List<Project>,
    setMapPos: (GeoPoint) -> Unit,
    setMapZoom: (Double) -> Unit,
    onGnssMarkerClick: () -> Unit,
    addProjectPerimeterPoint: (GeoPoint) -> Unit,
    removeLastProjectPerimeterPoint: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier
            .clipToBounds(),
        factory = { mapView.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(mapZoom)
            controller.setCenter(mapCenter)

            addMapListener(object : MapListener {
                override fun onScroll(event: ScrollEvent?): Boolean {
                    event?.source?.mapCenter?.let {
                        setMapPos(GeoPoint(it.latitude, it.longitude))
                    }
                    return true
                }

                override fun onZoom(event: ZoomEvent?): Boolean {
                    event?.zoomLevel?.let {
                        setMapZoom(it)
                    }
                    return true
                }
            })
        } },
        update = { view ->
            view.overlays.clear()

            projects.forEach { project ->
                val polygon = Polygon().apply {
                    title = project.name
                    points = project.points
                    fillPaint.color = if (currentProject?.id == project.id) 0x3300AA00 else 0x33005500
                    outlinePaint.color = if (currentProject?.id == project.id) 0xFF00FF00.toInt() else 0xFF008800.toInt()
                    outlinePaint.strokeWidth = 5f
                }
                view.overlays.add(polygon)
            }

            trees.forEach { tree ->
                val marker = Marker(view).apply {
                    title = "Arbre\nEssence: ${tree.essence}\nDiamètre: ${tree.diameter}cm\nHauteur: ${tree.height}m\nClasse: ${tree.cclass}\nEtat: ${tree.state}"
                    icon = ContextCompat.getDrawable(context, R.drawable.icon_tree_hollow)
                    position = GeoPoint(tree.latitude, tree.longitude)
                }
                view.overlays.add(marker)
            }



            if (gnssState == GNSSState.WORKING) {
                val gnssMarker = Marker(view).apply {
                    title = "Récepteur GNSS"
                    icon = scaledDrawable(context, R.drawable.icon_add_tree, 40)
                    position = gnssPos

                    setOnMarkerClickListener { _, _ ->
                        onGnssMarkerClick()
                        true
                    }
                }
                view.overlays.add(gnssMarker)
            }

            if (isCreatingProject) {
                val creationPolygon = Polygon().apply {
                    fillPaint.color = 0x330000FF
                    outlinePaint.color = 0xFFFF00FF.toInt()
                    outlinePaint.strokeWidth = 5f
                    if (projectPerimeterPoints.isNotEmpty()) {
                        points = projectPerimeterPoints + projectPerimeterPoints.first()
                    }
                }
                view.overlays.add(creationPolygon)

                val mapEventsReceiver = object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        p?.let { addProjectPerimeterPoint(it) }
                        return true
                    }
                    override fun longPressHelper(p: GeoPoint?): Boolean {
                        removeLastProjectPerimeterPoint()
                        return true
                    }
                }
                view.overlays.add(MapEventsOverlay(mapEventsReceiver))
            }

            view.invalidate()
        }
    )
}

@PreviewLightDarkCombo
@Composable
fun TreeScreenContentPreview1(modifier: Modifier = Modifier) {
    ForestryTheme {
        MapScreenContent(
            isOnline = true,
            currentProject = null,
            gnssPos = GeoPoint(48.294025, 4.01190814),
            gnssState = GNSSState.WORKING,
            isCreatingProject = false,
            projectPerimeterPoints = emptyList(),
            mapCenter = GeoPoint(48.294025, 4.01190814),
            mapZoom = 17.0,
            trees = emptyList(),
            projects = emptyList(),
            onFabClick = {},
            onConfirmProjectCreationClick = {},
            setMapPos = {},
            setMapZoom = {},
            onGnssMarkerClick = {},
            addProjectPerimeterPoint = {},
            removeLastProjectPerimeterPoint = {},
            modifier = modifier
        )
    }
}

@PreviewLightDarkCombo
@Composable
fun TreeScreenContentPreview2(modifier: Modifier = Modifier) {
    ForestryTheme {
        MapScreenContent(
            isOnline = true,
            currentProject = null,
            gnssPos = GeoPoint(48.294025, 4.01190814),
            gnssState = GNSSState.ERROR,
            isCreatingProject = false,
            projectPerimeterPoints = emptyList(),
            mapCenter = GeoPoint(48.294025, 4.01190814),
            mapZoom = 17.0,
            trees = emptyList(),
            projects = emptyList(),
            onFabClick = {},
            onConfirmProjectCreationClick = {},
            setMapPos = {},
            setMapZoom = {},
            onGnssMarkerClick = {},
            addProjectPerimeterPoint = {},
            removeLastProjectPerimeterPoint = {},
            modifier = modifier
        )
    }
}

@PreviewLightDarkCombo
@Composable
fun TreeScreenContentPreview3(modifier: Modifier = Modifier) {
    ForestryTheme {
        MapScreenContent(
            isOnline = true,
            currentProject = null,
            gnssPos = GeoPoint(48.294025, 4.01190814),
            gnssState = GNSSState.DISCONNECTED,
            isCreatingProject = false,
            projectPerimeterPoints = emptyList(),
            mapCenter = GeoPoint(48.294025, 4.01190814),
            mapZoom = 17.0,
            trees = emptyList(),
            projects = emptyList(),
            onFabClick = {},
            onConfirmProjectCreationClick = {},
            setMapPos = {},
            setMapZoom = {},
            onGnssMarkerClick = {},
            addProjectPerimeterPoint = {},
            removeLastProjectPerimeterPoint = {},
            modifier = modifier
        )
    }
}

@PreviewLightDarkCombo
@Composable
fun TreeScreenContentPreview4(modifier: Modifier = Modifier) {
    ForestryTheme {
        MapScreenContent(
            isOnline = false,
            currentProject = null,
            gnssPos = GeoPoint(48.294025, 4.01190814),
            gnssState = GNSSState.WORKING,
            isCreatingProject = false,
            projectPerimeterPoints = emptyList(),
            mapCenter = GeoPoint(48.294025, 4.01190814),
            mapZoom = 17.0,
            trees = emptyList(),
            projects = emptyList(),
            onFabClick = {},
            onConfirmProjectCreationClick = {},
            setMapPos = {},
            setMapZoom = {},
            onGnssMarkerClick = {},
            addProjectPerimeterPoint = {},
            removeLastProjectPerimeterPoint = {},
            modifier = modifier
        )
    }
}

@PreviewLightDarkCombo
@Composable
fun TreeScreenContentPreview5(modifier: Modifier = Modifier) {
    ForestryTheme {
        MapScreenContent(
            isOnline = false,
            currentProject = null,
            gnssPos = GeoPoint(48.294025, 4.01190814),
            gnssState = GNSSState.DISCONNECTED,
            isCreatingProject = false,
            projectPerimeterPoints = emptyList(),
            mapCenter = GeoPoint(48.294025, 4.01190814),
            mapZoom = 17.0,
            trees = emptyList(),
            projects = emptyList(),
            onFabClick = {},
            onConfirmProjectCreationClick = {},
            setMapPos = {},
            setMapZoom = {},
            onGnssMarkerClick = {},
            addProjectPerimeterPoint = {},
            removeLastProjectPerimeterPoint = {},
            modifier = modifier
        )
    }
}

@PreviewLightDarkCombo
@Composable
fun TreeScreenContentPreview6(modifier: Modifier = Modifier) {
    ForestryTheme {
        MapScreenContent(
            isOnline = true,
            currentProject = null,
            gnssPos = GeoPoint(48.294025, 4.01190814),
            gnssState = GNSSState.WORKING,
            isCreatingProject = true,
            projectPerimeterPoints = emptyList(),
            mapCenter = GeoPoint(48.294025, 4.01190814),
            mapZoom = 17.0,
            trees = emptyList(),
            projects = emptyList(),
            onFabClick = {},
            onConfirmProjectCreationClick = {},
            setMapPos = {},
            setMapZoom = {},
            onGnssMarkerClick = {},
            addProjectPerimeterPoint = {},
            removeLastProjectPerimeterPoint = {},
            modifier = modifier
        )
    }
}