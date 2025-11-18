package com.example.mobileformtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.mobileformtest.model.Car
import com.example.mobileformtest.ui.screens.CarDetailScreen  // CHANGED
import com.example.mobileformtest.ui.screens.HomeScreen   // CHANGED
import com.example.mobileformtest.ui.theme.MobileFormTestTheme  // CHANGED

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileFormTestTheme {  // CHANGED
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CarPartsApp()
                }
            }
        }
    }
}

@Composable
fun CarPartsApp() {
    var selectedCar by remember { mutableStateOf<Car?>(null) }

    if (selectedCar == null) {
        // Show HomeScreen
        HomeScreen(  // CHANGED
            onCarClick = { car ->
                selectedCar = car
            }
        )
    } else {
        // Show CarDetailScreen
        CarDetailScreen(  // CHANGED (rename your file first!)
            car = selectedCar!!,
            onBackClick = {
                selectedCar = null
            }
        )
    }
}