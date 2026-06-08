package com.example.forestry.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.forestry.R
import com.example.forestry.data.models.BluetoothConnectionState
import com.example.forestry.ui.navigation.Screen
import com.example.forestry.ui.previews.FakeForestryViewModel
import com.example.forestry.ui.previews.PreviewLightDarkCombo
import com.example.forestry.ui.theme.ForestryTheme
import com.example.forestry.viewmodel.ForestryViewModel

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(
    viewModel: ForestryViewModel,
    modifier: Modifier = Modifier,
) {
    val gnssConnectionState by viewModel.gnssConnectionState.collectAsState()
    val onlineMode by viewModel.isConnected.collectAsState()
    val currentProject by viewModel.currentProject.collectAsState()
    var openBondedDevicesDialog by remember { mutableStateOf(false) }
    var projectCreationFormDialogOpened by remember { mutableStateOf(false) }

    viewModel.setProjectCreationMode(false)

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(
                        onClick = viewModel::navigateBack
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = {
                    Text("Configuration")
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding)
        ) {
            Button(
                onClick = { openBondedDevicesDialog = true },
                shape = RectangleShape,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = when (gnssConnectionState) {
                        is BluetoothConnectionState.Connecting -> "Connecter le récepteur GNSS"
                        is BluetoothConnectionState.Connected -> "Changer le récepteur GNSS"
                        is BluetoothConnectionState.Disconnected -> "Connecter le récepteur GNSS"
                        is BluetoothConnectionState.Error -> "Connecter le récepteur GNSS"
                    },
                    modifier = Modifier.padding(start = 24.dp)
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    painterResource(R.drawable.icon_gnss),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                )
            }
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                        MaterialTheme.shapes.medium
                    )
            ) {
                Column {
                    Text(
                        text = "Projet en cours",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = 16.dp, start = 16.dp, bottom = 4.dp)
                    )
                    Button(
                        onClick = { viewModel.navigateTo(Screen.PROJECTS) },
                        shape = RectangleShape,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonColors(
                            Color.Transparent,
                            MaterialTheme.colorScheme.onSurface,
                            MaterialTheme.colorScheme.error,
                            MaterialTheme.colorScheme.onError
                        ),
                    ) {
                        if (currentProject !== null) {
                            ProjectItem(currentProject!!.name, "Forestier Hors Ligne")
                        } else {
                            ProjectItem(
                                "Aucun projet séléctionné",
                                "Appuyer pour séléctionner un projet sur lequel travailler ou créer un nouveau projet"
                            )
                        }
                    }
                }
                HorizontalDivider(thickness = 2.dp)
                SettingsItem(
                    Icons.Filled.Add,
                    "Créer un nouveau projet",
                    { projectCreationFormDialogOpened = true },
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                        MaterialTheme.shapes.medium
                    )
            ) {
                SettingsItem(
                    Icons.Filled.Settings,
                    "Préférences",
                    onClick = { viewModel.navigateTo(Screen.PREFERENCES) },
                )
                HorizontalDivider(thickness = 2.dp)
                SettingsItem(
                    Icons.Filled.AccountCircle,
                    if (onlineMode) "Se déconnecter" else "Se connecter",
                    onClick = { viewModel.logoff(); viewModel.navigateTo(Screen.LOGIN, true) },
                )
            }


        }
        if (openBondedDevicesDialog) {
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { granted -> Log.d("Forestry", "BLUETOOTH_CONNECT granted? $granted") }
            )
            LaunchedEffect(Unit) {
                launcher.launch(Manifest.permission.BLUETOOTH_CONNECT)
            }
            BondedDevicesDialog(
                viewModel = viewModel,
                onDismissRequest = { openBondedDevicesDialog = false },
            )
        }
        if (projectCreationFormDialogOpened) {
            ProjectCreationFormDialog(
                viewModel = viewModel,
                onDismissRequest = { projectCreationFormDialogOpened = false },
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    underText: String? = null,
    rightText: String? = null,
) {
    SettingsItem(
        rememberVectorPainter(icon),
        text,
        onClick,
        modifier,
        underText,
        rightText
    )
}

@Composable
fun SettingsItem(
    icon: Painter,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    underText: String? = null,
    rightText: String? = null,
) {
    Button(
        onClick = onClick,
        shape = RectangleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonColors(
            Color.Transparent,
            MaterialTheme.colorScheme.onSurface,
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.onError
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.padding(start = 4.dp, end = 8.dp)
            )
            Column {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall
                )
                underText?.apply {
                    Text(
                        text = underText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

@Composable
fun BondedDevicesDialog(
    viewModel: ForestryViewModel,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val gnssConnectionState by viewModel.gnssConnectionState.collectAsState()
    val bondedDevices = viewModel.getBondedDevices()

    Dialog(onDismissRequest = onDismissRequest) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Choisissez le récepteur GNSS",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                if (bondedDevices.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        try {
                            items(bondedDevices) { device ->
                                if (device.name !== null) {
                                    TextButton(
                                        onClick = { viewModel.connect(device) },
                                        enabled = when (gnssConnectionState) {
                                            is BluetoothConnectionState.Connecting -> false
                                            else -> true
                                        }
                                    ) {
                                        Text(text = device.name)
                                    }
                                }
                            }
                        } catch (e: SecurityException) {
                            Log.w("Forestry", "Unable to retrieve device names")
                        }
                    }
                    if (gnssConnectionState == BluetoothConnectionState.Disconnected) {
                        Text(
                            text = "Si vous ne trouvez pas le récepteur GNSS, vérifier que vous l'avez au préalable appairez à votre appareil",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    if (gnssConnectionState == BluetoothConnectionState.Connecting) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 8.dp)
                        )
                    }
                    if (gnssConnectionState != BluetoothConnectionState.Disconnected) {
                        Text(
                            text = when (gnssConnectionState) {
                                is BluetoothConnectionState.Connecting -> "Connexion..."
                                is BluetoothConnectionState.Connected -> "Le récepteur GNSS est connecté!"
                                is BluetoothConnectionState.Error -> "Une erreur est survenue"
                                is BluetoothConnectionState.Disconnected -> ""
                            },
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                } else {
                    Text(
                        text = "Aucun appareil appairé trouvé. Vérifier que vous avez bien autorisez l'application à se connecter au Bluetooth et que vous avez au préalable appairer le récepteur GNSS",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectCreationFormDialog(
    viewModel: ForestryViewModel,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val projectCreationName by viewModel.projectCreationName.collectAsState()

    Dialog(onDismissRequest = onDismissRequest) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Créer un projet")
                TextField(
                    projectCreationName,
                    viewModel::onProjectCreationNameChange,
                    placeholder = {
                        Text("Nom du projet")
                    }
                )
                Button(
                    viewModel::openProjectDelimiter,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("Délimiter la zone")
                }
            }
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@RequiresApi(Build.VERSION_CODES.S)
@PreviewLightDarkCombo
@Composable
fun SettingsScreenPreview(modifier: Modifier = Modifier) {
    ForestryTheme {
        ConfigurationScreen(FakeForestryViewModel(), modifier)
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@PreviewLightDarkCombo
@Composable
fun BondedDevicesDialogPreview(modifier: Modifier = Modifier) {
    ForestryTheme {
        BondedDevicesDialog(FakeForestryViewModel(), {}, modifier)
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@PreviewLightDarkCombo
@Composable
fun ProjectCreationFormDialogPreview(modifier: Modifier = Modifier) {
    ForestryTheme {
        ProjectCreationFormDialog(FakeForestryViewModel(), {}, modifier)
    }
}