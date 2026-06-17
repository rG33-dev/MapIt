package com.example.mapit.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WeatherViewModel : ViewModel() {
    private val _state = MutableStateFlow(WeatherState(isLoading = true))
    val state: StateFlow<WeatherState> = _state.asStateFlow()

    private val api = WeatherApi.create()
    // NOTE: Using a placeholder. User should replace with real OpenWeatherMap API Key
    private val WEATHER_API_KEY = "8f36c84b8069677567e9f85c94294b0f" 

    init {
        loadWeatherInfo()
    }

    fun loadWeatherInfo() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val lat = _state.value.latitude
                val lon = _state.value.longitude
                
                val current = api.getCurrentWeather(lat, lon, WEATHER_API_KEY)
                val forecast = api.getForecast(lat, lon, WEATHER_API_KEY)

                val dailyData = forecast.list.filterIndexed { index, _ -> index % 8 == 0 }
                    .map { item ->
                        DailyForecast(
                            day = SimpleDateFormat("EEE", Locale.getDefault()).format(Date(item.dt * 1000)),
                            temp = item.main.temp.toInt().toString(),
                            condition = item.weather.firstOrNull()?.main ?: "Unknown"
                        )
                    }

                _state.update {
                    it.copy(
                        isLoading = false,
                        temp = current.main.temp.toInt().toString(),
                        current = current.weather.firstOrNull()?.main ?: "Unknown",
                        humidity = "${current.main.humidity}%",
                        wind = "${current.wind.speed} m/s",
                        locationName = current.name,
                        dailyForecast = dailyData,
                        pressure = "${current.main.pressure} hPa",
                        feelsLike = "${current.main.feels_like.toInt()}°"
                    )
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        error = "Failed to fetch weather: ${e.message}. (Check API Key)"
                    ) 
                }
            }
        }
    }

    fun updateLocation(lat: Double, lng: Double, name: String) {
        _state.update { 
            it.copy(latitude = lat, longitude = lng)
        }
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
