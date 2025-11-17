package com.example.mobileformtest
data class Car(
    val id: Int,
    val make: String,
    val model: String,
    val year: Int,
    val imageUrl: String, // In a real app, this would be a URL or drawable resource
    val parts: List<CarPart>
)

data class CarPart(
    val name: String,
    val category: PartCategory,
    val price: Double,
    val inStock: Boolean
)

enum class PartCategory {
    ENGINE,
    TRANSMISSION,
    BRAKES,
    WHEELS,
    DRIVE_TRAIN,
    EXTERIOR,
    INTERIOR
}

// Mock Database Object
object CarDatabase {

    // Sample parts for different categories
    private val hondaParts = listOf(
        CarPart("Engine Block", PartCategory.ENGINE, 2500.00, true),
        CarPart("Cylinder Head", PartCategory.ENGINE, 800.00, true),
        CarPart("Oil Pan", PartCategory.ENGINE, 150.00, true),
        CarPart("Transmission Assembly", PartCategory.TRANSMISSION, 1800.00, true),
        CarPart("Clutch Kit", PartCategory.TRANSMISSION, 350.00, false),
        CarPart("Brake Rotors (Set)", PartCategory.BRAKES, 180.00, true),
        CarPart("Brake Pads (Set)", PartCategory.BRAKES, 85.00, true),
        CarPart("Alloy Wheels (Set of 4)", PartCategory.WHEELS, 1200.00, true),
        CarPart("Drive Shaft", PartCategory.DRIVE_TRAIN, 450.00, true),
        CarPart("Front Bumper", PartCategory.EXTERIOR, 320.00, true)
    )

    private val toyotaParts = listOf(
        CarPart("V6 Engine", PartCategory.ENGINE, 3200.00, true),
        CarPart("Turbocharger", PartCategory.ENGINE, 1100.00, false),
        CarPart("Automatic Transmission", PartCategory.TRANSMISSION, 2100.00, true),
        CarPart("Disc Brake Set", PartCategory.BRAKES, 220.00, true),
        CarPart("Steel Wheels (Set of 4)", PartCategory.WHEELS, 800.00, true),
        CarPart("Axle Assembly", PartCategory.DRIVE_TRAIN, 550.00, true),
        CarPart("Hood", PartCategory.EXTERIOR, 280.00, true),
        CarPart("Dashboard", PartCategory.INTERIOR, 450.00, false)
    )

    private val fordParts = listOf(
        CarPart("V8 Engine", PartCategory.ENGINE, 4500.00, true),
        CarPart("Performance Exhaust", PartCategory.ENGINE, 650.00, true),
        CarPart("Manual Transmission", PartCategory.TRANSMISSION, 1650.00, false),
        CarPart("Performance Brake Kit", PartCategory.BRAKES, 380.00, true),
        CarPart("Racing Wheels (Set of 4)", PartCategory.WHEELS, 1800.00, true),
        CarPart("Limited Slip Differential", PartCategory.DRIVE_TRAIN, 750.00, true),
        CarPart("Rear Spoiler", PartCategory.EXTERIOR, 420.00, true),
        CarPart("Leather Seats (Set)", PartCategory.INTERIOR, 980.00, true)
    )

    private val chevyParts = listOf(
        CarPart("Small Block V8", PartCategory.ENGINE, 3800.00, true),
        CarPart("Supercharger", PartCategory.ENGINE, 2200.00, false),
        CarPart("4-Speed Transmission", PartCategory.TRANSMISSION, 1400.00, true),
        CarPart("Drum Brakes (Set)", PartCategory.BRAKES, 160.00, true),
        CarPart("Chrome Wheels (Set of 4)", PartCategory.WHEELS, 1500.00, true),
        CarPart("Rear Axle", PartCategory.DRIVE_TRAIN, 680.00, true)
    )

    // Mock car catalog
    val cars = listOf(
        Car(
            id = 1,
            make = "Honda",
            model = "Civic",
            year = 2022,
            imageUrl = "honda_civic_2022",
            parts = hondaParts
        ),
        Car(
            id = 2,
            make = "Honda",
            model = "Accord",
            year = 2023,
            imageUrl = "honda_accord_2023",
            parts = hondaParts.shuffled().take(8)
        ),
        Car(
            id = 3,
            make = "Toyota",
            model = "Camry",
            year = 2021,
            imageUrl = "toyota_camry_2021",
            parts = toyotaParts
        ),
        Car(
            id = 4,
            make = "Toyota",
            model = "Corolla",
            year = 2023,
            imageUrl = "toyota_corolla_2023",
            parts = toyotaParts.shuffled().take(7)
        ),
        Car(
            id = 5,
            make = "Ford",
            model = "Mustang",
            year = 2022,
            imageUrl = "ford_mustang_2022",
            parts = fordParts
        ),
        Car(
            id = 6,
            make = "Ford",
            model = "F-150",
            year = 2023,
            imageUrl = "ford_f150_2023",
            parts = fordParts.shuffled().take(9)
        ),
        Car(
            id = 7,
            make = "Chevrolet",
            model = "Camaro",
            year = 2021,
            imageUrl = "chevy_camaro_2021",
            parts = chevyParts
        ),
        Car(
            id = 8,
            make = "Chevrolet",
            model = "Silverado",
            year = 2022,
            imageUrl = "chevy_silverado_2022",
            parts = chevyParts.shuffled().take(5)
        )
    )

    // API-like functions to simulate database operations
    fun getAllCars(): List<Car> = cars

    fun getCarById(id: Int): Car? = cars.find { it.id == id }

    fun getCarsByMake(make: String): List<Car> =
        cars.filter { it.make.equals(make, ignoreCase = true) }

    fun getCarsByYear(year: Int): List<Car> =
        cars.filter { it.year == year }

    fun searchCars(query: String): List<Car> =
        cars.filter {
            it.make.contains(query, ignoreCase = true) ||
                    it.model.contains(query, ignoreCase = true)
        }

    fun getPartsByCategory(carId: Int, category: PartCategory): List<CarPart>? =
        getCarById(carId)?.parts?.filter { it.category == category }

    fun getInStockParts(carId: Int): List<CarPart>? =
        getCarById(carId)?.parts?.filter { it.inStock }
}