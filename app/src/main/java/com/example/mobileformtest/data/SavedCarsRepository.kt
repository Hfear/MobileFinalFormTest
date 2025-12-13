package com.example.mobileformtest.data

import com.example.mobileformtest.model.Car
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
    }

    suspend fun saveCar(userId: String, car: Car) {
        val data = mapOf(
            "carId" to car.id,
            "make" to car.make,
            "model" to car.model,
            "year" to car.year,
            "imageUrl" to car.imageUrl,
            "savedAt" to FieldValue.serverTimestamp()
        )

        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(SAVED_CARS_COLLECTION)
            .document(car.id.toString())
            .set(data, SetOptions.merge())
            .await()
    }
}
