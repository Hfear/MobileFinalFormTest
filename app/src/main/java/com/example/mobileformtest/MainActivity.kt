package com.example.mobileformtest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarPartsTheme {
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
        // Show HomePage
        HomePage(
            onCarClick = { car ->
                selectedCar = car
            }
        )
    } else {
        // Show CarDetailPage
        CarDetailPage(
            car = selectedCar!!,
            onBackClick = {
                selectedCar = null
            }
        )
    }
}

@Composable
fun CarPartsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}