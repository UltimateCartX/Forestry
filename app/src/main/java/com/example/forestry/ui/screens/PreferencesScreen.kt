package com.example.forestry.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.forestry.data.models.ThemeMode
import com.example.forestry.data.models.getDisplayName
import com.example.forestry.ui.composables.ForestryScaffold
import com.example.forestry.ui.composables.RadioDialog
import com.example.forestry.ui.composables.RadioItem
import com.example.forestry.ui.composables.SettingsColumn
import com.example.forestry.ui.composables.SettingsItem
import com.example.forestry.ui.previews.PreviewLightDarkCombo
import com.example.forestry.ui.theme.ForestryTheme
import com.example.forestry.viewmodel.ForestryViewModel

@Composable
fun PreferencesScreen(
    viewModel: ForestryViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    PreferencesContent(
        themeMode = themeMode,
        onThemeChange = viewModel::setTheme,
        onNavigateBack = viewModel::navigateBack,
        modifier = modifier
    )
}

@Composable
fun PreferencesContent(
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var themeModalOpened by remember { mutableStateOf(false) }

    ForestryScaffold(
        title = "Préférences",
        onNavigateBack = onNavigateBack,
        modifier = modifier
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            SettingsColumn {
                SettingsItem(
                    icon = Icons.Filled.Settings,
                    text = "Thème de l'application",
                    onClick = { themeModalOpened = true },
                    underText = themeMode.getDisplayName()
                )
                if (themeModalOpened) {
                    RadioDialog(
                        title = "Thème de l'application",
                        onDismissRequest = { themeModalOpened = false }
                    ) {
                        ThemeMode.entries.forEach { mode ->
                            RadioItem(
                                text = mode.getDisplayName(),
                                selected = themeMode == mode,
                                onClick = {
                                    onThemeChange(mode)
                                    themeModalOpened = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            SettingsColumn {
                SettingsItem(
                    icon = Icons.Filled.Build,
                    text = "Version de l'application",
                    onClick = {},
                    underText = "a-0.24.2"
                )
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@PreviewLightDarkCombo
@Composable
fun PreferencesScreenPreview() {
    ForestryTheme {
        PreferencesContent(
            themeMode = ThemeMode.SYSTEM,
            onThemeChange = {},
            onNavigateBack = {},
        )
    }
}