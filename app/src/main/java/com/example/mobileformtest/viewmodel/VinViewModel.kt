package com.example.mobileformtest.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileformtest.data.CarRepository
import com.example.mobileformtest.data.SavedCarsRepository
import com.example.mobileformtest.data.SavedPartsRepository
import com.example.mobileformtest.data.VinRepository
import com.example.mobileformtest.model.Car
import com.example.mobileformtest.model.CarPart
import com.example.mobileformtest.model.DecodedVehicle
import com.example.mobileformtest.model.SavedPart
import kotlinx.coroutines.launch

sealed interface VinUiState {
    object Idle : VinUiState
    object Loading : VinUiState
    data class Success(val vehicle: DecodedVehicle) : VinUiState
    data class Error(val message: String) : VinUiState
}

class VinViewModel(private val context: Context) : ViewModel() {

    var vinUiState: VinUiState by mutableStateOf(VinUiState.Idle)
        private set

    private val _savedVehicles = mutableListOf<DecodedVehicle>()
    val savedVehicles: List<DecodedVehicle> get() = _savedVehicles

    var savedParts by mutableStateOf<List<SavedPart>>(emptyList())
        private set

    private val vinRepository = VinRepository()
    private val carRepository = CarRepository(context)
    private val savedCarsRepository = SavedCarsRepository()
    private val savedPartsRepository = SavedPartsRepository()

    /**
     * Decode VIN using NHTSA API and add to catalog.
     */
    fun decodeVin(vin: String) {
        if (vin.length < 11) {
            vinUiState = VinUiState.Error("VIN must be at least 11 characters")
            return
        }

        viewModelScope.launch {
            vinUiState = VinUiState.Loading

            val vehicle = vinRepository.decodeVin(vin)
            if (vehicle != null) {
                carRepository.addCarFromVinDecoder(vehicle)
                vinUiState = VinUiState.Success(vehicle)
            } else {
                vinUiState = VinUiState.Error("Failed to decode VIN. Please check and try again.")
            }
        }
    }

    /**
     * Save vehicle to user's profile.
     */
    fun saveVehicleToProfile(vehicle: DecodedVehicle, userId: String?) {
        if (!_savedVehicles.any { it.vin == vehicle.vin }) {
            _savedVehicles.add(vehicle)
        }

        if (userId == null) {
            Log.w("VinViewModel", "Not signed in; vehicle saved only in memory.")
            return
        }

        viewModelScope.launch {
            try {
                val linkedCar = carRepository.findCarBySpecs(
                    vehicle.make,
                    vehicle.model,
                    vehicle.year.toIntOrNull() ?: 0
                )

                savedCarsRepository.saveVinVehicle(userId, vehicle, linkedCar)
                Log.d("VinViewModel", "Saved vehicle to user profile")
            } catch (e: Exception) {
                Log.e("VinViewModel", "Error saving vehicle: ${e.message}", e)
            }
        }
    }

    /**
     * Load user's saved vehicles from Firebase.
     */
    fun loadVehiclesFromFirebase(userId: String) {
        viewModelScope.launch {
            try {
                val vehicles = savedCarsRepository.loadSavedVehicles(userId)
                _savedVehicles.clear()
                _savedVehicles.addAll(vehicles)
            } catch (e: Exception) {
                Log.e("VinViewModel", "Error loading vehicles: ${e.message}", e)
            }
        }
    }

    /**
     * Load user's saved parts from Firebase.
     */
    fun loadSavedPartsFromFirebase(userId: String) {
        viewModelScope.launch {
            try {
                savedParts = savedPartsRepository.loadSavedParts(userId)
            } catch (e: Exception) {
                Log.e("VinViewModel", "Error loading parts: ${e.message}", e)
                savedParts = emptyList()
            }
        }
    }

    /**
     * Save a part to user's savedParts list.
     */
    suspend fun savePartToProfileSuspend(car: Car, part: CarPart, userId: String) {
        savedPartsRepository.savePart(userId, car, part)
        savedParts = savedPartsRepository.loadSavedParts(userId)
    }

    /**
     * Remove a saved part from user's savedParts list.
     */
    fun removeSavedPart(partDocId: String, userId: String?) {
        if (userId == null) return

        viewModelScope.launch {
            try {
                savedPartsRepository.removePart(userId, partDocId)
                savedParts = savedParts.filterNot { it.docId == partDocId }
            } catch (e: Exception) {
                Log.e("VinViewModel", "Error removing part: ${e.message}", e)
            }
        }
    }

    /**
     * Submit user contribution for missing vehicle info.
     */
    fun submitMissingInfo(
        vehicle: DecodedVehicle,
        updates: Map<String, String>,
        userId: String?
    ) {
        if (userId == null) return

        viewModelScope.launch {
            try {
                savedCarsRepository.submitContribution(vehicle, updates, userId)
                Log.d("VinViewModel", "User contribution submitted")
            } catch (e: Exception) {
                Log.e("VinViewModel", "Error submitting: ${e.message}", e)
            }
        }
    }

    /**
     * Clear saved lists (on sign out).
     */
    fun clearSavedVehicles() {
        _savedVehicles.clear()
        savedParts = emptyList()
    }

    /**
     * Reset VIN decoder state.
     */
    fun reset() {
        vinUiState = VinUiState.Idle
    }

    /**
     * Convert DecodedVehicle to Car for navigation.
     * Finds catalog car or creates one with compatible parts.
     */
    suspend fun getCarFromVehicle(vehicle: DecodedVehicle): Car {
        val catalogCar = carRepository.findCarBySpecs(
            vehicle.make,
            vehicle.model,
            vehicle.year.toIntOrNull() ?: 0
        )

        if (catalogCar != null) return catalogCar

        val compatibleParts = carRepository.getCompatibleParts(
            vehicle.make,
            vehicle.model,
            vehicle.year.toIntOrNull() ?: 0
        )

        return Car(
            id = vehicle.vin.hashCode(),
            make = vehicle.make,
            model = vehicle.model,
            year = vehicle.year.toIntOrNull() ?: 0,
            imageUrl = "${vehicle.make.lowercase()}_${vehicle.model.lowercase()}",
            parts = compatibleParts
        )
    }
}
