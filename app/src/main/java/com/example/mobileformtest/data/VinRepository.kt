package com.example.mobileformtest.data

import com.example.mobileformtest.model.DecodedVehicle
import com.example.mobileformtest.network.NhtsaApi
import android.util.Log

class VinRepository {
    suspend fun decodeVin(vin: String): DecodedVehicle? {
        return try {
            val cleanVin = vin.trim().replace(" ", "").take(17)

            Log.d("VinRepository", "Decoding VIN: $cleanVin")

            val response = NhtsaApi.retrofitService.decodeVin(cleanVin)

            if (response.results.isEmpty()) {
                Log.e("VinRepository", "Empty response")
                return null
            }

            val data = response.results[0]

            DecodedVehicle(
                vin = cleanVin,
                make = data.make?.takeIf { it.isNotBlank() } ?: "Unknown",
                model = data.model?.takeIf { it.isNotBlank() } ?: "Unknown",
                year = data.modelYear?.takeIf { it.isNotBlank() } ?: "Unknown",
                vehicleType = data.vehicleType?.takeIf { it.isNotBlank() } ?: "Unknown",
                manufacturer = data.manufacturer?.takeIf { it.isNotBlank() } ?: "Unknown",
                plantCountry = data.plantCountry?.takeIf { it.isNotBlank() } ?: "Unknown",
                engineInfo = data.engineCylinders?.takeIf { it.isNotBlank() } ?: "Unknown"
            )
        } catch (e: Exception) {
            Log.e("VinRepository", "Error decoding VIN: ${e.message}", e)
            null
        }
    }
}