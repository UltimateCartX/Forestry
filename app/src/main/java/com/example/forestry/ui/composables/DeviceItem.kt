package com.example.forestry.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.forestry.ui.previews.PreviewLightDarkCombo
import com.example.forestry.ui.theme.ForestryTheme

@Composable
fun DeviceItem(
    name: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text = name, modifier = Modifier.padding(vertical = 8.dp))
    }
}

@PreviewLightDarkCombo
@Composable
fun DeviceItemPreview() {
    ForestryTheme {
        DeviceItem(
            name = "Forestry GNSS 01",
            enabled = true,
            onClick = {}
        )
    }
}