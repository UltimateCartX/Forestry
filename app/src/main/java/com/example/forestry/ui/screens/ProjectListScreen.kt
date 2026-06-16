package com.example.forestry.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.forestry.R
import com.example.forestry.data.models.Project
import com.example.forestry.ui.composables.ProjectItem
import com.example.forestry.ui.previews.PreviewLightDarkCombo
import com.example.forestry.ui.theme.ForestryTheme
import com.example.forestry.viewmodel.ForestryViewModel
import java.util.UUID

@Composable
fun ProjectListScreen(
    viewModel: ForestryViewModel,
) {
    val projects by viewModel.projects.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadProjects()
    }

    ProjectListContent(
        projects = projects,
        onProjectClick = { project ->
            viewModel.setCurrentProject(project)
            viewModel.navigateBack()
        },
        onBackClick = viewModel::navigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListContent(
    projects: List<Project>,
    onProjectClick: (Project) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                title = {
                    Text("Liste des projets")
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (projects.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text("Aucun projet")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
            ) {
                items(
                    items = projects,
                    key = { project -> project.id }
                ) { project ->
                    ProjectItem(
                        name = project.name,
                        author = project.ownerName,
                        onClick = { onProjectClick(project) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@PreviewLightDarkCombo
@Composable
fun ProjectListPreview(modifier: Modifier = Modifier) {
    ForestryTheme {
        ProjectListContent(
            projects = listOf(
                Project(UUID.randomUUID(), "Forêt A", emptyList(), "Forestier Hors Ligne"),
                Project(UUID.randomUUID(), "Forêt B", emptyList(), "John Doe"),
            ),
            onProjectClick = {},
            onBackClick = {}
        )
    }
}