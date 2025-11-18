package com.example.mobileformtest.data

import android.content.Context
import com.example.mobileformtest.model.Car
import com.example.mobileformtest.model.CarResponse
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.io.IOException

/**
 * Repository for car data
 * Handles reading from local JSON file (simulating API calls)
 * Will be easy to swap to real API calls later
 */
class CarRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Fetch cars from local JSON file
     * Simulates an API call with delay for realistic loading states
     *
     * @return List of cars
     * @throws IOException if file cannot be read
     */
    suspend fun getCars(): List<Car> {
        // Simulate network delay
        delay(500)

        // Read JSON from assets
        val jsonString = loadJsonFromAssets("cars_data.json")

        // Parse JSON using kotlinx-serialization
        val response = json.decodeFromString<CarResponse>(jsonString)

        return response.cars
    }

    /**
     * Search cars by make or model
     *
     * @param query Search term
     * @return Filtered list of cars
     * @throws IOException if data cannot be loaded
     */
    suspend fun searchCars(query: String): List<Car> {
        delay(300)

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
        delay(200)

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
}