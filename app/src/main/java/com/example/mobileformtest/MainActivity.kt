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
import com.example.mobileformtest.auth.FirebaseAuthManager
import com.example.mobileformtest.model.Car
import com.example.mobileformtest.ui.screens.AboutScreen
import com.example.mobileformtest.ui.screens.CarDetailScreen
import com.example.mobileformtest.ui.screens.ForgotPasswordScreen
import com.example.mobileformtest.ui.screens.HomeScreen
import com.example.mobileformtest.ui.screens.ProfileScreen
import com.example.mobileformtest.ui.screens.SignInScreen
import com.example.mobileformtest.ui.screens.SignUpScreen
import com.example.mobileformtest.ui.screens.WelcomeScreen
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

enum class Screen { HOME, SEARCH, ABOUT, PROFILE, DETAIL, SIGN_IN, SIGN_UP, FORGOT_PASSWORD }

@Composable
fun CarPartsApp(authManager: FirebaseAuthManager) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedCar by remember { mutableStateOf<Car?>(null) }
    val currentUser by authManager.authState.collectAsState()

    val showBottomBar = currentScreen !in setOf(Screen.DETAIL, Screen.SIGN_IN, Screen.SIGN_UP, Screen.FORGOT_PASSWORD)

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
                Screen.ABOUT -> AboutScreen()
                Screen.PROFILE -> ProfileScreen(
                    userEmail = currentUser?.email,
                    onSignOut = { authManager.signOut() },
                    onSignInRequest = { currentScreen = Screen.SIGN_IN }
                )
                Screen.DETAIL -> selectedCar?.let { car ->
                    CarDetailScreen(
                        car = car,
                        onBackClick = { currentScreen = Screen.SEARCH },
                        currentUserId = currentUser?.uid
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
            }
        }
    }
}