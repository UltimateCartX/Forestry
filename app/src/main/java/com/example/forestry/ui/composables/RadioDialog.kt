package com.example.forestry.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.forestry.ui.previews.PreviewLightDarkCombo
import com.example.forestry.ui.theme.ForestryTheme

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

@PreviewLightDarkCombo
@Composable
fun RadioDialogPreview() {
    ForestryTheme {
        RadioDialog("Faites un choix", {}) {
            RadioItem("Choix 1", true, {})
            RadioItem("Choix 2", false, {})
            RadioItem("Choix 3", false, {})
        }
    }
}