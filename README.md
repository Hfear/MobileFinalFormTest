```md
# 🚗 MobileFormTest — Car Catalog, Parts, and VIN Decoder (Kotlin + Jetpack Compose)

MobileFormTest is an Android app built with **Kotlin** and **Jetpack Compose**. It supports browsing a car catalog, viewing compatible parts, decoding vehicles from a VIN using the **NHTSA VIN API**, and saving vehicles to a signed-in user profile using **Firebase (Auth + Firestore)**.

---

## Features

- Browse a shared car catalog
- Search cars by make/model
- View car details and compatible parts
- Decode VINs via NHTSA API
- Save vehicles to a signed-in user profile (Firestore)
- Submit missing vehicle information (Firestore)
- Firebase Authentication (sign in / sign up / password reset)
- Bottom navigation + simple screen routing

---

## Project Structure

```

app/src/main/
├── assets/                         (optional local fallback data)
├── java/com/example/mobileformtest/
│   ├── auth/
│   │   └── FirebaseAuthManager.kt
│   ├── data/
│   │   ├── CarRepository.kt
│   │   ├── SavedCarsRepository.kt
│   │   ├── UserProfileRepository.kt
│   │   └── VinRepository.kt
│   ├── model/
│   │   ├── Car.kt
│   │   ├── CarPart.kt
│   │   └── DecodedVehicle.kt
│   ├── ui/
│   │   ├── CarViewModel.kt
│   │   ├── VinViewModel.kt
│   │   └── screens/
│   │       ├── WelcomeScreen.kt
│   │       ├── HomeScreen.kt
│   │       ├── CarDetailScreen.kt
│   │       ├── VinDecoderScreen.kt
│   │       ├── ProfileScreen.kt
│   │       ├── ManualCarEntryScreen.kt
│   │       ├── AboutScreen.kt
│   │       ├── SignInScreen.kt
│   │       ├── SignUpScreen.kt
│   │       └── ForgotPasswordScreen.kt
│   └── MainActivity.kt
└── res/

```

---

## Firebase / Data Storage

Firestore collections used by the app:

- `cars`  
  Shared car catalog (loaded by `CarRepository`).
- `users/{userId}/savedCars`  
  Saved vehicles tied to a signed-in user.
- `user_contributions`  
  Manual submissions for missing vehicle information.

Authentication is handled through Firebase Auth (email/password) and the current user state is observed in `MainActivity`.

---

## How Files Connect: Data Flow Chain (Updated)

This diagram focuses on the **car catalog flow** (Home/Search → Detail). The VIN flow is listed right after.

```

┌─────────────────────────────────────────────────────────────────┐
│  1. USER OPENS APP                                              │
└────────────────────┬────────────────────────────────────────────┘
↓
┌─────────────────────────────────────────────────────────────────┐
│  MainActivity.kt (Entry Point)                                  │
│  • Launches app + theme                                          │
│  • Routes between screens (Welcome, Home/Search, VIN, Profile)   │
│  • Observes auth state + loads saved vehicles on sign-in         │
└────────────────────┬────────────────────────────────────────────┘
↓
┌─────────────────────────────────────────────────────────────────┐
│  HomeScreen.kt (UI Layer)                                       │
│  • Displays car list                                             │
│  • Shows loading / error / results                               │
│  • Observes CarViewModel state                                   │
│  • Sends user actions to CarViewModel                            │
└────────────────────┬────────────────────────────────────────────┘
↓
┌─────────────────────────────────────────────────────────────────┐
│  CarViewModel.kt (Business Logic)                               │
│  • Receives: user actions (search, refresh, load)                │
│  • Manages: UI state (Loading, Success, Error)                   │
│  • Calls: CarRepository for data                                 │
│  • Returns: updated state to HomeScreen                          │
└────────────────────┬────────────────────────────────────────────┘
↓
┌─────────────────────────────────────────────────────────────────┐
│  CarRepository.kt (Data Access)                                 │
│  • Loads catalog cars from Firestore (cars collection)           │
│  • Supports searching and lookups (by id/specs)                  │
│  • May use local fallback data if included                       │
└─────────────────────────────────────────────────────────────────┘

```
 ↓ Data flows back up through the same chain ↓
```

HomeScreen displays the cars.

```

### VIN Flow 

```

VinDecoderScreen
→ VinViewModel.decodeVin(vin)
→ VinRepository (NHTSA API)
→ VinViewModel updates VinUiState (Loading / Success / Error)

If decoded:

* CarRepository may add the decoded vehicle to the shared catalog
* VinViewModel.saveVehicleToProfile saves the vehicle to:
  users/{userId}/savedCars

```

---

## Complete Data Flow Example (Updated for This App)

```

┌─────────────────────────────────────────────────┐
│ 1. APP LAUNCH                                   │
│    MainActivity.onCreate() called               │
│    ↓                                            │
│    CarPartsApp() renders                        │
│    ↓                                            │
│    WelcomeScreen shows (default)                │
└─────────────────────────────────────────────────┘
↓
┌─────────────────────────────────────────────────┐
│ 2. USER CONTINUES TO HOME/SEARCH                │
│    User taps "Browse as Guest"                  │
│    ↓                                            │
│    HomeScreen renders                           │
└─────────────────────────────────────────────────┘
↓
┌─────────────────────────────────────────────────┐
│ 3. HOMESCREEN INITIALIZATION                    │
│    HomeScreen uses CarViewModel                 │
│    ↓                                            │
│    CarViewModel.init triggers getCars()         │
│    ↓                                            │
│    carUiState = Loading                         │
│    ↓                                            │
│    UI shows loading indicator                   │
└─────────────────────────────────────────────────┘
↓
┌─────────────────────────────────────────────────┐
│ 4. DATA FETCHING                                │
│    CarViewModel calls repository.getCars()      │
│    ↓                                            │
│    CarRepository loads cars from Firestore      │
│    ↓                                            │
│    Returns List<Car> to ViewModel               │
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
│    User clicks a car card                       │
│    ↓                                            │
│    MainActivity sets selectedCar + routes DETAIL│
│    ↓                                            │
│    CarDetailScreen(selectedCar) renders         │
└─────────────────────────────────────────────────┘
↓
┌─────────────────────────────────────────────────┐
│ 7. DETAIL VIEW                                  │
│    CarDetailScreen receives a Car object        │
│    ↓                                            │
│    Displays car info + car.parts list           │
│    ↓                                            │
│    (Optional) user saves car if signed in       │
└─────────────────────────────────────────────────┘
↓
┌─────────────────────────────────────────────────┐
│ 8. NAVIGATION BACK                              │
│    User clicks back                             │
│    ↓                                            │
│    MainActivity routes back to Home/Search      │
└─────────────────────────────────────────────────┘

````

---

## Separation of Concerns (Golden Rules)

| Layer | Allowed To | NOT Allowed To |
|------|------------|----------------|
| UI (Screens) | Display data, handle clicks | Call repositories directly, contain business logic |
| ViewModel | Business logic, state management | Depend on Compose UI or navigation |
| Repository | Data access, API calls, persistence | Depend on UI or ViewModel |
| Models | Define data structure | Contain app logic or network/database code |

### Example of WRONG

```kotlin
// HomeScreen.kt (Don't do this)
Button(onClick = {
    val cars = CarRepository(context).getCars() // UI calling repository directly
}) {
    Text("Load Cars")
}
````

### Example of RIGHT

```kotlin
// HomeScreen.kt
Button(onClick = {
    viewModel.refreshData() // UI triggers ViewModel
}) {
    Text("Refresh")
}

// CarViewModel.kt
fun refreshData() {
    getCars() // ViewModel calls repository internally
}
```

---

## Setup / Run

1. Open the project in Android Studio
2. Add Firebase to the project and include `google-services.json`
3. Enable **Firebase Authentication** and **Cloud Firestore**
4. Sync Gradle and run on an emulator or device

---

## Notes / Limitations

* VIN decode results depend on the NHTSA API and may be incomplete.
* Firestore write access depends on your Firestore security rules.
* Manual car submissions are stored in `user_contributions` for review.

---

```
```
