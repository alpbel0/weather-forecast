package com.example.weather_forecast.ui.screens

import com.example.weather_forecast.data.repository.WeatherResult

sealed interface WeatherUiState {
    data object Idle : WeatherUiState
    data object Loading : WeatherUiState
    data class Success(val weather: WeatherResult) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}