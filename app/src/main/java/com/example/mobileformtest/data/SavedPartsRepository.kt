package com.example.mobileformtest.data

import android.util.Log
import com.example.mobileformtest.model.Car
import com.example.mobileformtest.model.CarPart
import com.example.mobileformtest.model.SavedPart
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class SavedPartsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val SAVED_PARTS_COLLECTION = "savedParts"
        private const val PLACEHOLDER_DOC = "_meta"
    }

    /**
     * Save a part to user's savedParts collection.
     */
    suspend fun savePart(userId: String, car: Car, part: CarPart) {
        val docId = buildPartId(car, part)

        val data = mapOf(
            "docId" to docId,
            "carId" to car.id,
            "carMake" to car.make,
            "carModel" to car.model,
            "carYear" to car.year,
            "name" to part.name,
            "category" to part.category,
            "price" to part.price,
            "inStock" to part.inStock,
            "savedAt" to FieldValue.serverTimestamp()
        )

        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(SAVED_PARTS_COLLECTION)
            .document(docId)
            .set(data, SetOptions.merge())
            .await()
    }

    /**
     * Remove a part from user's savedParts collection.
     */
    suspend fun removePart(userId: String, partDocId: String) {
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(SAVED_PARTS_COLLECTION)
            .document(partDocId)
            .delete()
            .await()
    }

    /**
     * Load all saved parts for a user.
     */
    suspend fun loadSavedParts(userId: String): List<SavedPart> {
        return try {
            val snapshot = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(SAVED_PARTS_COLLECTION)
                .get()
                .await()

            snapshot.documents
                .filter { it.id != PLACEHOLDER_DOC }
                .mapNotNull { doc ->
                    try {
                        SavedPart(
                            docId = doc.getString("docId") ?: doc.id,
                            carId = (doc.getLong("carId") ?: 0L).toInt(),
                            carMake = doc.getString("carMake") ?: "Unknown",
                            carModel = doc.getString("carModel") ?: "Unknown",
                            carYear = (doc.getLong("carYear") ?: 0L).toInt(),
                            name = doc.getString("name") ?: "Unknown",
                            category = doc.getString("category") ?: "",
                            price = doc.getDouble("price") ?: 0.0,
                            inStock = doc.getBoolean("inStock") ?: false
                        )
                    } catch (e: Exception) {
                        Log.e("SavedPartsRepository", "Error parsing part: ${e.message}", e)
                        null
                    }
                }
        } catch (e: Exception) {
            Log.e("SavedPartsRepository", "Error loading parts: ${e.message}", e)
            emptyList()
        }
    }

    private fun buildPartId(car: Car, part: CarPart): String {
        val raw = "${car.id}|${part.category}|${part.name}".lowercase()
        return raw.hashCode().toString()
    }
}
