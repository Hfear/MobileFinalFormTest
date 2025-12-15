package com.example.mobileformtest.data

import android.content.Context
import android.util.Log
import com.example.mobileformtest.model.Car
import com.example.mobileformtest.model.CarPart
import com.example.mobileformtest.model.CarResponse
import com.example.mobileformtest.model.DecodedVehicle
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import java.io.IOException
import kotlin.math.abs

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

    // ... existing getCars(), searchCars(), getCarById() functions stay the same ...

    suspend fun getCars(): List<Car> {
        return try {
            fetchCarsFromFirestore()
        } catch (e: Exception) {
            Log.e("GetCars", "Error fetching cars from firestore", e)
            loadCarsFromAssets()
        }
    }

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

    suspend fun getCarById(carId: Int): Car? {
        val allCars = getCars()
        return allCars.find { it.id == carId }
    }

    // NEW: Check if car exists in Firestore by make/model/year
    suspend fun carExistsInCatalog(make: String, model: String, year: Int): Boolean {
        return try {
            val snapshot = firestore.collection(CARS_COLLECTION)
                .whereEqualTo("make", make)
                .whereEqualTo("model", model)
                .whereEqualTo("year", year)
                .limit(1)
                .get()
                .await()

            !snapshot.isEmpty
        } catch (e: Exception) {
            Log.e("CarRepository", "Error checking if car exists: ${e.message}")
            false
        }
    }

    // NEW: Automatically add car from VIN decoder to catalog
    suspend fun addCarFromVinDecoder(vehicle: DecodedVehicle): Boolean {
        return try {
            // Check if already exists first
            if (carExistsInCatalog(vehicle.make, vehicle.model, vehicle.year.toIntOrNull() ?: 0)) {
                Log.d("CarRepository", "Car already exists in catalog, skipping")
                return true
            }

            val carId = "${vehicle.make}_${vehicle.model}_${vehicle.year}".hashCode()

            val carData = hashMapOf(
                "id" to carId,
                "make" to vehicle.make,
                "model" to vehicle.model,
                "year" to (vehicle.year.toIntOrNull() ?: 0),
                "imageUrl" to "${vehicle.make.lowercase()}_${vehicle.model.lowercase()}",
                "parts" to emptyList<Map<String, Any>>(), // Empty initially
                "addedFromVin" to true,
                "vin" to vehicle.vin,
                "vehicleType" to vehicle.vehicleType,
                "manufacturer" to vehicle.manufacturer,
                "timestamp" to System.currentTimeMillis()
            )

            firestore.collection(CARS_COLLECTION)
                .document(carId.toString())
                .set(carData)
                .await()

            Log.d("CarRepository", "Successfully added ${vehicle.make} ${vehicle.model} to catalog")
            true
        } catch (e: Exception) {
            Log.e("CarRepository", "Error adding car to catalog: ${e.message}", e)
            false
        }
    }

    // NEW: Find car in catalog by make/model/year
    suspend fun findCarBySpecs(make: String, model: String, year: Int): Car? {
        return try {
            val snapshot = firestore.collection(CARS_COLLECTION)
                .whereEqualTo("make", make)
                .whereEqualTo("model", model)
                .whereEqualTo("year", year)
                .limit(1)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.toCar()
        } catch (e: Exception) {
            Log.e("CarRepository", "Error finding car: ${e.message}")
            null
        }
    }

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
        val id = getLong("id")?.toInt() ?: this.id.toIntOrNull()
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

    // Find compatible parts for a vehicle
    suspend fun getCompatibleParts(make: String, model: String, year: Int): List<CarPart> {
        return try {
            val allCars = getCars()

            // Priority 1: Exact match (same make, model, year)
            val exactMatch = allCars.find {
                it.make.equals(make, ignoreCase = true) &&
                        it.model.equals(model, ignoreCase = true) &&
                        it.year == year
            }
            if (exactMatch != null && exactMatch.parts.isNotEmpty()) {
                return exactMatch.parts
            }

            // Priority 2: Same model, nearby year (±5 years)
            val sameModel = allCars.filter {
                it.make.equals(make, ignoreCase = true) &&
                        it.model.equals(model, ignoreCase = true) &&
                        kotlin.math.abs(it.year - year) <= 5
            }.flatMap { it.parts }

            if (sameModel.isNotEmpty()) {
                return sameModel.distinctBy { it.name }.take(15)
            }

            // Priority 3: Same make, any model
            val sameMake = allCars.filter {
                it.make.equals(make, ignoreCase = true)
            }.flatMap { it.parts }

            if (sameMake.isNotEmpty()) {
                return sameMake.distinctBy { it.name }.take(10)
            }

            // Priority 4: Universal parts (all makes)
            return allCars.flatMap { it.parts }
                .distinctBy { it.name }
                .take(5)

        } catch (e: Exception) {
            Log.e("CarRepository", "Error getting compatible parts: ${e.message}")
            emptyList()
        }
    }
}