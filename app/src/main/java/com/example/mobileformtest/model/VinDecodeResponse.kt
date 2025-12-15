package com.example.mobileformtest.model

import com.google.gson.annotations.SerializedName

data class VinDecodeResponse(
    @SerializedName("Results") val results: List<VinResultData>
)

data class VinResultData(
    @SerializedName("Make") val make: String?,
    @SerializedName("Model") val model: String?,
    @SerializedName("ModelYear") val modelYear: String?,
    @SerializedName("VehicleType") val vehicleType: String?,
    @SerializedName("Manufacturer") val manufacturer: String?,
    @SerializedName("PlantCountry") val plantCountry: String?,
    @SerializedName("EngineCylinders") val engineCylinders: String?
)

data class DecodedVehicle(
    val vin: String,
    val make: String,
    val model: String,
    val year: String,
    val vehicleType: String,
    val manufacturer: String,
    val plantCountry: String,
    val engineInfo: String,
    val userContributed: Map<String, String> = emptyMap()
)