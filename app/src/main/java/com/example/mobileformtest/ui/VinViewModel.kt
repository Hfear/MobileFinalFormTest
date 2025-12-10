package com.example.mobileformtest.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileformtest.data.VinRepository
import com.example.mobileformtest.model.DecodedVehicle
import kotlinx.coroutines.launch

sealed interface VinUiState {
    object Idle : VinUiState
    object Loading : VinUiState
    data class Success(val vehicle: DecodedVehicle) : VinUiState
    data class Error(val message: String) : VinUiState
}

class VinViewModel : ViewModel() {
    var vinUiState: VinUiState by mutableStateOf(VinUiState.Idle)
        private set

    // Store saved vehicles
    private val _savedVehicles = mutableListOf<DecodedVehicle>()
    val savedVehicles: List<DecodedVehicle> get() = _savedVehicles

    private val repository = VinRepository()

    fun decodeVin(vin: String) {
        if (vin.length < 11) {
            vinUiState = VinUiState.Error("VIN must be at least 11 characters")
            return
        }

        viewModelScope.launch {
            vinUiState = VinUiState.Loading

            val vehicle = repository.decodeVin(vin)
            vinUiState = if (vehicle != null) {
                VinUiState.Success(vehicle)
            } else {
                VinUiState.Error("Failed to decode VIN. Please check and try again.")
            }
        }
    }

    fun saveVehicle(vehicle: DecodedVehicle) {
        if (!_savedVehicles.any { it.vin == vehicle.vin }) {
            _savedVehicles.add(vehicle)
        }
    }

    fun reset() {
        vinUiState = VinUiState.Idle
    }
}