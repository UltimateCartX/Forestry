package com.example.forestry.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.forestry.R
import com.example.forestry.data.models.TreeClass
import com.example.forestry.ui.navigation.Screen
import com.example.forestry.ui.previews.FakeForestryViewModel
import com.example.forestry.ui.previews.PreviewLightDarkCombo
import com.example.forestry.ui.theme.ForestryTheme
import com.example.forestry.viewmodel.ForestryViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
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
                    Text("Enregistrer le nouvel arbre")
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                        MaterialTheme.shapes.medium
                    )
            ) {
                SettingsItem(
                    painterResource(R.drawable.icon_forest_filled),
                    "Changer le projet actuel",
                    { viewModel.navigateTo(Screen.PROJECTS) },
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
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp)
            )
            TextField(
                value = newTreeEssence,
                onValueChange = viewModel::setNewTreeEssence,
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
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp)
            )
            Row {
                TextField(
                    value = newTreeDiameter.toString(),
                    onValueChange = { viewModel.setNewTreeDiameter(it.toDouble()) },
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
                        .padding(start = 8.dp, top = 8.dp, end = 4.dp)
                )
                TextField(
                    value = newTreeHeight.toString(),
                    onValueChange = { viewModel.setNewTreeHeight(it.toDouble()) },
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
                        .padding(start = 4.dp, top = 8.dp, end = 8.dp)
                )
            }
            TextField(
                value = newTreeState,
                onValueChange = viewModel::setNewTreeState,
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
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp)
            )
            Column(
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                        MaterialTheme.shapes.medium
                    )
            ) {
                Text(
                    "Classe",
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioItem(
                        "Petit",
                        newTreeClass is TreeClass.SMALL,
                        { viewModel.setNewTreeClass(TreeClass.SMALL) }
                    )
                    RadioItem(
                        "Moyen",
                        newTreeClass is TreeClass.MEDIUM,
                        { viewModel.setNewTreeClass(TreeClass.MEDIUM) }
                    )
                    RadioItem(
                        "Gros",
                        newTreeClass is TreeClass.BIG,
                        { viewModel.setNewTreeClass(TreeClass.BIG) }
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.shapes.medium
                    )
            ) {
                Text(
                    "Vérifiez bien l'exactitude des informations de l'arbre, ils ne pourront plus être modifier une fois validé!",
                    modifier = Modifier.padding(8.dp)
                )
            }
            Button(
                onClick = { viewModel.createNewTree(); viewModel.navigateBack() },
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    "Confirmer",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@PreviewLightDarkCombo
@Composable
fun NewTreeScreenPreview(modifier: Modifier = Modifier) {
    ForestryTheme {
        NewTreeScreen(FakeForestryViewModel())
    }
}