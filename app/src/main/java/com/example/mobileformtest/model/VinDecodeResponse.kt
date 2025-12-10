package com.example.mobileformtest.model

import com.google.gson.annotations.SerializedName

data class VinDecodeResponse(
    @SerializedName("Results") val results: List<VinResult>
)

data class VinResult(
    @SerializedName("Variable") val variable: String,
    @SerializedName("Value") val value: String?
)

data class DecodedVehicle(
    val vin: String,
    val make: String,
    val model: String,
    val year: String,
    val vehicleType: String,
    val manufacturer: String,
    val plantCountry: String,
    val engineInfo: String
)