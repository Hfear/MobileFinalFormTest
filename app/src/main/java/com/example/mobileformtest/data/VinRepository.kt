package com.example.mobileformtest.data

import com.example.mobileformtest.model.DecodedVehicle
import com.example.mobileformtest.network.NhtsaApi

class VinRepository {
    suspend fun decodeVin(vin: String): DecodedVehicle? {
        return try {
            val response = NhtsaApi.retrofitService.decodeVin(vin)
            val results = response.results.firstOrNull() ?: return null

            // Parse the results into a map
            val dataMap = response.results.associate {
                it.variable to (it.value ?: "N/A")
            }

            DecodedVehicle(
                vin = vin,
                make = dataMap["Make"] ?: "Unknown",
                model = dataMap["Model"] ?: "Unknown",
                year = dataMap["Model Year"] ?: "Unknown",
                vehicleType = dataMap["Vehicle Type"] ?: "Unknown",
                manufacturer = dataMap["Manufacturer Name"] ?: "Unknown",
                plantCountry = dataMap["Plant Country"] ?: "Unknown",
                engineInfo = dataMap["Engine Number of Cylinders"] ?: "Unknown"
            )
        } catch (e: Exception) {
            null
        }
    }
}