package com.example.forestry.ui.theme

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.forestry.R
import com.example.forestry.data.models.GNSSState
import com.example.forestry.ui.navigation.Screen
import com.example.forestry.ui.previews.FakeForestryViewModel
import com.example.forestry.ui.previews.PreviewLightDarkCombo
import com.example.forestry.utils.DegreeConverter
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: ForestryViewModel,
    modifier: Modifier = Modifier
) {
    val gnssState by viewModel.gnssState.collectAsState()
    val gnssPos by viewModel.gnssPos.collectAsState()
    val projectCreationMode by viewModel.projectCreationMode.collectAsState()
    val isOnline by viewModel.online.collectAsState()

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
                when (gnssState) {
                    GNSSState.DISCONNECTED -> Text(
                        "Le récepteur GNSS n'est pas connecté",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(4.dp)
                    )
                    GNSSState.ERROR -> Text(
                        "Le récepteur GNSS ne capte pas les satellites",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(4.dp)
                    )
                    GNSSState.WORKING -> {}
                }
                if (!isOnline) {
                    Text(
                        "Mode Hors-Ligne",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(4.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (projectCreationMode) {
                Button(
                    viewModel::closeProjectDelimiter,
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
                        enabled = gnssState is GNSSState.WORKING,
                        modifier = Modifier
                            .padding(16.dp)
                            .height(56.dp)
                            .width(56.dp)
                    ) {
                        Icon(painterResource(R.drawable.icon_my_location), contentDescription = null)
                    }
                    Spacer(Modifier.weight(1f))
                    FloatingActionButton(
                        onClick = { viewModel.navigateTo(Screen.CONFIGURATION) },
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.icon_forest_filled),
                            contentDescription = null
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        var permissionGranted by remember { mutableStateOf(false) }

        if (!permissionGranted) {
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                if (granted) permissionGranted = true
            }

            LaunchedEffect(Unit) {
                launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }

        } else {
            Map(viewModel, mapView, modifier.padding(innerPadding))
        }
    }
}

@Composable
fun Map(
    viewModel: ForestryViewModel,
    mapView: MapView,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gnssData by viewModel.incomingData.collectAsState()
    val gnssPos by viewModel.gnssPos.collectAsState()
    val gnssState by viewModel.gnssState.collectAsState()
    val projectCreationMode by viewModel.projectCreationMode.collectAsState()
    val projectDelimiterPoints by viewModel.projectDelimiterPoints.collectAsState()
    var currentPos by rememberSaveable { mutableStateOf(GeoPoint(48.294025, 4.01190814)) }
    var currentZoom by rememberSaveable { mutableDoubleStateOf(17.0) }

    val trees by viewModel.trees.collectAsState()
    val projects by viewModel.projects.collectAsState()

    LaunchedEffect(gnssData) {
        if (gnssData !== null) {
            Log.d("Forestry", gnssData!!)
            val startIndex = gnssData!!.indexOf("\$GNGGA")
            if (startIndex != -1) {
                val startData = gnssData!!.substring(startIndex + 1)
                val endIndex = startData.indexOf("$")
                val gngga = startData.slice(0..endIndex)
                val gnggaValues = gngga.split(",")
                if (gnggaValues[2] != "" && gnggaValues[4] != "") {
                    val lat = DegreeConverter.toDecimalDegree(gnggaValues[2])
                    val lon = DegreeConverter.toDecimalDegree(gnggaValues[4])
                    viewModel.setGnssPos(lat, lon)
                    viewModel.setGnssState(GNSSState.WORKING)
                } else {
                    viewModel.setGnssState(GNSSState.ERROR)
                }
            }
        }
    }

    AndroidView(
        modifier = modifier
            .clipToBounds(),
        factory = { mapView.apply {
            viewModel.loadProjects()
            viewModel.loadTrees()

            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(currentZoom)
            controller.setCenter(currentPos)

            for (project in projects) {
                val polygon = Polygon().apply {
                    title = "project.name"
                    points = project.points
                    fillPaint.color = 0x003300AA00
                    outlinePaint.color = 0x00FF00FF00.toInt()
                    outlinePaint.strokeWidth = 5f
                }
                overlays.add(polygon)
            }

            for (tree in trees) {
                overlays.add(Marker(this).apply {
                    title =
                        "Arbre\nEssence: ${tree.essence}\nDiamètre: ${tree.diameter}cm\nHauteur: ${tree.height}m\nClasse: ${tree.cclass}\nEtat: ${tree.state}"
                    icon = ContextCompat.getDrawable(context, R.drawable.icon_tree_hollow)
                    position = GeoPoint(tree.latitude, tree.longitude)
                })
            }

            val gnssMarker = Marker(this).apply {
                title = "Récepteur GNSS"
                icon = scaledDrawable(context, R.drawable.icon_add_tree, 40)
                position = GeoPoint(0.0, 0.0)
                setVisible(false)

                setOnMarkerClickListener { _, _ ->
                    viewModel.setNewTreeLat(gnssPos.latitude)
                    viewModel.setNewTreeLon(gnssPos.longitude)
                    viewModel.navigateTo(Screen.NEWTREE)
                    true
                }
            }
            overlays.add(gnssMarker)
            setTag(R.id.gnssMarker, gnssMarker)

            if (projectCreationMode) {
                val polygon = Polygon().apply {
                    fillPaint.color = 0x00330000FF
                    outlinePaint.color = 0x00FF00FF.toInt()
                    outlinePaint.strokeWidth = 5f
                }

                overlays.add(polygon)

                val mapEventsReceiver = object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        p?.let {
                            projectDelimiterPoints.add(it)
                            polygon.points = projectDelimiterPoints + projectDelimiterPoints.first()
                            invalidate()
                        }
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint?): Boolean {
                        projectDelimiterPoints.removeLastOrNull()
                        polygon.points = projectDelimiterPoints + projectDelimiterPoints.first()
                        invalidate()
                        return true
                    }
                }
                overlays.add(MapEventsOverlay(mapEventsReceiver))
            }

            addMapListener(object : MapListener {
                override fun onScroll(event: ScrollEvent?): Boolean {
                    event?.source?.mapCenter?.let {
                        currentPos = GeoPoint(it.latitude, it.longitude)
                    }
                    return true
                }

                override fun onZoom(event: ZoomEvent?): Boolean {
                    event?.zoomLevel?.let {
                        currentZoom = it
                    }
                    return true
                }
            })
        } },
        update = { newMapView ->
            val gnssMarker = newMapView.getTag(R.id.gnssMarker) as Marker
            if (gnssState is GNSSState.WORKING) {
                gnssMarker.position = gnssPos
                gnssMarker.setVisible(true)
            } else {
                gnssMarker.setVisible(false)
            }
        }
    )
}

@SuppressLint("ViewModelConstructorInComposable")
@PreviewLightDarkCombo
@Composable
fun TreeScreenPreview(modifier: Modifier = Modifier) {
    ForestryTheme {
        MapScreen(FakeForestryViewModel())
    }
}