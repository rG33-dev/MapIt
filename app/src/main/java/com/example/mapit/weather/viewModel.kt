package com.example.mapit.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val _state = MutableStateFlow(WeatherState(isLoading = true, temp = "", current = ""))
    val state: StateFlow<WeatherState> = _state.asStateFlow()


    init {
        loadWeatherInfo()

    }

    fun loadWeatherInfo() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                delay(1000)
                val defaultData = WeatherState(
                    temp = "300",
                    current = "Sunny"
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _state.value = _state.value.copy(isLoading = false)

            }


        }
    }


}