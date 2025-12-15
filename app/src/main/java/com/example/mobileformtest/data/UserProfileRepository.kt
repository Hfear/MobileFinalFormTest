package com.example.mobileformtest.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

/**
 * Handles creation of per-user Firestore scaffolding (profile doc + saved lists).
 */
class UserProfileRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val SAVED_CARS_COLLECTION = "savedCars"
        private const val SAVED_PARTS_COLLECTION = "savedParts"
        private const val PLACEHOLDER_DOC = "_meta"
    }

    fun initializeUserStructure(uid: String, email: String): Task<Void> {
        val batch = firestore.batch()
        val userDoc = firestore.collection(USERS_COLLECTION).document(uid)
        val timestamp = FieldValue.serverTimestamp()

        val profileData = mapOf(
            "email" to email,
            "createdAt" to timestamp,
            "updatedAt" to timestamp
        )
        batch.set(userDoc, profileData, SetOptions.merge())

        val placeholderData = mapOf("seededAt" to timestamp)
        batch.set(
            userDoc.collection(SAVED_CARS_COLLECTION).document(PLACEHOLDER_DOC),
            placeholderData,
            SetOptions.merge()
        )
        batch.set(
            userDoc.collection(SAVED_PARTS_COLLECTION).document(PLACEHOLDER_DOC),
            placeholderData,
            SetOptions.merge()
        )

        return batch.commit()
    }
}
