package com.example.mobileformtest.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class representing a Car from the API/JSON
 * Matches the structure in cars_data.json
 */
@Serializable
data class Car(
    @SerialName("id")
    val id: Int,

    @SerialName("make")
    val make: String,

    @SerialName("model")
    val model: String,

    @SerialName("year")
    val year: Int,

    @SerialName("imageUrl")
    val imageUrl: String,

    @SerialName("parts")
    val parts: List<CarPart>
)

/**
 * Data class representing a Car Part
 */
@Serializable
data class CarPart(
    @SerialName("name")
    val name: String,

    @SerialName("category")
    val category: String,

    @SerialName("price")
    val price: Double,

    @SerialName("inStock")
    val inStock: Boolean
) {
    /**
     * Convert string category to PartCategory enum
     */
    fun getCategoryEnum(): PartCategory {
        return when (category.uppercase()) {
            "ENGINE" -> PartCategory.ENGINE
            "TRANSMISSION" -> PartCategory.TRANSMISSION
            "BRAKES" -> PartCategory.BRAKES
            "WHEELS" -> PartCategory.WHEELS
            "DRIVE_TRAIN" -> PartCategory.DRIVE_TRAIN
            "EXTERIOR" -> PartCategory.EXTERIOR
            "INTERIOR" -> PartCategory.INTERIOR
            else -> PartCategory.ENGINE
        }
    }
}

/**
 * Response wrapper matching the JSON structure
 */
@Serializable
data class CarResponse(
    @SerialName("cars")
    val cars: List<Car>
)

/**
 * Enum for part categories
 */
enum class PartCategory {
    ENGINE,
    TRANSMISSION,
    BRAKES,
    WHEELS,
    DRIVE_TRAIN,
    EXTERIOR,
    INTERIOR
}