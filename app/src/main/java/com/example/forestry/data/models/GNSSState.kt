package com.example.forestry.data.models

sealed class GNSSState {
    data object DISCONNECTED: GNSSState()
    data object ERROR : GNSSState()
    data object WORKING : GNSSState()
}