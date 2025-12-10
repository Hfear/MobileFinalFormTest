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
import com.example.mobileformtest.model.Car
import com.example.mobileformtest.ui.screens.AboutScreen
import com.example.mobileformtest.ui.screens.CarDetailScreen
import com.example.mobileformtest.ui.screens.HomeScreen
import com.example.mobileformtest.ui.screens.ProfileScreen
import com.example.mobileformtest.ui.screens.WelcomeScreen
import com.example.mobileformtest.ui.theme.MobileFormTestTheme

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

enum class Screen { HOME, SEARCH, ABOUT, PROFILE, DETAIL }

@Composable
fun CarPartsApp() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedCar by remember { mutableStateOf<Car?>(null) }

    Scaffold(
        bottomBar = {
            if (currentScreen != Screen.DETAIL) {
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
                    onNavigateToSearch = { currentScreen = Screen.SEARCH }
                )
                Screen.SEARCH -> HomeScreen(
                    onCarClick = { car ->
                        selectedCar = car
                        currentScreen = Screen.DETAIL
                    }
                )
                Screen.ABOUT -> AboutScreen()
                Screen.PROFILE -> ProfileScreen()
                Screen.DETAIL -> selectedCar?.let { car ->
                    CarDetailScreen(
                        car = car,
                        onBackClick = { currentScreen = Screen.SEARCH }
                    )
                }
            }
        }
    }
}