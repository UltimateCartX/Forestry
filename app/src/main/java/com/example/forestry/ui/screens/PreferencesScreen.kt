package com.example.forestry.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.forestry.data.models.ThemeMode
import com.example.forestry.ui.previews.FakeForestryViewModel
import com.example.forestry.ui.previews.PreviewLightDarkCombo
import com.example.forestry.ui.theme.ForestryTheme
import com.example.forestry.viewmodel.ForestryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    viewModel: ForestryViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    var themeModalOpened by remember { mutableStateOf(false) }

    Scaffold (
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
                    Text("Préférences")
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding)
        ) {
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
                    "Thème de l'application",
                    { themeModalOpened = true },
                    underText = when (themeMode) {
                        ThemeMode.SYSTEM -> "Système"
                        ThemeMode.LIGHT -> "Clair"
                        ThemeMode.DARK -> "Sombre"
                    }
                )
                if (themeModalOpened) {
                    RadioDialog(
                        "Thème de l'application",
                        { themeModalOpened = false }
                    ) {
                        RadioItem(
                            "Système",
                            themeMode == ThemeMode.SYSTEM,
                            { viewModel.setTheme(ThemeMode.SYSTEM) },
                            Modifier.fillMaxWidth()
                        )
                        RadioItem(
                            "Clair",
                            themeMode == ThemeMode.LIGHT,
                            { viewModel.setTheme(ThemeMode.LIGHT) },
                            Modifier.fillMaxWidth()
                        )
                        RadioItem(
                            "Sombre",
                            themeMode == ThemeMode.DARK,
                            { viewModel.setTheme(ThemeMode.DARK) },
                            Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Spacer(Modifier.weight(1f))
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
                    Icons.Filled.Build,
                    "Version de l'application",
                    {},
                    underText = "a-0.24.1"
                )
            }
        }
    }
}

@Composable
fun RadioDialog(
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
        ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceContainer,
                                MaterialTheme.shapes.medium
                            )
                    ) {
                        content()
                    }
                }
        }
    }
}

@Composable
fun RadioItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .height(56.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected, null)
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@PreviewLightDarkCombo
@Composable
fun PreferencesScreenPreview(modifier: Modifier = Modifier) {
    ForestryTheme {
        PreferencesScreen(FakeForestryViewModel(), modifier)
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@PreviewLightDarkCombo
@Composable
fun RadioDialogPreview(modifier: Modifier = Modifier) {
    ForestryTheme {
        RadioDialog("Titre", {}, modifier) {
            RadioItem("blabla", true, {}, modifier)
            RadioItem("blabla", false, {}, modifier)
            RadioItem("blabla", false, {}, modifier)
        }
    }
}
