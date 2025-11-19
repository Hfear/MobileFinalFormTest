# MobileFinalFormTest

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [File Documentation](#file-documentation)
- [Setup Instructions](#setup-instructions)
- [Architecture](#architecture)
- [Data Flow](#data-flow)
- [Future Enhancements](#future-enhancements)

---

## 🎯 Overview

This Android app allows users to browse a catalog of cars and their available parts. It currently uses a local JSON file as a mock database, with the architecture designed to easily switch to a real API when ready.

**Key Highlights:**
- ✅ Modern Android development with Jetpack Compose
- ✅ MVVM architecture pattern
- ✅ kotlinx-serialization for JSON parsing
- ✅ Proper state management with sealed interfaces
- ✅ Exception handling and loading states
- ✅ Clean separation of concerns
- ✅ Ready for API integration

---

## ✨ Features

### 🏠 Home Screen
- **Car List:** Browse 10 cars from 6 manufacturers
- **Search:** Filter cars by make or model
- **Real-time Results:** See car count update as you search
- **Loading State:** Spinner shown while fetching data
- **Error Handling:** Retry button if data fails to load
- **Refresh:** Pull to refresh data

### 🚙 Car Detail Screen
- **Vehicle Info:** Make, model, year, parts count
- **Parts List:** View all available parts with pricing
- **Stock Status:** Visual indicators (green = in stock, orange = out of stock)
- **Category Filtering:** Filter parts by Engine, Brakes, Wheels, Transmission, etc.
- **Price Display:** Formatted prices ($XXX.XX)
- **Navigation:** Back button to return to list

---

## 🛠️ Tech Stack

### Core Technologies
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Architecture:** MVVM (Model-View-ViewModel)
- **Async Operations:** Kotlin Coroutines
- **JSON Parsing:** kotlinx-serialization
- **State Management:** Compose State / StateFlow
- **Dependency Injection:** Manual (ready for Hilt/Dagger)

### Key Libraries
```kotlin
// Jetpack Compose
implementation("androidx.compose:compose-bom:2024.02.00")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.material:material-icons-extended")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

// Serialization
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

// Future: Retrofit for real API
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
```

---

## 📂 Project Structure

```
mobileFormTest/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── assets/
│   │       │   └── cars_data.json              # Mock database (10 cars)
│   │       │
│   │       ├── java/com/example/mobileformtest/
│   │       │   │
│   │       │   ├── model/                      # DATA MODELS
│   │       │   │   └── Car.kt                  # Car, CarPart, PartCategory
│   │       │   │
│   │       │   ├── network/                    # API SERVICE (future)
│   │       │   │   └── CarApiService.kt        # Retrofit interface
│   │       │   │
│   │       │   ├── data/                       # REPOSITORY LAYER
│   │       │   │   └── CarRepository.kt        # Data access logic
│   │       │   │
│   │       │   ├── ui/                         # UI LAYER
│   │       │   │   ├── CarViewModel.kt         # Business logic + state
│   │       │   │   │
│   │       │   │   ├── screens/                # SCREEN COMPOSABLES
│   │       │   │   │   ├── HomeScreen.kt       # Car list screen
│   │       │   │   │   └── CarDetailScreen.kt  # Car detail screen
│   │       │   │   │
│   │       │   │   └── theme/                  # MATERIAL THEME
│   │       │   │       ├── Color.kt
│   │       │   │       ├── Theme.kt
│   │       │   │       └── Type.kt
│   │       │   │
│   │       │   └── MainActivity.kt             # Entry point + navigation
│   │       │
│   │       ├── res/                            # Resources (icons, strings)
│   │       └── AndroidManifest.xml             # App configuration
│   │
│   └── build.gradle.kts                        # App-level build config
│
└── build.gradle.kts                            # Project-level build config
```

---

## 📄 File Documentation

### 📊 Data Layer

#### **cars_data.json**
- **Location:** `app/src/main/assets/`
- **Type:** JSON Data File
- **Purpose:** Mock database with 10 cars and their parts
- **Size:** ~8KB, 382 lines
- **Structure:**
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
- **Contains:**
  - 10 cars (Honda, Toyota, Ford, Chevrolet, Tesla, BMW)
  - 40+ parts across 7 categories
  - Prices ranging from $85 to $6,500
  - Mixed in-stock/out-of-stock status

#### **Car.kt**
- **Location:** `model/`
- **Type:** Data Classes + Enum
- **Purpose:** Defines data structure for cars and parts
- **Components:**
  1. **Car** - Represents a vehicle with parts list
  2. **CarPart** - Represents a single part
  3. **CarResponse** - Wrapper for JSON parsing
  4. **PartCategory** - Enum for part types
- **Key Features:**
  - Uses `@Serializable` for JSON parsing
  - Uses `@SerialName` for field mapping
  - Helper method `getCategoryEnum()` for string-to-enum conversion
- **Interacts With:** All layers (used throughout app)

#### **CarRepository.kt**
- **Location:** `data/`
- **Type:** Repository Class
- **Purpose:** Handles all data operations
- **Functions:**
  - `getCars()` - Loads all cars from JSON (500ms delay)
  - `searchCars(query)` - Filters by make/model (300ms delay)
  - `getCarById(id)` - Gets specific car (200ms delay)
  - `loadJsonFromAssets()` - Private helper to read files
- **Features:**
  - Simulates network latency for realistic UX
  - Proper exception handling (IOException)
  - Uses kotlinx-serialization for parsing
- **Future:** Easy to swap JSON reading with API calls

### 🌐 Network Layer

#### **CarApiService.kt**
- **Location:** `network/`
- **Type:** Retrofit Interface
- **Purpose:** Defines API endpoints (NOT CURRENTLY USED)
- **Status:** ⏸️ Prepared for future API integration
- **Components:**
  - Retrofit service interface
  - API endpoint definitions with `@GET` annotations
  - Singleton `CarApi` object
- **When Ready:** Update `BASE_URL` and switch Repository to use this

### 🎮 ViewModel Layer

#### **CarViewModel.kt**
- **Location:** `ui/`
- **Type:** AndroidViewModel
- **Purpose:** Manages UI state and business logic
- **Architecture:** Follows MVVM pattern from Mars Photos example
- **Key Components:**

  **1. CarUiState Sealed Interface**
  ```kotlin
  sealed interface CarUiState {
      data class Success(val cars: List<Car>) : CarUiState
      object Error : CarUiState
      object Loading : CarUiState
  }
  ```
  - Limits possible states to 3 values
  - Makes state handling type-safe
  - Pattern from Android documentation

  **2. State Property**
  ```kotlin
  var carUiState: CarUiState by mutableStateOf(CarUiState.Loading)
      private set
  ```
  - Observable by UI (Compose reactive)
  - Private setter (only ViewModel can change)
  - Survives configuration changes

  **3. Main Functions**
  - `getCars()` - Loads data, handles exceptions
  - `searchCars(query)` - Filters results
  - `refreshData()` - Reloads data
  - `getCarById(id)` - Gets specific car

- **Exception Handling:**
  ```kotlin
  carUiState = try {
      val cars = repository.getCars()
      CarUiState.Success(cars)
  } catch (e: IOException) {
      CarUiState.Error
  }
  ```

### 📱 UI Layer - Screens

#### **HomeScreen.kt**
- **Location:** `ui/screens/`
- **Type:** Composable Functions
- **Purpose:** Main screen with car list
- **Composables:**

  **1. HomeScreen** (Main)
  - TopAppBar with title + refresh button
  - Search TextField (filters as you type)
  - State-based content switching

  **2. LoadingScreen**
  - Circular progress indicator
  - "Loading cars..." text
  - Centered layout

  **3. ErrorScreen**
  - Error message display
  - Retry button
  - Friendly error handling

  **4. ResultScreen**
  - Car count display
  - LazyColumn for car list
  - Empty state handling

  **5. CarListItem**
  - Card with car info
  - Make initial as icon
  - Parts count + stock badges
  - Clickable (navigates to detail)

- **State Handling:**
  ```kotlin
  when (val state = viewModel.carUiState) {
      is CarUiState.Loading -> LoadingScreen()
      is CarUiState.Success -> ResultScreen(state.cars)
      is CarUiState.Error -> ErrorScreen()
  }
  ```

#### **CarDetailScreen.kt**
- **Location:** `ui/screens/`
- **Type:** Composable Functions
- **Purpose:** Detailed car view with parts
- **Features:**
  - Vehicle info card (make, model, year)
  - Large car image placeholder
  - Category filter chips (All, Engine, Brakes, etc.)
  - Filtered parts list
  - Stock status indicators
  - Price formatting
  - Back navigation

- **Composables:**
  - `CarDetailScreen` - Main layout
  - `InfoRow` - Key-value pair display
  - `PartCard` - Individual part display

- **Filtering Logic:**
  ```kotlin
  var selectedCategory by remember { mutableStateOf<PartCategory?>(null) }
  
  val filteredParts = if (selectedCategory != null) {
      car.parts.filter { it.getCategoryEnum() == selectedCategory }
  } else {
      car.parts
  }
  ```

### 🏠 Application Layer

#### **MainActivity.kt**
- **Location:** Root package
- **Type:** ComponentActivity
- **Purpose:** App entry point and navigation
- **Components:**

  **1. MainActivity Class**
  ```kotlin
  override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContent {
          MobileFormTestTheme {
              CarPartsApp()
          }
      }
  }
  ```

  **2. CarPartsApp** (Navigation)
  ```kotlin
  var selectedCar by remember { mutableStateOf<Car?>(null) }
  
  if (selectedCar == null) {
      HomeScreen(onCarClick = { car -> selectedCar = car })
  } else {
      CarDetailScreen(
          car = selectedCar!!,
          onBackClick = { selectedCar = null }
      )
  }
  ```
  - Simple state-based navigation
  - No Navigation Component needed
  - Clean and maintainable

### 🎨 Theme Layer

#### **Color.kt, Theme.kt, Type.kt**
- **Location:** `ui/theme/`
- **Purpose:** Material 3 theming
- **Provides:**
  - Color schemes (light/dark mode)
  - Typography definitions
  - Shape definitions
  - Applied to entire app via `MobileFormTestTheme`

### ⚙️ Configuration Files

#### **AndroidManifest.xml**
- Declares MainActivity as launcher
- Includes Internet permission (for future API)
- App metadata (name, icon)

#### **build.gradle.kts (Module: app)**
- App-level build configuration
- Dependencies declaration
- Serialization plugin
- Compose setup
- Min/Target SDK versions

#### **build.gradle.kts (Project)**
- Project-level settings
- Plugin versions
- Repository locations

---

## 🚀 Setup Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 11 or higher
- Android SDK API 34
- Minimum device: Android 7.0 (API 24)

### Installation Steps

1. **Clone/Download the project**

2. **Open in Android Studio**
   - File → Open
   - Select project folder

3. **Sync Gradle**
   - Click "Sync Now" if prompted
   - Wait for dependencies to download

4. **Add JSON file to assets**
   - Create `app/src/main/assets/` folder if missing
   - Place `cars_data.json` in assets folder
   - Verify location: `app/src/main/assets/cars_data.json`

5. **Build the project**
   - Build → Rebuild Project
   - Wait for successful build

6. **Run the app**
   - Click green play button (▶️)
   - Select emulator or physical device
   - Wait for installation

### Troubleshooting

**Issue: "Failed to load" error**
- ✅ Check `cars_data.json` is in `app/src/main/assets/`
- ✅ NOT in `app/src/androidTest/assets/`
- ✅ Clean Project and Rebuild
- ✅ Uninstall app from device and reinstall

**Issue: Build errors**
- ✅ Sync Gradle files
- ✅ Invalidate Caches (File → Invalidate Caches)
- ✅ Check dependencies in build.gradle.kts

**Issue: Emulator issues**
- ✅ Ensure emulator has 10-15 GB free disk space
- ✅ Use Pixel 5 or newer device
- ✅ Or use physical Android device

---

## 🏗️ Architecture

### MVVM Pattern

```
┌─────────────────────────────────────────────────┐
│                   View Layer                     │
│  (HomeScreen, CarDetailScreen, MainActivity)     │
│  - Displays UI                                   │
│  - Observes ViewModel state                      │
│  - Handles user interactions                     │
└──────────────────┬──────────────────────────────┘
                   │ observes state
                   │ calls functions
┌──────────────────▼──────────────────────────────┐
│                ViewModel Layer                   │
│              (CarViewModel)                      │
│  - Manages UI state (Loading/Success/Error)     │
│  - Business logic                                │
│  - Survives configuration changes                │
└──────────────────┬──────────────────────────────┘
                   │ requests data
                   │ receives results
┌──────────────────▼──────────────────────────────┐
│              Repository Layer                    │
│             (CarRepository)                      │
│  - Single source of truth                        │
│  - Data access logic                             │
│  - Future: Combines local + remote sources       │
└──────────────────┬──────────────────────────────┘
                   │ reads
                   │ parses
┌──────────────────▼──────────────────────────────┐
│                Data Source                       │
│  (cars_data.json / Future: API)                 │
│  - Raw data storage                              │
│  - JSON structure                                │
└─────────────────────────────────────────────────┘
```

### Key Principles

1. **Separation of Concerns**
   - Each layer has single responsibility
   - Easy to test independently
   - Clear data flow

2. **Unidirectional Data Flow**
   - Data flows down (ViewModel → View)
   - Events flow up (View → ViewModel)
   - State is single source of truth

3. **Dependency Rule**
   - Inner layers know nothing about outer layers
   - UI depends on ViewModel
   - ViewModel depends on Repository
   - Repository depends on Data Source

---

## 🔄 Data Flow

### App Launch Flow
```
1. User opens app
   ↓
2. MainActivity.onCreate() called
   ↓
3. CarPartsApp() renders
   ↓
4. HomeScreen shows with CarViewModel
   ↓
5. ViewModel.init { getCars() } runs automatically
   ↓
6. CarUiState = Loading → LoadingScreen shows (spinner)
   ↓
7. Repository.getCars() reads cars_data.json
   ↓
8. JSON parsed with kotlinx-serialization → List<Car>
   ↓
9. CarUiState = Success(cars) → UI updates
   ↓
10. ResultScreen shows with 10 cars
```

### Search Flow
```
1. User types in search bar
   ↓
2. onChange triggers viewModel.searchCars(query)
   ↓
3. CarUiState = Loading (brief spinner)
   ↓
4. Repository.searchCars(query) filters cars
   ↓
5. CarUiState = Success(filteredCars)
   ↓
6. UI updates with filtered results
```

### Navigation Flow
```
1. User clicks car in list
   ↓
2. onCarClick(car) callback fires
   ↓
3. MainActivity: selectedCar = car
   ↓
4. if/else triggers → CarDetailScreen shows
   ↓
5. User clicks back button
   ↓
6. onBackClick() callback fires
   ↓
7. MainActivity: selectedCar = null
   ↓
8. if/else triggers → HomeScreen shows
```

### Error Handling Flow
```
1. Exception occurs in Repository
   ↓
2. Try-catch in ViewModel catches it
   ↓
3. CarUiState = Error
   ↓
4. ErrorScreen shows with retry button
   ↓
5. User clicks retry
   ↓
6. viewModel.getCars() called again
   ↓
7. Flow restarts from step 1
```

---

## 🔮 Future Enhancements

### Ready for Implementation

#### **1. Real API Integration** 🌐
Currently prepared but not active:
```kotlin
// In CarRepository.kt, replace:
val jsonString = loadJsonFromAssets("cars_data.json")

// With:
val response = CarApi.retrofitService.getCars()
```

**Files to Change:** 1 (CarRepository.kt)  
**Files Unchanged:** 12 (all UI and ViewModel files)

#### **2. Room Database Integration** 💾
Add local caching:
- Create DAO interfaces
- Add Room dependency
- Repository becomes mediator between Room and API
- Offline-first approach

#### **3. Dependency Injection** 💉
- Add Hilt or Dagger
- Remove manual object creation
- Better testability
- Proper lifecycle management

#### **4. Navigation Component** 🧭
- Replace simple if/else navigation
- Deep linking support
- Navigation graph
- Type-safe arguments

#### **5. Image Loading** 🖼️
- Add Coil library
- Load actual car images from URLs
- Placeholder and error states
- Image caching

#### **6. Additional Features** ✨
- Shopping cart functionality
- User authentication
- Favorites/wishlist
- Part reviews and ratings
- Price comparison
- Order history
- Push notifications
- Dark mode toggle
- Language localization

### Testing Strategy

#### **Unit Tests**
- ViewModel logic
- Repository functions
- Data parsing
- State management

#### **UI Tests**
- Screen rendering
- User interactions
- Navigation flow
- State changes

#### **Integration Tests**
- End-to-end flows
- API integration
- Database operations

---

## 📊 Project Statistics

| Metric | Count |
|--------|-------|
| **Total Files** | 13 |
| **Lines of Code** | ~1,582 |
| **Composables** | 10+ |
| **Screens** | 2 |
| **Data Models** | 4 |
| **Cars in Database** | 10 |
| **Car Parts** | 40+ |
| **Part Categories** | 7 |
| **Manufacturers** | 6 |

---

## 🤝 Contributing

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable names
- Comment complex logic
- Keep functions small and focused

### Git Workflow
1. Create feature branch
2. Make changes
3. Test thoroughly
4. Submit pull request
5. Code review
6. Merge to main

---

## 📝 License

This project is created for educational purposes as part of a mobile development course.

---

## 👨‍💻 Developer Notes

### Design Decisions

**Why kotlinx-serialization over Gson?**
- Official Kotlin solution
- Better performance
- Compile-time safety
- Multiplatform support
- Recommended by Android team

**Why sealed interface for UI state?**
- Type-safe state management
- Exhaustive when expressions
- Clear state transitions
- Industry best practice
- From official Android samples

**Why MVVM architecture?**
- Recommended by Google
- Clear separation of concerns
- Testable components
- Survives configuration changes
- Industry standard

**Why simple navigation over Navigation Component?**
- Only 2 screens
- Simple flow
- Less complexity
- Easy to understand
- Can migrate later if needed

### Learning Resources

This project follows patterns from:
- [Android Developer Documentation](https://developer.android.com)
- Mars Photos sample app (from course materials)
- [Jetpack Compose Samples](https://github.com/android/compose-samples)
- Material Design 3 guidelines

---

## 🎓 Educational Purpose

This app was built as part of a Computer Science mobile development course, demonstrating:
- ✅ Professional Android development practices
- ✅ Modern UI with Jetpack Compose
- ✅ Clean architecture principles
- ✅ State management techniques
- ✅ Error handling best practices
- ✅ JSON parsing and serialization
- ✅ Coroutines and async programming
- ✅ Material Design implementation

---

## 📞 Support

For questions or issues:
1. Check troubleshooting section
2. Review Android Studio logs (Logcat)
3. Verify file locations match documentation
4. Ensure all dependencies are synced

---

## 🎉 Acknowledgments

- Android Team for Jetpack Compose
- Course instructors for Mars Photos example
- Material Design team for UI guidelines
- Kotlin team for kotlinx-serialization

---

**Built with ❤️ using Kotlin and Jetpack Compose**

*Last Updated: November 2025*
