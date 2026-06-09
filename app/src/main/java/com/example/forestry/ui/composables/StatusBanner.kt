package com.example.forestry.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.forestry.ui.previews.PreviewLightDarkCombo
import com.example.forestry.ui.theme.ForestryTheme

enum class StatusType {
    ERROR, WARNING
}

@Composable
fun StatusBanner(
    text: String,
    type: StatusType,
    modifier: Modifier = Modifier
) {
    val (containerColor, contentColor, icon) = when (type) {
        StatusType.ERROR -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            Icons.Default.Error
        )
        StatusType.WARNING -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            Icons.Default.Warning
        )
    }

    Surface(
        color = containerColor,
        contentColor = contentColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = type.name,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@PreviewLightDarkCombo
@Composable
fun ErrorStatusBannerPreview() {
    ForestryTheme {
        StatusBanner(text = "Example Error Message", type = StatusType.ERROR)
    }
}

@PreviewLightDarkCombo
@Composable
fun WarningStatusBannerPreview() {
    ForestryTheme {
        StatusBanner(text = "Example Warning Message", type = StatusType.WARNING)
    }
}
