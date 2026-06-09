package com.example.forestry.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.forestry.R
import com.example.forestry.data.enums.TreeClass
import com.example.forestry.data.models.Project
import com.example.forestry.ui.composables.ForestryScaffold
import com.example.forestry.ui.composables.RadioItem
import com.example.forestry.ui.composables.SettingsItem
import com.example.forestry.ui.navigation.Screen
import com.example.forestry.ui.previews.PreviewLightDarkCombo
import com.example.forestry.ui.theme.ForestryTheme
import com.example.forestry.viewmodel.ForestryViewModel

@Composable
fun NewTreeScreen(
    viewModel: ForestryViewModel,
    modifier: Modifier = Modifier
) {
    val currentProject by viewModel.currentProject.collectAsState()
    val newTreeLat by viewModel.newTreeLat.collectAsState()
    val newTreeLon by viewModel.newTreeLon.collectAsState()
    val newTreeEssence by viewModel.newTreeEssence.collectAsState()
    val newTreeDiameter by viewModel.newTreeDiameter.collectAsState()
    val newTreeHeight by viewModel.newTreeHeight.collectAsState()
    val newTreeState by viewModel.newTreeState.collectAsState()
    val newTreeClass by viewModel.newTreeClass.collectAsState()

    NewTreeContent(
        currentProject = currentProject,
        newTreeLat = newTreeLat,
        newTreeLon = newTreeLon,
        newTreeEssence = newTreeEssence,
        newTreeDiameter = newTreeDiameter,
        newTreeHeight = newTreeHeight,
        newTreeState = newTreeState,
        newTreeClass = newTreeClass,
        onNavigateBack = viewModel::navigateBack,
        onChangeProjectClick = { viewModel.navigateTo(Screen.PROJECTS) },
        onEssenceTextFieldInput = viewModel::setNewTreeEssence,
        onDiameterTextFieldInput = viewModel::setNewTreeDiameter,
        onHeightTextFieldInput = viewModel::setNewTreeHeight,
        onStateTextFieldInput = viewModel::setNewTreeState,
        onClassRadioInput = viewModel::setNewTreeClass,
        onConfirmClick = { viewModel.createNewTree(); viewModel.navigateBack() },
        modifier = modifier
    )
}

@Composable
fun NewTreeContent(
    currentProject: Project?,
    newTreeLat: Double,
    newTreeLon: Double,
    newTreeEssence: String,
    newTreeDiameter: String,
    newTreeHeight: String,
    newTreeState: String,
    newTreeClass: TreeClass,
    onNavigateBack: () -> Unit,
    onChangeProjectClick: () -> Unit,
    onEssenceTextFieldInput: (String) -> Unit,
    onDiameterTextFieldInput: (String) -> Unit,
    onHeightTextFieldInput: (String) -> Unit,
    onStateTextFieldInput: (String) -> Unit,
    onClassRadioInput: (TreeClass) -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    ForestryScaffold(
        title = "Enregistrer le nouvel arbre",
        onNavigateBack = onNavigateBack,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                        MaterialTheme.shapes.medium
                    )
            ) {
                SettingsItem(
                    icon = painterResource(R.drawable.icon_forest_filled),
                    text = "Changer le projet actuel",
                    onClick = onChangeProjectClick,
                    underText = currentProject?.name
                )
            }
            TextField(
                value = "$newTreeLat° N, $newTreeLon° E",
                onValueChange = {},
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null
                    )
                },
                label = { Text("Position") },
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            TextField(
                value = newTreeEssence,
                onValueChange = onEssenceTextFieldInput,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.icon_tree_filled),
                        contentDescription = null
                    )
                },
                label = { Text("Essence") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            Row {
                TextField(
                    value = newTreeDiameter,
                    onValueChange = onDiameterTextFieldInput,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.icon_diameter),
                            contentDescription = null
                        )
                    },
                    label = { Text("Diamètre") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(top = 8.dp, end = 4.dp)
                )
                TextField(
                    value = newTreeHeight,
                    onValueChange = onHeightTextFieldInput,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.icon_height),
                            contentDescription = null
                        )
                    },
                    label = { Text("Hauteur") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .padding(start = 4.dp, top = 8.dp)
                )
            }
            TextField(
                value = newTreeState,
                onValueChange = onStateTextFieldInput,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.icon_state),
                        contentDescription = null
                    )
                },
                label = { Text("Etat") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            Column(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                        MaterialTheme.shapes.medium
                    )
            ) {
                Text(
                    text = "Classe",
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioItem(
                        text = "Petit",
                        selected = newTreeClass == TreeClass.SMALL,
                        onClick = { onClassRadioInput(TreeClass.SMALL) }
                    )
                    RadioItem(
                        text = "Moyen",
                        selected = newTreeClass == TreeClass.MEDIUM,
                        onClick = { onClassRadioInput(TreeClass.MEDIUM) }
                    )
                    RadioItem(
                        text = "Gros",
                        selected = newTreeClass == TreeClass.BIG,
                        onClick = { onClassRadioInput(TreeClass.BIG) }
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.shapes.medium
                    )
            ) {
                Text(
                    text = "Vérifiez bien l'exactitude des informations de l'arbre, ils ne pourront plus être modifier une fois validé!",
                    modifier = Modifier.padding(8.dp)
                )
            }
            Button(
                onClick = onConfirmClick,
                enabled = currentProject != null &&
                        newTreeEssence.isNotBlank() &&
                        newTreeDiameter.isNotBlank() &&
                        newTreeHeight.isNotBlank() &&
                        newTreeState.isNotBlank(),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Confirmer",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@PreviewLightDarkCombo
@Composable
fun NewTreeScreenPreview() {
    ForestryTheme {
        NewTreeContent(
            currentProject = null,
            newTreeLat = 0.0,
            newTreeLon = 0.0,
            newTreeEssence = "",
            newTreeDiameter = "",
            newTreeHeight = "",
            newTreeState = "",
            newTreeClass = TreeClass.SMALL,
            onNavigateBack = {},
            onChangeProjectClick = {},
            onEssenceTextFieldInput = {},
            onDiameterTextFieldInput = {},
            onHeightTextFieldInput = {},
            onStateTextFieldInput = {},
            onClassRadioInput = {},
            onConfirmClick = {}
        )
    }
}