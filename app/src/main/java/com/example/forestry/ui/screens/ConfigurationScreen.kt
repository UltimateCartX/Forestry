package com.example.forestry.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import com.example.forestry.R
import com.example.forestry.data.enums.BluetoothConnectionState
import com.example.forestry.data.models.Project
import com.example.forestry.ui.composables.DeviceItem
import com.example.forestry.ui.composables.ForestryScaffold
import com.example.forestry.ui.composables.ProjectItem
import com.example.forestry.ui.composables.SettingsColumn
import com.example.forestry.ui.composables.SettingsItem
import com.example.forestry.ui.navigation.Screen
import com.example.forestry.ui.previews.PreviewLightDarkCombo
import com.example.forestry.ui.theme.ForestryTheme
import com.example.forestry.viewmodel.ForestryViewModel
import java.util.UUID

@Composable
fun ConfigurationScreen(
    viewModel: ForestryViewModel,
    modifier: Modifier = Modifier
) {
    val gnssConnectionState by viewModel.gnssConnectionState.collectAsState()
    val onlineMode by viewModel.isConnected.collectAsState()
    val currentProject by viewModel.currentProject.collectAsState()
    val bondedDevices by viewModel.bondedDevices.collectAsState()
    val newProjectName by viewModel.newProjectName.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshBondedDevices()
        viewModel.setProjectCreationMode(false)
    }

    ConfigurationContent(
        gnssConnectionState = gnssConnectionState,
        onlineMode = onlineMode,
        currentProject = currentProject,
        bondedDevices = bondedDevices,
        newProjectName = newProjectName,
        onNavigateBack = viewModel::navigateBack,
        onProjectClick = { viewModel.navigateTo(Screen.PROJECTS) },
        onPreferencesClick = { viewModel.navigateTo(Screen.PREFERENCES) },
        onLoginClick = { viewModel.logoff(); viewModel.navigateTo(Screen.LOGIN, true) },
        onBluetoothDeviceClick = viewModel::onBluetoothDeviceClick,
        onProjectNameChange = viewModel::onProjectNameChange,
        onCreateNewProject = viewModel::openProjectDelimiter,
        modifier = modifier,
    )
}

@Composable
fun ConfigurationContent(
    gnssConnectionState: BluetoothConnectionState,
    onlineMode: Boolean,
    currentProject: Project?,
    bondedDevices: List<BluetoothDevice>,
    newProjectName: String,
    onNavigateBack: () -> Unit,
    onProjectClick: () -> Unit,
    onPreferencesClick: () -> Unit,
    onLoginClick: () -> Unit,
    onBluetoothDeviceClick: (BluetoothDevice) -> Unit,
    onProjectNameChange: (String) -> Unit,
    onCreateNewProject: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showBondedDevicesDialog by remember { mutableStateOf(false) }
    var showProjectCreationDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { _ ->
            showBondedDevicesDialog = true
        }
    )

    ForestryScaffold(
        title = "Configuration",
        onNavigateBack = onNavigateBack,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Button(
                onClick = { launcher.launch(Manifest.permission.BLUETOOTH_CONNECT) },
                shape = RectangleShape,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = when (gnssConnectionState) {
                        BluetoothConnectionState.CONNECTED -> "GNSS Connecté"
                        BluetoothConnectionState.CONNECTING -> "Connexion en cours..."
                        BluetoothConnectionState.ERROR -> "Erreur de connexion"
                        else -> "Connecter le récepteur GNSS"
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
            SettingsColumn {
                Column {
                    Text(
                        text = "Projet en cours",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = 16.dp, start = 16.dp, bottom = 4.dp)
                    )
                    if (currentProject != null) {
                        ProjectItem(
                            name = currentProject.name,
                            author = "Forestier Hors Ligne",
                            onClick = onProjectClick,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        ProjectItem(
                            name = "Aucun projet séléctionné",
                            author = "Appuyer pour séléctionner un projet sur lequel travailler ou créer un nouveau projet",
                            onClick = onProjectClick,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                HorizontalDivider(thickness = 2.dp)
                SettingsItem(
                    Icons.Filled.Add,
                    "Créer un nouveau projet",
                    { showProjectCreationDialog = true },
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            SettingsColumn {
                SettingsItem(
                    icon = Icons.Filled.Settings,
                    text = "Préférences",
                    onClick = onPreferencesClick,
                )
                HorizontalDivider(thickness = 2.dp)
                SettingsItem(
                    icon = Icons.Filled.AccountCircle,
                    text = if (onlineMode) "Se déconnecter" else "Se connecter",
                    onClick = onLoginClick
                )
            }


        }
        if (showBondedDevicesDialog) {
            BondedDevicesDialog(
                gnssConnectionState = gnssConnectionState,
                bondedDevices = bondedDevices,
                onDeviceClick = onBluetoothDeviceClick,
                onDismissRequest = { showBondedDevicesDialog = false },
            )
        }
        if (showProjectCreationDialog) {
            ProjectCreationFormDialog(
                newProjectName = newProjectName,
                onProjectNameChange = onProjectNameChange,
                onCreateNewProject = onCreateNewProject,
                onDismissRequest = { showProjectCreationDialog = false },
            )
        }
    }
}

@Composable
fun BondedDevicesDialog(
    gnssConnectionState: BluetoothConnectionState,
    bondedDevices: List<BluetoothDevice>,
    onDeviceClick: (BluetoothDevice) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Choisissez le récepteur GNSS",
                    style = MaterialTheme.typography.headlineSmall,
                )

                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Text(
                        text = "Vous devez autoriser l'application à se connecter au Bluetooth pour connecter le récepteur GNSS",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = android.net.Uri.fromParts(
                                        "package",
                                        context.packageName,
                                        null
                                    )
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ouvrir les paramètres de permissions")
                    }
                } else if (bondedDevices.isEmpty()) {
                    Text(
                        text = "Aucun appareil appairé trouvé, vous devez au préalable appairez le récepteur GNSS à votre appareil",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ouvrir les paramètres Bluetooth")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(max = 300.dp)
                            .padding(bottom = 8.dp)
                    ) {
                        items(
                            items = bondedDevices,
                            key = { device -> device.address }
                        ) { device ->
                            DeviceItem(
                                name = device.name ?: "Appareil inconnu",
                                enabled = when (gnssConnectionState) {
                                    BluetoothConnectionState.CONNECTING -> false
                                    else -> true
                                },
                                onClick = { onDeviceClick(device) }
                            )
                        }
                    }
                    if (gnssConnectionState == BluetoothConnectionState.CONNECTING) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                    Text(
                        text = when (gnssConnectionState) {
                            BluetoothConnectionState.CONNECTING -> "Connexion..."
                            BluetoothConnectionState.CONNECTED -> "Le récepteur GNSS est connecté!"
                            BluetoothConnectionState.ERROR -> "Une erreur est survenue"
                            BluetoothConnectionState.DISCONNECTED -> "Si vous ne trouvez pas le récepteur GNSS, vérifier que vous l'avez au préalable appairez à votre appareil"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                            context.startActivity(intent)
                        },
                        enabled = when (gnssConnectionState) {
                            BluetoothConnectionState.CONNECTING -> false
                            else -> true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ouvrir les paramètres Bluetooth")
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectCreationFormDialog(
    newProjectName: String,
    onProjectNameChange: (String) -> Unit,
    onCreateNewProject: () -> Unit,
    onDismissRequest: () -> Unit,
) {

    Dialog(onDismissRequest = onDismissRequest) {
        Card {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Créer un projet",
                    style = MaterialTheme.typography.headlineSmall,
                )
                TextField(
                    value = newProjectName,
                    onValueChange = onProjectNameChange,
                    placeholder = {
                        Text("Nom du projet")
                    }
                )
                Button(
                    onClick = onCreateNewProject,
                    enabled = newProjectName.isNotBlank(),
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
@PreviewLightDarkCombo
@Composable
fun SettingsScreenPreview() {
    ForestryTheme {
        ConfigurationContent(
            gnssConnectionState = BluetoothConnectionState.DISCONNECTED,
            onlineMode = false,
            currentProject = Project(UUID.randomUUID(), "Projet A", emptyList()),
            bondedDevices = emptyList(),
            newProjectName = "",
            onNavigateBack = {},
            onProjectClick = {},
            onPreferencesClick = {},
            onLoginClick = {},
            onBluetoothDeviceClick = {},
            onProjectNameChange = {},
            onCreateNewProject = {}
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@PreviewLightDarkCombo
@Composable
fun BondedDevicesDialogPreview() {
    ForestryTheme {
        BondedDevicesDialog(
            gnssConnectionState = BluetoothConnectionState.CONNECTING,
            bondedDevices = emptyList(),
            onDeviceClick = {},
            onDismissRequest = {}
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@PreviewLightDarkCombo
@Composable
fun ProjectCreationFormDialogPreview() {
    ForestryTheme {
        ProjectCreationFormDialog(
            newProjectName = "",
            onProjectNameChange = {},
            onCreateNewProject = {},
            onDismissRequest = {}
        )
    }
}