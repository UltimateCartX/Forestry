package com.example.forestry.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.forestry.ui.previews.PreviewLightDarkCombo
import com.example.forestry.ui.theme.ForestryTheme

@Composable
fun ProjectItem(
    name: String,
    author: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall // headlineMedium might be too big for lists
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = author,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Better hierarchy
            )
        }
    }
}

@PreviewLightDarkCombo
@Composable
fun ProjectItemPreview(modifier: Modifier = Modifier) {
    ForestryTheme {
        ProjectItem("Forêt de bois de boulogne", "Chépa Chépa", {})
    }
}