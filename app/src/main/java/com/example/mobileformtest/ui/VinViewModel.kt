package com.example.mobileformtest.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileformtest.data.CarRepository
import com.example.mobileformtest.data.SavedCarsRepository
import com.example.mobileformtest.data.VinRepository
import com.example.mobileformtest.model.Car
import com.example.mobileformtest.model.DecodedVehicle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import android.util.Log

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

    private val vinRepository = VinRepository()
    private val carRepository = CarRepository(context)
    private val savedCarsRepository = SavedCarsRepository()
    private val firestore = Firebase.firestore

    fun decodeVin(vin: String) {
        if (vin.length < 11) {
            vinUiState = VinUiState.Error("VIN must be at least 11 characters")
            return
        }

        viewModelScope.launch {
            vinUiState = VinUiState.Loading

            val vehicle = vinRepository.decodeVin(vin)
            if (vehicle != null) {
                // Auto-add to catalog
                carRepository.addCarFromVinDecoder(vehicle)

                vinUiState = VinUiState.Success(vehicle)
            } else {
                vinUiState = VinUiState.Error("Failed to decode VIN. Please check and try again.")
            }
        }
    }

    // Save to user profile
    fun saveVehicleToProfile(vehicle: DecodedVehicle, userId: String?) {
        if (!_savedVehicles.any { it.vin == vehicle.vin }) {
            _savedVehicles.add(vehicle)
        }

        if (userId != null) {
            viewModelScope.launch {
                try {
                    val car = carRepository.findCarBySpecs(
                        vehicle.make,
                        vehicle.model,
                        vehicle.year.toIntOrNull() ?: 0
                    )

                    if (car != null) {
                        savedCarsRepository.saveCar(userId, car)
                        Log.d("VinViewModel", "Saved car to user profile")
                    } else {
                        saveVehicleDataToFirebase(vehicle, userId)
                    }
                } catch (e: Exception) {
                    Log.e("VinViewModel", "Error saving vehicle: ${e.message}")
                }
            }
        }
    }

    // Load user's vehicles
    fun loadVehiclesFromFirebase(userId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("savedCars")
            .get()
            .addOnSuccessListener { documents ->
                val vehicles = documents.mapNotNull { doc ->
                    try {
                        DecodedVehicle(
                            vin = doc.getString("vin") ?: doc.id,
                            make = doc.getString("make") ?: "Unknown",
                            model = doc.getString("model") ?: "Unknown",
                            year = doc.getLong("year")?.toString() ?: "Unknown",
                            vehicleType = doc.getString("vehicleType") ?: "Unknown",
                            manufacturer = doc.getString("manufacturer") ?: "Unknown",
                            plantCountry = "",
                            engineInfo = ""
                        )
                    } catch (e: Exception) {
                        Log.e("VinViewModel", "Error parsing vehicle: ${e.message}")
                        null
                    }
                }
                _savedVehicles.clear()
                _savedVehicles.addAll(vehicles)
            }
            .addOnFailureListener { e ->
                Log.e("VinViewModel", "Error loading vehicles: ${e.message}")
            }
    }

    // Submit missing info for a vehicle
    fun submitMissingInfo(
        vehicle: DecodedVehicle,
        updates: Map<String, String>,
        userId: String?
    ) {
        if (userId == null) return

        viewModelScope.launch {
            val submissionData = hashMapOf(
                "vin" to vehicle.vin,
                "make" to vehicle.make,
                "model" to vehicle.model,
                "year" to vehicle.year,
                "updates" to updates,
                "submittedBy" to userId,
                "status" to "pending",
                "timestamp" to System.currentTimeMillis()
            )

            firestore.collection("user_contributions")
                .add(submissionData)
                .addOnSuccessListener {
                    Log.d("VinViewModel", "User contribution submitted")
                }
                .addOnFailureListener { e ->
                    Log.e("VinViewModel", "Error submitting: ${e.message}")
                }
        }
    }

    // Clear on sign out
    fun clearSavedVehicles() {
        _savedVehicles.clear()
    }

    fun reset() {
        vinUiState = VinUiState.Idle
    }

    // Update getCarFromVehicle to include compatible parts
    suspend fun getCarFromVehicle(vehicle: DecodedVehicle): Car {
        val catalogCar = carRepository.findCarBySpecs(
            vehicle.make,
            vehicle.model,
            vehicle.year.toIntOrNull() ?: 0
        )

        if (catalogCar != null) {
            return catalogCar
        }

        // Get compatible parts from similar vehicles
        val compatibleParts = carRepository.getCompatibleParts(
            vehicle.make,
            vehicle.model,
            vehicle.year.toIntOrNull() ?: 0
        )

        // Create car with compatible parts
        return Car(
            id = vehicle.vin.hashCode(),
            make = vehicle.make,
            model = vehicle.model,
            year = vehicle.year.toIntOrNull() ?: 0,
            imageUrl = "${vehicle.make.lowercase()}_${vehicle.model.lowercase()}",
            parts = compatibleParts
        )
    }

    private fun saveVehicleDataToFirebase(vehicle: DecodedVehicle, userId: String) {
        val vehicleData = hashMapOf(
            "vin" to vehicle.vin,
            "make" to vehicle.make,
            "model" to vehicle.model,
            "year" to (vehicle.year.toIntOrNull() ?: 0),
            "vehicleType" to vehicle.vehicleType,
            "manufacturer" to vehicle.manufacturer,
            "carId" to "${vehicle.make}_${vehicle.model}_${vehicle.year}".hashCode(),
            "imageUrl" to "${vehicle.make.lowercase()}_${vehicle.model.lowercase()}",
            "savedAt" to System.currentTimeMillis()
        )

        firestore.collection("users")
            .document(userId)
            .collection("savedCars")
            .document(vehicle.vin)
            .set(vehicleData)
            .addOnSuccessListener {
                Log.d("VinViewModel", "Vehicle data saved")
            }
            .addOnFailureListener { e ->
                Log.e("VinViewModel", "Error saving vehicle data: ${e.message}")
            }
    }
}