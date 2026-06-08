package com.example.forestry.ui.previews

import com.example.forestry.data.models.BluetoothConnectionState
import com.example.forestry.viewmodel.ForestryViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A fake ViewModel used for previews
 *
 * Can override states if wanted
 */
class FakeForestryViewModel : ForestryViewModel(

) {
    override val gnssConnectionState: StateFlow<BluetoothConnectionState> = MutableStateFlow<BluetoothConnectionState>(BluetoothConnectionState.Disconnected)
}