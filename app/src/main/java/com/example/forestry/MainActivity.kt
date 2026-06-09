package com.example.forestry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.preference.PreferenceManager
import com.example.forestry.ui.navigation.Navigation
import com.example.forestry.ui.theme.ForestryTheme
import com.example.forestry.viewmodel.ForestryViewModel
import com.example.forestry.viewmodel.ForestryViewModelFactory
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val viewModel: ForestryViewModel = viewModel(
                factory = ForestryViewModelFactory(application)
            )

            val themeMode by viewModel.themeMode.collectAsState()

            ForestryTheme(themeMode) {
                Navigation(viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
    }

    override fun onPause() {
        super.onPause()
        Configuration.getInstance().save(this, PreferenceManager.getDefaultSharedPreferences(this))
    }
}
