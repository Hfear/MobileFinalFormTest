package com.example.mobileformtest.model

data class SavedPart(
    val docId: String,
    val carId: Int,
    val carMake: String,
    val carModel: String,
    val carYear: Int,
    val name: String,
    val category: String,
    val price: Double,
    val inStock: Boolean
)
