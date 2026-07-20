package com.example.weather_forecast.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.data.local.CityHistory
import com.example.weather_forecast.data.local.UserPreferencesRepository
import com.example.weather_forecast.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState: StateFlow<WeatherUiState> = _uiState

    val history: StateFlow<List<CityHistory>> = repository.history
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val isCelsius: StateFlow<Boolean> = userPreferencesRepository.isCelsius
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    init {
        loadLastSearchedCity()
    }

    private fun loadLastSearchedCity() {
        viewModelScope.launch {
            val lastCity = repository.getLastSearchedCity()
            if (lastCity != null) {
                searchCity(lastCity.cityName)
            }
        }
    }

    fun toggleTemperatureUnit() {
        viewModelScope.launch {
            userPreferencesRepository.setIsCelsius(!isCelsius.value)
        }
    }

    fun searchCity(cityName: String) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading

            repository.getWeatherForCity(cityName)
                .onSuccess { weatherResult ->
                    _uiState.value = WeatherUiState.Success(weatherResult)
                }
                .onFailure { error ->
                    _uiState.value = WeatherUiState.Error(error.message ?: "Bilinmeyen hata")
                }
        }
    }

    fun searchByLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading

            repository.getWeatherForCoordinates(latitude, longitude)
                .onSuccess { weatherResult ->
                    _uiState.value = WeatherUiState.Success(weatherResult)
                }
                .onFailure { error ->
                    _uiState.value = WeatherUiState.Error(error.message ?: "Bilinmeyen hata")
                }
        }
    }

    fun setError(message: String) {
        _uiState.value = WeatherUiState.Error(message)
    }

    fun setLoading() {
        _uiState.value = WeatherUiState.Loading
    }
}

class WeatherViewModelFactory(
    private val repository: WeatherRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return WeatherViewModel(repository, userPreferencesRepository) as T
    }
}