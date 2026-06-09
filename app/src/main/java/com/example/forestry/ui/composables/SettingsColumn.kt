package com.example.forestry.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.forestry.ui.previews.PreviewLightDarkCombo
import com.example.forestry.ui.theme.ForestryTheme

@Composable
fun SettingsColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                MaterialTheme.shapes.medium
            )
    ) { content() }
}

@PreviewLightDarkCombo
@Composable
fun SettingsColumnPreview(modifier: Modifier = Modifier) {
    ForestryTheme {
        SettingsColumn {
            SettingsItem(Icons.Filled.Settings, "Préférences", {})
            HorizontalDivider(thickness = 2.dp)
            SettingsItem(Icons.Filled.Settings, "Préférences", {})
            HorizontalDivider(thickness = 2.dp)
            SettingsItem(Icons.Filled.Settings, "Préférences", {})
        }
    }
}