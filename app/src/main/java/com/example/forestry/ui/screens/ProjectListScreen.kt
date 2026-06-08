package com.example.forestry.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.forestry.ui.navigation.Screen
import com.example.forestry.ui.previews.FakeForestryViewModel
import com.example.forestry.ui.previews.PreviewLightDarkCombo
import com.example.forestry.ui.theme.ForestryTheme
import com.example.forestry.viewmodel.ForestryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListScreen(
    viewModel: ForestryViewModel,
    modifier: Modifier = Modifier
) {
    viewModel.loadProjects()
    val projects by viewModel.projects.collectAsState()

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
                    Text("Liste des projets")
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        ) {
            items(projects) { project ->
                Button(
                    onClick = { viewModel.setCurrentProject(project); viewModel.navigateBack() },
                    shape = RectangleShape,
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonColors(
                        Color.Transparent,
                        MaterialTheme.colorScheme.onSurface,
                        MaterialTheme.colorScheme.error,
                        MaterialTheme.colorScheme.onError
                    ),
                ) {
                    ProjectItem(
                        name = project.name,
                        "Forestier Hors Ligne",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectItem(
    name: String,
    author: String,
    modifier: Modifier = Modifier
) {
    Card(modifier.padding(8.dp)) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.padding(4.dp))
            Text(
                text = author,
                style = MaterialTheme.typography.bodyLarge
            )/*
            Spacer(Modifier.padding(4.dp))
            Row {
                Text(
                    text = "Nombre d'arbres: 57",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Nombre de forestiers actifs: 57",
                    style = MaterialTheme.typography.labelLarge
                )
            }*/
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@PreviewLightDarkCombo
@Composable
fun ProjectListPreview(modifier: Modifier = Modifier) {
    ForestryTheme {
        ProjectListScreen(FakeForestryViewModel())
    }
}

@PreviewLightDarkCombo
@Composable
fun ProjectItemPreview(modifier: Modifier = Modifier) {
    ForestryTheme {
        ProjectItem("Forêt de bois de boulogne", "Chépa Chépa")
    }
}