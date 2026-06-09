package com.example.forestry.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forestry.data.api.ForestryAPI
import com.example.forestry.data.database.ForestryDatabase
import com.example.forestry.data.datastore.DataStoreManager
import com.example.forestry.data.repositories.DataStoreRepository
import com.example.forestry.data.repositories.ForestryRepository
import com.example.forestry.data.repositories.GNSSRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ForestryViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForestryViewModel::class.java)) {
            val api: ForestryAPI by lazy {
                Retrofit.Builder()
                    .baseUrl("https://gnssv2.alwaysdata.net/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ForestryAPI::class.java)
            }

            val forestryRepository = ForestryRepository(
                api = api,
                dao = ForestryDatabase.getDatabase(application).forestryDao(),
            )
            val dataStoreRepository = DataStoreRepository(DataStoreManager(application))
            val gnssRepository = GNSSRepository(application)
            @Suppress("UNCHECKED_CAST")
            return ForestryViewModel(
                forestryRepository = forestryRepository,
                dataStoreRepository = dataStoreRepository,
                gnssRepository = gnssRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}