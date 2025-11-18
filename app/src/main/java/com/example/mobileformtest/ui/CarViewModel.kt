package com.example.mobileformtest.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileformtest.data.CarRepository
import com.example.mobileformtest.model.Car
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Sealed interface for UI State
 */
sealed interface CarUiState {
    data class Success(val cars: List<Car>) : CarUiState
    object Error : CarUiState
    object Loading : CarUiState
}

/**
 * ViewModel for managing car data
 */
class CarViewModel(application: Application) : AndroidViewModel(application) {

    var carUiState: CarUiState by mutableStateOf(CarUiState.Loading)
        private set

    private val repository = CarRepository(application.applicationContext)

    init {
        getCars()
    }

    fun getCars() {
        viewModelScope.launch {
            carUiState = CarUiState.Loading

            carUiState = try {
                val cars = repository.getCars()
                CarUiState.Success(cars)
            } catch (e: IOException) {
                CarUiState.Error
            } catch (e: Exception) {
                CarUiState.Error
            }
        }
    }

    fun searchCars(query: String) {
        viewModelScope.launch {
            carUiState = CarUiState.Loading

            carUiState = try {
                val cars = repository.searchCars(query)
                CarUiState.Success(cars)
            } catch (e: IOException) {
                CarUiState.Error
            } catch (e: Exception) {
                CarUiState.Error
            }
        }
    }

    fun refreshData() {
        getCars()
    }

    suspend fun getCarById(carId: Int): Car? {
        return try {
            repository.getCarById(carId)
        } catch (e: Exception) {
            null
        }
    }
}