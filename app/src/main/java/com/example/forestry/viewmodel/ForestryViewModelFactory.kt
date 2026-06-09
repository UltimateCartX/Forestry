package com.example.forestry.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ForestryViewModelFactory(private val application: android.app.Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForestryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForestryViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}