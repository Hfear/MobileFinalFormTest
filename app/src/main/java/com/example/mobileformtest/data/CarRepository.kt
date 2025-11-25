package com.example.mobileformtest.data

import android.content.Context
import com.example.mobileformtest.model.Car
import com.example.mobileformtest.model.CarPart
import com.example.mobileformtest.model.CarResponse
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import java.io.IOException

/**
 * Repository for car data
 * Handles reading from local JSON file (simulating API calls)
 * Will be easy to swap to real API calls later
 */
class CarRepository(
    private val context: Context,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    companion object {
        private const val CARS_COLLECTION = "cars"
    }

    /**
     * Fetch cars from local JSON file
     *
     * @return List of cars
     * @throws IOException if file cannot be read
     */
    suspend fun getCars(): List<Car> {
        return try {
            fetchCarsFromFirestore()
        } catch (_: Exception) {
            loadCarsFromAssets()
        }
    }

    /**
     * Search cars by make or model
     *
     * @param query Search term
     * @return Filtered list of cars
     * @throws IOException if data cannot be loaded
     */
    suspend fun searchCars(query: String): List<Car> {
        val allCars = getCars()

        return if (query.isBlank()) {
            allCars
        } else {
            allCars.filter { car ->
                car.make.contains(query, ignoreCase = true) ||
                        car.model.contains(query, ignoreCase = true)
            }
        }
    }

    /**
     * Get a specific car by ID
     *
     * @param carId The car's ID
     * @return Car if found, null otherwise
     * @throws IOException if data cannot be loaded
     */
    suspend fun getCarById(carId: Int): Car? {
        val allCars = getCars()
        return allCars.find { it.id == carId }
    }

    /**
     * Load JSON file from assets folder
     *
     * @param fileName Name of the JSON file
     * @return String content of the file
     * @throws IOException if file cannot be read
     */
    private fun loadJsonFromAssets(fileName: String): String {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            throw IOException("Failed to read JSON file: $fileName", e)
        }
    }

    private fun loadCarsFromAssets(): List<Car> {
        val jsonString = loadJsonFromAssets("cars_data.json")
        val response = json.decodeFromString<CarResponse>(jsonString)
        return response.cars
    }

    private suspend fun fetchCarsFromFirestore(): List<Car> {
        val snapshot = firestore.collection(CARS_COLLECTION).get().await()
        val cars = snapshot.documents.mapNotNull { it.toCar() }
        if (cars.isEmpty()) throw IOException("No cars found in Firestore")
        return cars
    }

    private fun DocumentSnapshot.toCar(): Car? {
        val id = getLong("id")?.toInt() ?: id.toIntOrNull()
        val make = getString("make") ?: return null
        val model = getString("model") ?: return null
        val year = getLong("year")?.toInt()
        val imageUrl = getString("imageUrl") ?: return null
        val parts = (get("parts") as? List<*>)
            ?.mapNotNull { (it as? Map<*, *>)?.toCarPart() }
            ?: emptyList()

        if (id == null || year == null) return null

        return Car(
            id = id,
            make = make,
            model = model,
            year = year,
            imageUrl = imageUrl,
            parts = parts
        )
    }

    private fun Map<*, *>.toCarPart(): CarPart? {
        val name = this["name"] as? String ?: return null
        val category = this["category"] as? String ?: return null
        val price = when (val value = this["price"]) {
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull()
            else -> null
        } ?: return null
        val inStock = when (val value = this["inStock"]) {
            is Boolean -> value
            is String -> value.toBooleanStrictOrNull() ?: false
            else -> false
        }
        return CarPart(name, category, price, inStock)
    }
}