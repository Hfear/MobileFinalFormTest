package com.example.mobileformtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.example.mobileformtest.auth.FirebaseAuthManager
import com.example.mobileformtest.model.Car
import com.example.mobileformtest.ui.VinViewModel
import com.example.mobileformtest.ui.screens.*
import com.example.mobileformtest.ui.theme.MobileFormTestTheme

class MainActivity : ComponentActivity() {

    private lateinit var authManager: FirebaseAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authManager = FirebaseAuthManager()
        setContent {
            MobileFormTestTheme {
                CarPartsApp(authManager = authManager)
            }
        }
    }

    override fun onDestroy() {
        authManager.dispose()
        super.onDestroy()
    }
}

enum class Screen {
    HOME, SEARCH, ABOUT, PROFILE, DETAIL,
    SIGN_IN, SIGN_UP, FORGOT_PASSWORD,
    VIN_DECODER, MANUAL_ENTRY
}

@Composable
fun CarPartsApp(authManager: FirebaseAuthManager) {
    val context = LocalContext.current
    val vinViewModel: VinViewModel = remember { VinViewModel(context) }

    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedCar by remember { mutableStateOf<Car?>(null) }
    val currentUser by authManager.authState.collectAsState()
    val scope = rememberCoroutineScope()

    // Load vehicles when user signs in
    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { userId ->
            vinViewModel.loadVehiclesFromFirebase(userId)
        }
    }

    val showBottomBar = currentScreen !in setOf(
        Screen.DETAIL, Screen.SIGN_IN, Screen.SIGN_UP, Screen.FORGOT_PASSWORD, Screen.MANUAL_ENTRY
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentScreen == Screen.HOME,
                        onClick = { currentScreen = Screen.HOME },
                        icon = { Icon(Icons.Default.Home, "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.SEARCH,
                        onClick = { currentScreen = Screen.SEARCH },
                        icon = { Icon(Icons.Default.Search, "Search") },
                        label = { Text("Search") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.VIN_DECODER,
                        onClick = { currentScreen = Screen.VIN_DECODER },
                        icon = { Icon(Icons.Default.Star, "VIN") },
                        label = { Text("VIN") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.ABOUT,
                        onClick = { currentScreen = Screen.ABOUT },
                        icon = { Icon(Icons.Default.Info, "About") },
                        label = { Text("About") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.PROFILE,
                        onClick = { currentScreen = Screen.PROFILE },
                        icon = { Icon(Icons.Default.Person, "Profile") },
                        label = { Text("Profile") }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (currentScreen) {
                Screen.HOME -> WelcomeScreen(
                    onBrowseAsGuest = { currentScreen = Screen.SEARCH },
                    onSignInRequest = { currentScreen = Screen.SIGN_IN }
                )
                Screen.SEARCH -> HomeScreen(
                    onCarClick = { car ->
                        selectedCar = car
                        currentScreen = Screen.DETAIL
                    }
                )
                Screen.VIN_DECODER -> VinDecoderScreen(
                    onVehicleSaved = { vehicle ->
                        currentScreen = Screen.PROFILE
                    },
                    viewModel = vinViewModel,
                    currentUserId = currentUser?.uid,
                    onManualEntryClick = {
                        currentScreen = Screen.MANUAL_ENTRY
                    }
                )
                Screen.ABOUT -> AboutScreen()
                Screen.PROFILE -> ProfileScreen(
                    userEmail = currentUser?.email,
                    onSignOut = {
                        vinViewModel.clearSavedVehicles()
                        authManager.signOut()
                    },
                    onSignInRequest = { currentScreen = Screen.SIGN_IN },
                    savedVehicles = vinViewModel.savedVehicles,
                    onVehicleClick = { vehicle ->
                        scope.launch {
                            val car = vinViewModel.getCarFromVehicle(vehicle)
                            selectedCar = car
                            currentScreen = Screen.DETAIL
                        }
                    },
                    onAddUnknownCar = {
                        currentScreen = Screen.MANUAL_ENTRY
                    }
                )
                Screen.DETAIL -> selectedCar?.let { car ->
                    CarDetailScreen(
                        car = car,
                        onBackClick = {
                            currentScreen = Screen.SEARCH
                            selectedCar = null
                        },
                        currentUserId = currentUser?.uid,
                        onAddMissingInfo = {
                            currentScreen = Screen.MANUAL_ENTRY
                        }
                    )
                }
                Screen.SIGN_IN -> SignInScreen(
                    authManager = authManager,
                    onBack = { currentScreen = Screen.PROFILE },
                    onSignedIn = { currentScreen = Screen.PROFILE },
                    onForgotPassword = { currentScreen = Screen.FORGOT_PASSWORD },
                    onNavigateToSignUp = { currentScreen = Screen.SIGN_UP }
                )
                Screen.SIGN_UP -> SignUpScreen(
                    authManager = authManager,
                    onBack = { currentScreen = Screen.SIGN_IN },
                    onSignedUp = { currentScreen = Screen.PROFILE }
                )
                Screen.FORGOT_PASSWORD -> ForgotPasswordScreen(
                    authManager = authManager,
                    onBack = { currentScreen = Screen.SIGN_IN }
                )
                Screen.MANUAL_ENTRY -> ManualCarEntryScreen(
                    onBackClick = { currentScreen = Screen.PROFILE },
                    onSaveClick = { updates ->
                        selectedCar?.let { car ->
                            val vehicle = vinViewModel.savedVehicles.find {
                                it.vin.hashCode() == car.id
                            }
                            vehicle?.let {
                                vinViewModel.submitMissingInfo(it, updates, currentUser?.uid)
                            }
                        }
                        currentScreen = Screen.PROFILE
                    },
                    currentUserId = currentUser?.uid
                )
            }
        }
    }
}