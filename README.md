```
# 🚗 MobileFormTest — Car Catalog, Parts, and VIN Decoder (Kotlin + Jetpack Compose)

MobileFormTest is an Android app built with Kotlin and Jetpack Compose. It supports browsing a shared car catalog, viewing compatible parts, decoding vehicles from a VIN using the NHTSA VIN API, and saving vehicles to a signed-in user profile using Firebase (Auth + Firestore).

--------------------------------------------------------------------------------

## ✅ Features

- Browse a shared car catalog
- Search cars by make/model
- View car details and compatible parts
- Decode VINs using the NHTSA VIN API
- Save vehicles to a signed-in user profile (Firestore)
- Submit missing vehicle information (Firestore)
- Firebase Authentication:
  - Sign in / sign up
  - Forgot password / reset password
- Bottom navigation + screen routing from MainActivity

--------------------------------------------------------------------------------

## 📂 Project Structure

app/src/main/
│
├── assets/                          (optional local fallback data)
│
├── java/com/example/mobileformtest/
│   │
│   ├── auth/                        🔐 AUTHENTICATION
│   │   └── FirebaseAuthManager.kt   → Sign in / sign up / sign out / reset password + auth state
│   │
│   ├── model/                       📊 DATA MODELS
│   │   ├── Car.kt                   → Car shape (make/model/year/parts)
│   │   ├── CarPart.kt               → Part shape (name/category/price/stock)
│   │   └── DecodedVehicle.kt        → VIN-decoded vehicle fields
│   │
│   ├── network/                     🌐 API LAYER
│   │   └── NhtsaApiService.kt       → Retrofit service for VIN decode
│   │
│   ├── data/                        💾 DATA ACCESS LAYER
│   │   ├── CarRepository.kt         → Load/search catalog cars + lookups + compatible parts
│   │   ├── VinRepository.kt         → Calls NHTSA VIN API + parses response
│   │   ├── SavedCarsRepository.kt   → Save cars to user profile in Firestore
│   │   └── UserProfileRepository.kt → Create/initialize user profile data in Firestore
│   │
│   ├── ui/                          🎨 STATE + BUSINESS LOGIC
│   │   ├── CarViewModel.kt          → Catalog state (Loading/Success/Error), search/refresh
│   │   ├── VinViewModel.kt          → VIN decode + save/load user vehicles + submit missing info
│   │   │
│   │   └── screens/                 📱 UI SCREENS
│   │       ├── WelcomeScreen.kt        → Entry screen (guest or sign-in)
│   │       ├── HomeScreen.kt           → Catalog browsing + search
│   │       ├── CarDetailScreen.kt      → Car details + parts list + save car
│   │       ├── VinDecoderScreen.kt     → VIN input + decode + save vehicle
│   │       ├── ProfileScreen.kt        → Saved vehicles + profile actions
│   │       ├── ManualCarEntryScreen.kt → Submit missing vehicle info
│   │       ├── SignInScreen.kt         → Sign in
│   │       ├── SignUpScreen.kt         → Sign up
│   │       ├── ForgotPasswordScreen.kt → Password reset
│   │       └── AboutScreen.kt          → About page
│   │
│   └── MainActivity.kt              🏠 APP ENTRY + NAVIGATION
│
├── res/                             🎨 RESOURCES (themes, icons, strings)
└── AndroidManifest.xml              ⚙️ APP CONFIGURATION

--------------------------------------------------------------------------------

## ☁️ Firebase / Data Storage

Firestore collections used:
- cars
  Shared catalog cars loaded by CarRepository.
- users/{userId}/savedCars
  Saved vehicles tied to an authenticated user.
- user_contributions
  Manual submissions for missing vehicle information.

Authentication:
- Email/password authentication via Firebase Auth.
- Auth state is observed in MainActivity to route screens and load user data.

--------------------------------------------------------------------------------

## 🔗 How Files Connect: The Data Flow Chain (Updated)

Visual Connection Map (Catalog Flow)

┌─────────────────────────────────────────────────────────────────────────────┐
│  1. USER OPENS APP                                                          │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ MainActivity.kt (Entry Point)                                               │
│ • Launches app and applies theme                                            │
│ • Routes between screens (Welcome, Home/Search, VIN, Profile)               │
│ • Observes authentication state                                             │
│ • Loads saved vehicles on sign-in                                           │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ HomeScreen.kt (UI Layer)                                                    │
│ • Displays list of cars                                                     │
│ • Shows loading / error / results states                                    │
│ • Observes CarViewModel state                                               │
│ • Sends user actions (search, refresh, select) to ViewModel                 │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ CarViewModel.kt (Business Logic)                                            │
│ • Receives user actions (search, refresh, load)                             │
│ • Manages UI state (Loading, Success, Error)                                │
│ • Calls CarRepository to fetch data                                         │
│ • Returns updated state to HomeScreen                                       │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ CarRepository.kt (Data Access)                                              │
│ • Loads catalog cars from Firestore (cars collection)                       │
│ • Supports searching and lookups (by id or vehicle specs)                   │
│ • May use local fallback data if included                                   │
└─────────────────────────────────────────────────────────────────────────────┘

     ▲
     │
     └────────────── Data flows back up through the same chain ────────────────

HomeScreen displays the cars.

VIN Flow (High-Level)

VinDecoderScreen
→ VinViewModel.decodeVin(vin)
→ VinRepository (NHTSA VIN API)
→ VinViewModel updates UI state (Loading / Success / Error)

If decoded:
→ VinViewModel.saveVehicleToProfile(...)
→ Firestore users/{userId}/savedCars

--------------------------------------------------------------------------------

## 🔄 Complete Data Flow Example (Updated)

Scenario: App launch → browse cars → open details → back

┌─────────────────────────────────────────────────┐
│ 1. APP LAUNCH                                   │
│    MainActivity.onCreate() called               │
│    ↓                                            │
│    App UI renders                               │
│    ↓                                            │
│    WelcomeScreen shows (default)                │
└─────────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────┐
│ 2. USER CONTINUES TO HOME/SEARCH                │
│    User taps "Browse as Guest" or signs in      │
│    ↓                                            │
│    HomeScreen renders                           │
└─────────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────┐
│ 3. HOMESCREEN INITIALIZATION                    │
│    HomeScreen uses CarViewModel                 │
│    ↓                                            │
│    CarViewModel triggers getCars()              │
│    ↓                                            │
│    carUiState = Loading                         │
│    ↓                                            │
│    UI shows loading indicator                   │
└─────────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────┐
│ 4. DATA FETCHING                                │
│    CarViewModel calls CarRepository.getCars()   │
│    ↓                                            │
│    Repository loads cars from Firestore         │
│    ↓                                            │
│    Returns List<Car>                            │
└─────────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────┐
│ 5. STATE UPDATE                                 │
│    carUiState = Success(cars)                   │
│    ↓                                            │
│    HomeScreen observes state change             │
│    ↓                                            │
│    UI renders list of cars                      │
└─────────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────┐
│ 6. USER INTERACTION                             │
│    User selects a car                           │
│    ↓                                            │
│    MainActivity routes to CarDetailScreen       │
└─────────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────┐
│ 7. DETAIL VIEW                                  │
│    CarDetailScreen receives a Car object        │
│    ↓                                            │
│    Displays car info + parts list               │
│    ↓                                            │
│    (Optional) save car if signed in             │
└─────────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────┐
│ 8. NAVIGATION BACK                              │
│    User clicks back                             │
│    ↓                                            │
│    MainActivity routes back to Home             │
└─────────────────────────────────────────────────┘

--------------------------------------------------------------------------------

## 🎯 Separation of Concerns (Golden Rules)

Layer | Allowed To | NOT Allowed To
UI (Screens) | Display data, handle clicks | Call repositories directly, contain business logic
ViewModel | Business logic, state management | Depend on Compose UI or navigation details
Repository | Data access (Firestore/API), persistence | Depend on UI or ViewModels
Models | Define data structure | Contain network/database code or app flow logic

Example of WRONG

// HomeScreen.kt (Don't do this)
Button(onClick = {
    val cars = CarRepository(context).getCars() // UI calling repository directly
}) {
    Text("Load Cars")
}

Example of RIGHT

// HomeScreen.kt
Button(onClick = {
    viewModel.refreshData() // UI calls ViewModel
}) {
    Text("Refresh")
}

// CarViewModel.kt
fun refreshData() {
    getCars() // ViewModel calls repository internally
}

--------------------------------------------------------------------------------

## ▶️ Setup / Run

1. Open the project in Android Studio
2. Add Firebase to the project and include google-services.json in /app directory
3. Enable:
   - Firebase Authentication (Email/Password)
   - Cloud Firestore
4. Enable gemini-pro subscription and add into local.kts: GEMINI_API_KEY=your_api_key
5. Sync Gradle and run on an emulator or device

--------------------------------------------------------------------------------

## Notes / Limitations

- VIN decode results depend on the NHTSA API and can be incomplete.
- Firestore write access depends on security rules.
- Manual car submissions are stored in user_contributions for review.
