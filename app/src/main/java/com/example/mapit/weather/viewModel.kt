package com.example.mapit.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val _state = MutableStateFlow(WeatherState(isLoading = true))
    val state: StateFlow<WeatherState> = _state.asStateFlow()

    init {
        loadWeatherInfo()
    }

    fun loadWeatherInfo() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1500) // Simulate API call
            _state.update {
                it.copy(
                    isLoading = false,
                    temp = "24",
                    current = "Sunny",
                    humidity = "45%",
                    wind = "12 km/h",
                    dailyForecast = listOf(
                        DailyForecast("Mon", "25", "Sunny", "Meeting at 10 AM"),
                        DailyForecast("Tue", "22", "Cloudy", ""),
                        DailyForecast("Wed", "20", "Rainy", "Take umbrella"),
                        DailyForecast("Thu", "23", "Sunny", ""),
                        DailyForecast("Fri", "26", "Sunny", "Weekend trip planning")
                    )
                )
            }
        }
    }

    fun updateLocation(lat: Double, lng: Double, name: String) {
        _state.update { 
            it.copy(
                latitude = lat,
                longitude = lng,
                locationName = name
            )
        }
        // In a real app, we'd trigger a new API call here
        loadWeatherInfo()
    }

    fun updateNote(day: String, newNote: String) {
        _state.update { currentState ->
            val updatedForecast = currentState.dailyForecast.map {
                if (it.day == day) it.copy(note = newNote) else it
            }
            currentState.copy(dailyForecast = updatedForecast)
        }
    }
}
