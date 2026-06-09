package com.example.forestry.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.example.forestry.ui.previews.PreviewLightDarkCombo
import com.example.forestry.ui.theme.ForestryTheme

@Composable
fun SettingsItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    underText: String? = null,
) {
    SettingsItem(
        rememberVectorPainter(icon),
        text,
        onClick,
        modifier,
        underText
    )
}

@Composable
fun SettingsItem(
    icon: Painter,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    underText: String? = null,
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

@PreviewLightDarkCombo
@Composable
fun SettingsItemPreview(modifier: Modifier = Modifier) {
    ForestryTheme {
        SettingsItem(Icons.Filled.Settings, "Préférences", {})
    }
}