package com.example.mobileformtest.data

import android.util.Log
import com.example.mobileformtest.model.Car
import com.example.mobileformtest.model.DecodedVehicle
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

/**
 * Handles persistence of saved cars under each user's Firestore document.
 */
class SavedCarsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val SAVED_CARS_COLLECTION = "savedCars"
        private const val CARS_COLLECTION = "cars"
    }

    /**
     * Save catalog car to user's savedCars collection.
     * docId: car.id, source: CATALOG, vin: empty
     */
    suspend fun saveCar(userId: String, car: Car) {
        val docId = car.id.toString()

        val data = mapOf(
            "docId" to docId,
            "source" to "CATALOG",
            "vin" to "",
            "carId" to car.id,
            "make" to car.make,
            "model" to car.model,
            "year" to car.year,
            "imageUrl" to car.imageUrl,
            "vehicleType" to "",
            "manufacturer" to "",
            "plantCountry" to "",
            "engineInfo" to "",
            "savedAt" to FieldValue.serverTimestamp()
        )

        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(SAVED_CARS_COLLECTION)
            .document(docId)
            .set(data, SetOptions.merge())
            .await()

        firestore.collection(CARS_COLLECTION)
            .document(docId)
            .set(
                data + mapOf("updatedAt" to FieldValue.serverTimestamp()),
                SetOptions.merge()
            )
            .await()
    }

    /**
     * Save VIN-decoded vehicle to user's savedCars collection.
     * docId: vehicle.vin, source: VIN
     */
    suspend fun saveVinVehicle(
        userId: String,
        vehicle: DecodedVehicle,
        linkedCatalogCar: Car? = null
    ) {
        val docId = vehicle.vin
        val yearInt = vehicle.year.toIntOrNull() ?: 0
        val carId = linkedCatalogCar?.id ?: vehicle.vin.hashCode()
        val imageUrl = linkedCatalogCar?.imageUrl ?: ""

        val data = mapOf(
            "docId" to docId,
            "source" to "VIN",
            "vin" to vehicle.vin,
            "carId" to carId,
            "make" to vehicle.make,
            "model" to vehicle.model,
            "year" to yearInt,
            "imageUrl" to imageUrl,
            "vehicleType" to vehicle.vehicleType,
            "manufacturer" to vehicle.manufacturer,
            "plantCountry" to vehicle.plantCountry,
            "engineInfo" to vehicle.engineInfo,
            "savedAt" to FieldValue.serverTimestamp()
        )

        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(SAVED_CARS_COLLECTION)
            .document(docId)
            .set(data, SetOptions.merge())
            .await()

        firestore.collection(CARS_COLLECTION)
            .document(docId)
            .set(
                data + mapOf("updatedAt" to FieldValue.serverTimestamp()),
                SetOptions.merge()
            )
            .await()
    }

    /**
     * Load all saved vehicles for a user.
     */
    suspend fun loadSavedVehicles(userId: String): List<DecodedVehicle> {
        return try {
            val snapshot = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(SAVED_CARS_COLLECTION)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    DecodedVehicle(
                        vin = doc.getString("vin")?.takeIf { it.isNotBlank() } ?: doc.id,
                        make = doc.getString("make") ?: "Unknown",
                        model = doc.getString("model") ?: "Unknown",
                        year = (doc.getLong("year") ?: 0L).toString(),
                        vehicleType = doc.getString("vehicleType") ?: "",
                        manufacturer = doc.getString("manufacturer") ?: "",
                        plantCountry = doc.getString("plantCountry") ?: "",
                        engineInfo = doc.getString("engineInfo") ?: ""
                    )
                } catch (e: Exception) {
                    Log.e("SavedCarsRepository", "Error parsing vehicle: ${e.message}", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("SavedCarsRepository", "Error loading vehicles: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Submit user contribution for missing vehicle info.
     */
    suspend fun submitContribution(
        vehicle: DecodedVehicle,
        updates: Map<String, String>,
        userId: String
    ) {
        try {
            val submissionData = hashMapOf(
                "vin" to vehicle.vin,
                "make" to vehicle.make,
                "model" to vehicle.model,
                "year" to vehicle.year,
                "updates" to updates,
                "submittedBy" to userId,
                "status" to "pending",
                "timestamp" to FieldValue.serverTimestamp()
            )

            firestore.collection("user_contributions")
                .add(submissionData)
                .await()

            Log.d("SavedCarsRepository", "Contribution submitted")
        } catch (e: Exception) {
            Log.e("SavedCarsRepository", "Error submitting contribution: ${e.message}", e)
            throw e
        }
    }
}
