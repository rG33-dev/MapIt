package com.example.mapit.weather

data class WeatherData(
    val temp: String,
    val wind: String,
    val humidity: String,
    val condition: String,
    val iconRes: Int? = null
)

data class WeatherState(
    val isLoading: Boolean = false,
    val temp: String = "",
    val current: String = "",
    val humidity: String = "",
    val wind: String = "",
    val error: String? = null,
    val dailyForecast: List<DailyForecast> = emptyList(),
    val latitude: Double = 37.7749, // Default to SF
    val longitude: Double = -122.4194,
    val locationName: String = "San Francisco",
    val pressure: String = "--",
    val feelsLike: String = "--"
)

data class DailyForecast(
    val day: String,
    val temp: String,
    val condition: String,
    val note: String = ""
)
