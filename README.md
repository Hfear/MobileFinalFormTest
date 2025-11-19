# 🚗 Car Parts Catalog - Project Template & Structure Guide

> 📌 **Purpose:** This project serves as a structural template for building the final car parts catalog app. It demonstrates proper file organization, data flow, and architectural patterns that should be replicated in the production version.


## 📂 Project Structure Template

```
app/src/main/
│
├── assets/                          📦 DATA SOURCE
│   └── cars_data.json              → Mock database (your team's API will replace this)
│
├── java/com/example/mobileformtest/
│   │
│   ├── model/                       📊 DATA MODELS
│   │   └── Car.kt                  → Defines what data looks like
│   │
│   ├── network/                     🌐 API LAYER (for future)
│   │   └── CarApiService.kt        → Ready for real API integration
│   │
│   ├── data/                        💾 DATA ACCESS LAYER
│   │   └── CarRepository.kt        → Gets data (currently from JSON, future from API)
│   │
│   ├── ui/                          🎨 UI LAYER
│   │   ├── CarViewModel.kt         → Manages state & business logic
│   │   │
│   │   └── screens/                📱 SCREEN COMPONENTS
│   │       ├── HomeScreen.kt       → List of cars
│   │       └── CarDetailScreen.kt  → Individual car details
│   │
│   └── MainActivity.kt              🏠 APP ENTRY POINT
│
├── res/                             🎨 RESOURCES (images, strings, themes)
└── AndroidManifest.xml             ⚙️ APP CONFIGURATION
```

---

## 🔗 How Files Connect: The Data Flow Chain

### **Visual Connection Map**

```
┌─────────────────────────────────────────────────────────────────┐
│  1. USER OPENS APP                                              │
└────────────────────┬────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────────┐
│  MainActivity.kt (Entry Point)                                  │
│  • Launches app                                                 │
│  • Shows HomeScreen or CarDetailScreen based on state           │
│  • Handles navigation between screens                           │
└────────────────────┬────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────────┐
│  HomeScreen.kt (UI Layer)                                       │
│  • Displays car list                                            │
│  • Shows loading spinner / error message / results              │
│  • Watches CarViewModel for state changes                       │
│  • Sends user actions to ViewModel                              │
└────────────────────┬────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────────┐
│  CarViewModel.kt (Business Logic)                               │
│  • Receives: User actions (search, refresh, load)               │
│  • Manages: UI state (Loading, Success, Error)                  │
│  • Calls: CarRepository to get data                             │
│  • Returns: Updated state to HomeScreen                         │
└────────────────────┬────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────────┐
│  CarRepository.kt (Data Access)                                 │
│  • Receives: Data requests from ViewModel                       │
│  • Reads: cars_data.json from assets folder                     │
│  • Parses: JSON into Car objects using kotlinx-serialization    │
│  • Returns: List<Car> back to ViewModel                         │
│  • Future: Will call CarApiService instead of reading JSON      │
└────────────────────┬────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────────┐
│  cars_data.json (Data Source)                                   │
│  • Contains: 10 cars with parts, prices, availability           │
│  • Structure: Matches Car.kt data model exactly                 │
│  • Purpose: Simulates API response                              │
│  • Future: Will be replaced by real API endpoint                │
└────────────────────┬────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────────┐
│  Car.kt (Data Models)                                           │
│  • Defines: Car, CarPart, CarResponse data classes              │
│  • Uses: @Serializable for JSON parsing                         │
│  • Provides: Type safety throughout the app                     │
│  • Structure: Must match JSON file and API response             │
└─────────────────────────────────────────────────────────────────┘

     ↓ Data flows back up through the same chain ↓

HomeScreen displays the cars!
```

---

## 📄 File-by-File Guide

### **CRITICAL: Read this section to understand each file's role**

---

## 1️⃣ **DATA LAYER**

### 📄 `cars_data.json`
**Location:** `app/src/main/assets/cars_data.json`

**What It Is:**
- A JSON file containing mock car data
- Simulates what your teammate's API will return

**What It Does:**
- Stores 10 cars with their parts
- Provides data structure template
- Read by CarRepository at runtime

**Structure:**
```json
{
  "cars": [
    {
      "id": 1,
      "make": "Honda",
      "model": "Civic",
      "year": 2022,
      "imageUrl": "honda_civic_2022",
      "parts": [
        {
          "name": "Engine Block",
          "category": "ENGINE",
          "price": 2500.00,
          "inStock": true
        }
      ]
    }
  ]
}
```

**Connects To:**
- ➡️ **CarRepository.kt** (reads this file)
- ➡️ **Car.kt** (structure must match data models)

**For Your Project:**
```
REPLACE THIS WITH → Real API endpoint
WHEN: Your teammate finishes the database/API
CHANGE NEEDED: Only in CarRepository.kt (one file!)
```

---

### 📄 `Car.kt`
**Location:** `app/src/main/java/com/example/mobileformtest/model/Car.kt`

**What It Is:**
- Data classes that define the shape of your data
- The "contract" between JSON and your app

**What It Contains:**

1. **Car Data Class**
   ```kotlin
   @Serializable
   data class Car(
       @SerialName("id") val id: Int,
       @SerialName("make") val make: String,
       @SerialName("model") val model: String,
       @SerialName("year") val year: Int,
       @SerialName("imageUrl") val imageUrl: String,
       @SerialName("parts") val parts: List<CarPart>
   )
   ```
   - Represents a single car
   - `@Serializable` = Can be converted from/to JSON
   - `@SerialName` = Maps JSON field names to Kotlin properties

2. **CarPart Data Class**
   ```kotlin
   @Serializable
   data class CarPart(
       @SerialName("name") val name: String,
       @SerialName("category") val category: String,
       @SerialName("price") val price: Double,
       @SerialName("inStock") val inStock: Boolean
   )
   ```
   - Represents a car part
   - Has helper method `getCategoryEnum()` to convert string to enum

3. **CarResponse Data Class**
   ```kotlin
   @Serializable
   data class CarResponse(
       @SerialName("cars") val cars: List<Car>
   )
   ```
   - Wrapper that matches JSON root structure
   - Used by Repository to parse the entire response

4. **PartCategory Enum**
   ```kotlin
   enum class PartCategory {
       ENGINE, TRANSMISSION, BRAKES, 
       WHEELS, DRIVE_TRAIN, EXTERIOR, INTERIOR
   }
   ```
   - Defines valid part categories
   - Used for filtering in UI

**Connects To:**
- ⬅️ Used by **CarRepository.kt** (parses JSON into these)
- ⬅️ Used by **CarViewModel.kt** (works with these objects)
- ⬅️ Used by **HomeScreen.kt** (displays Car data)
- ⬅️ Used by **CarDetailScreen.kt** (shows Car and CarPart details)

**For Your Project:**
```
MODIFY THIS TO MATCH → Your API's response structure
IF: API returns different field names or additional fields
EXAMPLE: If API has "manufacturer" instead of "make", update @SerialName
```

---

### 📄 `CarRepository.kt`
**Location:** `app/src/main/java/com/example/mobileformtest/data/CarRepository.kt`

**What It Is:**
- The data access layer
- Single source of truth for car data
- Abstracts away WHERE data comes from

**What It Does:**

1. **getCars()** - Main data loading function
   ```kotlin
   suspend fun getCars(): List<Car> {
       delay(500) // Simulates network delay
       val jsonString = loadJsonFromAssets("cars_data.json")
       val response = json.decodeFromString<CarResponse>(jsonString)
       return response.cars
   }
   ```
   - Reads JSON file from assets
   - Parses it into Car objects
   - Returns list of cars
   - `suspend` = Can be paused (runs in background)

2. **searchCars(query)** - Filters cars
   ```kotlin
   suspend fun searchCars(query: String): List<Car> {
       val allCars = getCars()
       return allCars.filter { car ->
           car.make.contains(query, ignoreCase = true) ||
           car.model.contains(query, ignoreCase = true)
       }
   }
   ```
   - Gets all cars
   - Filters by make or model
   - Case-insensitive search

3. **getCarById(id)** - Gets specific car
   ```kotlin
   suspend fun getCarById(carId: Int): Car? {
       val allCars = getCars()
       return allCars.find { it.id == carId }
   }
   ```
   - Finds car with matching ID
   - Returns null if not found

**Why This Design Matters:**
- ✅ ViewModel doesn't know if data is from JSON, API, or database
- ✅ Easy to swap data sources (just change this one file)
- ✅ Testable (can mock the repository)

**Connects To:**
- ⬅️ Called by **CarViewModel.kt** (requests data)
- ➡️ Reads **cars_data.json** (data source)
- ➡️ Uses **Car.kt** (converts JSON to these objects)

**For Your Project:**
```
REPLACE getCars() IMPLEMENTATION:

// Current (reads JSON):
val jsonString = loadJsonFromAssets("cars_data.json")

// Future (calls API):
val response = CarApi.retrofitService.getCars()
return response.cars

THAT'S IT! Only this file changes when switching to real API.
ViewModel and UI stay exactly the same.
```

---

## 2️⃣ **NETWORK LAYER** (Prepared but not active)

### 📄 `CarApiService.kt`
**Location:** `app/src/main/java/com/example/mobileformtest/network/CarApiService.kt`

**What It Is:**
- Retrofit API interface
- Defines how to communicate with a REST API
- **Currently not used** (prepared for future)

**What It Contains:**

```kotlin
interface CarApiService {
    @GET("cars")
    suspend fun getCars(): CarResponse
    
    // More endpoints can be added:
    // @GET("cars/{id}")
    // suspend fun getCarById(@Path("id") id: Int): Car
}

object CarApi {
    val retrofitService: CarApiService by lazy {
        retrofit.create(CarApiService::class.java)
    }
}
```

**Status:** 🟡 **PREPARED BUT NOT ACTIVE**

**Connects To:**
- 🔮 **Will connect to CarRepository.kt** (when API is ready)

**For Your Project:**
```
ACTIVATE THIS WHEN:
1. Your teammate provides API URL
2. Update BASE_URL = "https://your-api.com/"
3. In CarRepository, replace JSON reading with:
   val response = CarApi.retrofitService.getCars()

THAT'S IT! The rest of the app doesn't change.
```

---

## 3️⃣ **VIEWMODEL LAYER** (Business Logic)

### 📄 `CarViewModel.kt`
**Location:** `app/src/main/java/com/example/mobileformtest/ui/CarViewModel.kt`

**What It Is:**
- The brain of your app
- Manages UI state and business logic
- Survives screen rotations

**What It Contains:**

1. **CarUiState Sealed Interface** - Limits possible states
   ```kotlin
   sealed interface CarUiState {
       data class Success(val cars: List<Car>) : CarUiState
       object Error : CarUiState
       object Loading : CarUiState
   }
   ```
   - **Loading:** Showing spinner while fetching data
   - **Success:** Data loaded, show car list
   - **Error:** Something went wrong, show error screen
   
   **Why sealed interface?**
   - Type-safe: Compiler ensures you handle all states
   - Clear: Only 3 possible states, easy to understand
   - Pattern from Android official examples

2. **State Property** - Observable by UI
   ```kotlin
   var carUiState: CarUiState by mutableStateOf(CarUiState.Loading)
       private set
   ```
   - UI watches this value
   - When it changes, UI automatically updates
   - Private setter: Only ViewModel can change it

3. **getCars()** - Loads data with error handling
   ```kotlin
   fun getCars() {
       viewModelScope.launch {
           carUiState = CarUiState.Loading
           
           carUiState = try {
               val cars = repository.getCars()
               CarUiState.Success(cars)
           } catch (e: IOException) {
               CarUiState.Error
           }
       }
   }
   ```
   - Sets Loading state (UI shows spinner)
   - Calls repository to get data
   - If success: Set Success state with cars
   - If error: Set Error state
   - UI reacts automatically to state changes

**Why This Pattern Matters:**
- ✅ Separates business logic from UI
- ✅ Survives configuration changes (screen rotation)
- ✅ Easy to test
- ✅ Clear state management

**Connects To:**
- ⬅️ Used by **HomeScreen.kt** (observes state, calls functions)
- ➡️ Calls **CarRepository.kt** (to get data)
- ➡️ Works with **Car.kt** (manages Car objects)

**For Your Project:**
```
THIS FILE STAYS THE SAME when switching to API!
Only CarRepository changes.

ADD NEW FUNCTIONS here for new features:
- addToCart()
- toggleFavorite()
- filterByPrice()
etc.
```

---

## 4️⃣ **UI LAYER** (What Users See)

### 📄 `HomeScreen.kt`
**Location:** `app/src/main/java/com/example/mobileformtest/ui/screens/HomeScreen.kt`

**What It Is:**
- The main screen users see
- Displays list of cars
- Handles search and refresh

**What It Contains:**

1. **HomeScreen** - Main composable
   ```kotlin
   @Composable
   fun HomeScreen(
       onCarClick: (Car) -> Unit,
       viewModel: CarViewModel = viewModel()
   ) {
       // Observes viewModel.carUiState
       when (val state = viewModel.carUiState) {
           is CarUiState.Loading -> LoadingScreen()
           is CarUiState.Success -> ResultScreen(state.cars)
           is CarUiState.Error -> ErrorScreen()
       }
   }
   ```
   - **Observes** ViewModel state
   - **Shows** different screens based on state
   - **Handles** user interactions (search, click)

2. **LoadingScreen** - Shows while loading
   ```kotlin
   @Composable
   fun LoadingScreen() {
       CircularProgressIndicator()
       Text("Loading cars...")
   }
   ```
   - Shown when carUiState = Loading
   - Simple spinner + text

3. **ErrorScreen** - Shows on error
   ```kotlin
   @Composable
   fun ErrorScreen(retryAction: () -> Unit) {
       Text("Failed to load")
       Button(onClick = retryAction) {
           Text("Retry")
       }
   }
   ```
   - Shown when carUiState = Error
   - Has retry button that calls viewModel.getCars() again

4. **ResultScreen** - Shows car list
   ```kotlin
   @Composable
   fun ResultScreen(cars: List<Car>, onCarClick: (Car) -> Unit) {
       Text("${cars.size} cars found")
       LazyColumn {
           items(cars) { car ->
               CarListItem(car, onClick = { onCarClick(car) })
           }
       }
   }
   ```
   - Shown when carUiState = Success
   - Displays scrollable list of cars
   - Each car is clickable

5. **CarListItem** - Individual car card
   ```kotlin
   @Composable
   fun CarListItem(car: Car, onClick: () -> Unit) {
       Card(onClick = onClick) {
           // Shows: Make initial, Year Make, Model
           // Badges: Parts count, In-stock count
       }
   }
   ```
   - One card per car
   - Clickable, triggers navigation

**State Handling Pattern:**
```kotlin
when (carUiState) {
    Loading → Show spinner
    Success → Show car list
    Error → Show error + retry button
}
```

**Why This Pattern Matters:**
- ✅ Declarative UI: State determines what shows
- ✅ Automatic updates: State change = UI updates
- ✅ User-friendly: Always shows appropriate feedback

**Connects To:**
- ⬅️ Launched by **MainActivity.kt**
- ➡️ Uses **CarViewModel.kt** (observes state)
- ➡️ Displays **Car.kt** objects
- ➡️ Navigates to **CarDetailScreen.kt** (on car click)

**For Your Project:**
```
ADD NEW FEATURES here:
- Sort dropdown (by price, year, make)
- Filter chips (by manufacturer, price range)
- Grid view toggle
- Pull-to-refresh
- Empty state when no results

The pattern stays the same: observe state, show UI
```

---

### 📄 `CarDetailScreen.kt`
**Location:** `app/src/main/java/com/example/mobileformtest/ui/screens/CarDetailScreen.kt`

**What It Is:**
- Detail view for a single car
- Shows car info and all parts
- Has filtering by category

**What It Contains:**

1. **CarDetailScreen** - Main layout
   ```kotlin
   @Composable
   fun CarDetailScreen(
       car: Car,
       onBackClick: () -> Unit
   ) {
       var selectedCategory by remember { mutableStateOf<PartCategory?>(null) }
       
       Scaffold(topBar = { /* Back button */ }) {
           LazyColumn {
               item { /* Car image */ }
               item { /* Vehicle details card */ }
               item { /* Category filter chips */ }
               items(filteredParts) { part ->
                   PartCard(part)
               }
           }
       }
   }
   ```
   - Receives a Car object
   - Has local state for category filter
   - Shows car info + parts list

2. **Filtering Logic**
   ```kotlin
   val filteredParts = if (selectedCategory != null) {
       car.parts.filter { it.getCategoryEnum() == selectedCategory }
   } else {
       car.parts
   }
   ```
   - When category selected: show only matching parts
   - When null: show all parts

3. **PartCard** - Individual part display
   ```kotlin
   @Composable
   fun PartCard(part: CarPart) {
       Card {
           Text(part.name)  // e.g. "Engine Block"
           Badge(part.category)  // e.g. "ENGINE"
           Icon(part.inStock)  // ✅ or ⚠️
           Text("$${part.price}")  // e.g. "$2,500.00"
       }
   }
   ```
   - Shows part details
   - Color-coded stock status
   - Formatted price

**Connects To:**
- ⬅️ Launched by **MainActivity.kt** (when car selected)
- ➡️ Displays **Car.kt** and **CarPart** objects
- ⬅️ Calls onBackClick to return to HomeScreen

**For Your Project:**
```
ADD NEW FEATURES here:
- "Add to Cart" button per part
- Quantity selector
- Part images
- Compatibility check
- Related parts suggestions
- Customer reviews

Follow the same pattern: receive data, display it
```

---

## 5️⃣ **APPLICATION LAYER** (Entry Point)

### 📄 `MainActivity.kt`
**Location:** `app/src/main/java/com/example/mobileformtest/MainActivity.kt`

**What It Is:**
- App entry point
- Navigation controller
- Theme wrapper

**What It Contains:**

1. **MainActivity** - Android Activity
   ```kotlin
   class MainActivity : ComponentActivity() {
       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           setContent {
               MobileFormTestTheme {
                   CarPartsApp()
               }
           }
       }
   }
   ```
   - Entry point when app launches
   - Sets up Compose UI
   - Applies theme

2. **CarPartsApp** - Navigation logic
   ```kotlin
   @Composable
   fun CarPartsApp() {
       var selectedCar by remember { mutableStateOf<Car?>(null) }
       
       if (selectedCar == null) {
           HomeScreen(
               onCarClick = { car -> selectedCar = car }
           )
       } else {
           CarDetailScreen(
               car = selectedCar!!,
               onBackClick = { selectedCar = null }
           )
       }
   }
   ```
   - **Simple state-based navigation:**
     - `selectedCar == null` → Show HomeScreen
     - `selectedCar != null` → Show CarDetailScreen
   - **Click car** → Set selectedCar
   - **Click back** → Clear selectedCar

**Why This Pattern:**
- ✅ Simple: No Navigation Component needed for 2 screens
- ✅ Clear: Easy to understand
- ✅ Maintainable: Add more screens easily

**Connects To:**
- ➡️ Launches **HomeScreen.kt** (initial screen)
- ➡️ Launches **CarDetailScreen.kt** (after car click)
- ➡️ Passes **Car** objects between screens

**For Your Project:**
```
SCALE THIS UP when adding more screens:

Option 1: Add more if/else (for 3-4 screens)
Option 2: Switch to Navigation Component (for 5+ screens)

Example with shopping cart:
var currentScreen by remember { mutableStateOf(Screen.HOME) }

when (currentScreen) {
    Screen.HOME -> HomeScreen()
    Screen.DETAIL -> CarDetailScreen()
    Screen.CART -> ShoppingCartScreen()
    Screen.CHECKOUT -> CheckoutScreen()
}
```

---

## 🔄 Complete Data Flow Example

### **Scenario: User Opens App and Clicks a Car**

```
┌─────────────────────────────────────────────────┐
│ 1. APP LAUNCH                                   │
│    MainActivity.onCreate() called               │
│    ↓                                            │
│    CarPartsApp() renders                        │
│    ↓                                            │
│    selectedCar = null → HomeScreen shows        │
└─────────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────┐
│ 2. HOMESCREEN INITIALIZATION                    │
│    HomeScreen creates CarViewModel              │
│    ↓                                            │
│    ViewModel.init { getCars() } auto-runs       │
│    ↓                                            │
│    carUiState = Loading                         │
│    ↓                                            │
│    UI shows LoadingScreen (spinner)             │
└─────────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────┐
│ 3. DATA FETCHING                                │
│    ViewModel calls repository.getCars()         │
│    ↓                                            │
│    Repository reads cars_data.json              │
│    ↓                                            │
│    kotlinx-serialization parses JSON            │
│    ↓                                            │
│    Returns List<Car> to ViewModel               │
└─────────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────┐
│ 4. STATE UPDATE                                 │
│    ViewModel: carUiState = Success(cars)        │
│    ↓                                            │
│    HomeScreen detects state change              │
│    ↓                                            │
│    when statement switches to Success branch    │
│    ↓                                            │
│    ResultScreen shows with 10 cars              │
└─────────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────┐
│ 5. USER INTERACTION                             │
│    User clicks "2022 Honda Civic" card          │
│    ↓                                            │
│    onCarClick(car) callback fires               │
│    ↓                                            │
│    MainActivity: selectedCar = car              │
│    ↓                                            │
│    if/else evaluates to else branch             │
│    ↓                                            │
│    CarDetailScreen(car) renders                 │
└─────────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────┐
│ 6. DETAIL VIEW                                  │
│    CarDetailScreen receives car object          │
│    ↓                                            │
│    Displays car.make, car.model, car.year       │
│    ↓                                            │
│    Loops through car.parts                      │
│    ↓                                            │
│    Shows each part in PartCard                  │
└─────────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────┐
│ 7. NAVIGATION BACK                              │
│    User clicks back button                      │
│    ↓                                            │
│    onBackClick() callback fires                 │
│    ↓                                            │
│    MainActivity: selectedCar = null             │
│    ↓                                            │
│    if/else evaluates to if branch               │
│    ↓                                            │
│    HomeScreen renders again                     │
│    (State still Success, cars still cached)     │
└─────────────────────────────────────────────────┘
```

---

## 🎯 How to Use This Template for Your Final Project

### **Step 1: Keep the Structure** ✅

```
Copy this exact folder structure:
model/ → Your data classes
network/ → Your API calls
data/ → Your repository
ui/CarViewModel.kt → Your business logic
ui/screens/ → Your screen composables
MainActivity.kt → Your entry point
```

**Why:** This structure is proven, scalable, and follows Android best practices.

---

### **Step 2: Replace the Data Source** 🔄

**Current:**
```kotlin
// CarRepository.kt
val jsonString = loadJsonFromAssets("cars_data.json")
```

**Your Project:**
```kotlin
// CarRepository.kt
val response = CarApi.retrofitService.getCars()
return response.cars
```

**That's it!** The rest stays the same.

---

### **Step 3: Update Data Models** 📝

**If your API returns different fields:**

```kotlin
// Current Car.kt
@Serializable
data class Car(
    @SerialName("make") val make: String,
    @SerialName("model") val model: String
)

// Your API returns "manufacturer" and "car_model"?
@Serializable
data class Car(
    @SerialName("manufacturer") val make: String,  // Map API field to your property
    @SerialName("car_model") val model: String
)
```

**UI doesn't change!** It still uses `car.make` and `car.model`.

---

### **Step 4: Add New Features** ➕

**Want shopping cart?**

1. **Add to model:**
   ```kotlin
   // CartItem.kt (new file in model/)
   data class CartItem(
       val part: CarPart,
       val quantity: Int
   )
   ```

2. **Add to ViewModel:**
   ```kotlin
   // CarViewModel.kt
   private val _cartItems = mutableStateListOf<CartItem>()
   val cartItems: List<CartItem> = _cartItems
   
   fun addToCart(part: CarPart) {
       _cartItems.add(CartItem(part, 1))
   }
   ```

3. **Add UI:**
   ```kotlin
   // CarDetailScreen.kt - in PartCard
   Button(onClick = { viewModel.addToCart(part) }) {
       Text("Add to Cart")
   }
   
   // Create ShoppingCartScreen.kt
   @Composable
   fun ShoppingCartScreen(viewModel: CarViewModel) {
       LazyColumn {
           items(viewModel.cartItems) { item ->
               CartItemCard(item)
           }
       }
   }
   ```

4. **Add navigation:**
   ```kotlin
   // MainActivity.kt
   enum class Screen { HOME, DETAIL, CART }
   var currentScreen by remember { mutableStateOf(Screen.HOME) }
   
   when (currentScreen) {
       Screen.HOME -> HomeScreen()
       Screen.DETAIL -> CarDetailScreen()
       Screen.CART -> ShoppingCartScreen()
   }
   ```

**Follow the same pattern for ANY feature!**

---

### **Step 5: Maintain Separation of Concerns** 🎯

**Golden Rules:**

| Layer | Allowed To | NOT Allowed To |
|-------|-----------|----------------|
| **UI (Screens)** | Display data, handle clicks | Call Repository directly, business logic |
| **ViewModel** | Business logic, state management | Know about Android UI components |
| **Repository** | Data access, API calls | Know about UI or ViewModel |
| **Models** | Define data structure | Contain logic or functions |

**Example of WRONG:**
```kotlin
// HomeScreen.kt - DON'T DO THIS
Button(onClick = {
    val cars = CarRepository(context).getCars() // ❌ UI calling Repository
})
```

**Example of RIGHT:**
```kotlin
// HomeScreen.kt - DO THIS
Button(onClick = {
    viewModel.getCars() // ✅ UI calls ViewModel
})

// CarViewModel.kt
fun getCars() {
    viewModelScope.launch {
        repository.getCars() // ViewModel calls Repository
    }
}
```

---

## 🔧 Common Modifications for Final Project

### **Adding User Authentication**

1. **Add to model:**
   ```kotlin
   @Serializable
   data class User(
       val id: String,
       val email: String,
       val name: String
   )
   ```

2. **Add AuthRepository:**
   ```kotlin
   class AuthRepository {
       suspend fun login(email: String, password: String): User {
           // Call login API
       }
   }
   ```

3. **Add AuthViewModel:**
   ```kotlin
   class AuthViewModel : ViewModel() {
       sealed interface AuthState {
           object LoggedOut : AuthState
           data class LoggedIn(val user: User) : AuthState
       }
       
       var authState: AuthState by mutableStateOf(AuthState.LoggedOut)
   }
   ```

4. **Add LoginScreen:**
   ```kotlin
   @Composable
   fun LoginScreen(onLoginSuccess: () -> Unit) {
       // Login form
   }
   ```

5. **Update MainActivity:**
   ```kotlin
   if (authViewModel.authState is LoggedOut) {
       LoginScreen()
   } else {
       CarPartsApp()
   }
   ```

---

### **Adding Real-time Updates**

1. **Use Flow in Repository:**
   ```kotlin
   class CarRepository {
       fun getCarsFlow(): Flow<List<Car>> = flow {
           while (true) {
               emit(getCars())
               delay(30000) // Update every 30 seconds
           }
       }
   }
   ```

2. **Collect in ViewModel:**
   ```kotlin
   init {
       viewModelScope.launch {
           repository.getCarsFlow().collect { cars ->
               carUiState = Success(cars)
           }
       }
   }
   ```

---

### **Adding Database Caching (Room)**

1. **Add Room entity:**
   ```kotlin
   @Entity(tableName = "cars")
   data class CarEntity(
       @PrimaryKey val id: Int,
       val make: String,
       val model: String,
       // ...
   )
   ```

2. **Add DAO:**
   ```kotlin
   @Dao
   interface CarDao {
       @Query("SELECT * FROM cars")
       fun getAllCars(): Flow<List<CarEntity>>
       
       @Insert(onConflict = REPLACE)
       suspend fun insertCars(cars: List<CarEntity>)
   }
   ```

3. **Update Repository:**
   ```kotlin
   class CarRepository(private val carDao: CarDao) {
       suspend fun getCars(): List<Car> {
           // Try API first
           val apiCars = try {
               CarApi.retrofitService.getCars()
           } catch (e: Exception) {
               null
           }
           
           // If API succeeds, cache in database
           if (apiCars != null) {
               carDao.insertCars(apiCars.toEntities())
               return apiCars
           }
           
           // If API fails, return cached data
           return carDao.getAllCars().first().toCars()
       }
   }
   ```

**ViewModel and UI stay exactly the same!**

---

## ✅ Checklist for Final Project

### **Before Starting:**
- [ ] Review this entire README
- [ ] Understand each file's purpose
- [ ] Understand how files connect
- [ ] Review data flow examples
- [ ] Get API documentation from teammate

### **During Development:**
- [ ] Keep same folder structure
- [ ] One package per layer (model, network, data, ui)
- [ ] Follow naming conventions (Screen, ViewModel, Repository)
- [ ] Use sealed interfaces for UI state
- [ ] Handle loading and error states
- [ ] Test each layer independently

### **Code Review Checklist:**
- [ ] No Repository calls from UI
- [ ] No UI code in ViewModel
- [ ] All suspend functions in Repository
- [ ] All state changes in ViewModel
- [ ] Proper error handling with try-catch
- [ ] Loading states shown to user
- [ ] Back navigation works
- [ ] No hardcoded strings (use resources)

### **Testing Strategy:**
- [ ] Unit test ViewModel logic
- [ ] Unit test Repository functions
- [ ] UI test user flows
- [ ] Test error scenarios
- [ ] Test loading states
- [ ] Test navigation

---

## 📚 Key Takeaways

### **1. Separation of Concerns**
```
UI → Displays data, handles clicks
ViewModel → Business logic, state management
Repository → Data access
Model → Data structure
```

### **2. Single Source of Truth**
```
ViewModel holds the state
UI observes the state
State changes → UI updates automatically
```

### **3. Unidirectional Data Flow**
```
User Action → ViewModel → Repository → Data Source
Data Source → Repository → ViewModel → UI Update
```

### **4. Easy to Scale**
```
Add new screen → Create new Composable in ui/screens/
Add new feature → Add function to ViewModel
Add new data source → Modify Repository only
Add new data type → Add to model/ package
```

### **5. Ready for Production**
```
Swap JSON → API: Change 1 file (Repository)
Add database: Repository handles both sources
Add auth: Add AuthViewModel + LoginScreen
Scale to 100 screens: Same architecture works
```

---

## 🎓 Final Notes

**This is NOT just a demo.** This is a production-ready architecture that:
- ✅ Scales from 2 screens to 100+ screens
- ✅ Works with any data source (JSON, API, Database, Firebase)
- ✅ Handles real-world scenarios (loading, errors, empty states)
- ✅ Follows Android best practices
- ✅ Used by professional Android developers

**When building your final project:**
1. **Copy this structure exactly**
2. **Replace data source** (JSON → API)
3. **Add your features** (following same patterns)
4. **Keep the architecture** (it's already correct!)

**The architecture is done. You just need to fill in your specific features.**

---

## 📞 Questions to Ask Yourself

Before modifying this template, ask:

1. **"Which layer does this belong to?"**
   - Data structure? → model/
   - API call? → network/
   - Data logic? → data/
   - Business logic? → ViewModel
   - UI? → screens/

2. **"Who should know about this?"**
   - Everyone? → model/
   - Just data layer? → Repository
   - Just this screen? → Local state in Composable

3. **"What if this data source changes?"**
   - Will I have to rewrite my entire app? ❌
   - Or just change Repository? ✅

4. **"Can I test this independently?"**
   - Can I test ViewModel without UI? ✅
   - Can I test Repository without API? ✅
   - Can I test UI without real data? ✅

*This template demonstrates professional Android architecture - use it as your foundation!*
