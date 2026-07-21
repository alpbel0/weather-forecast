package com.example.weather_forecast.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.weather_forecast.data.network.mapWeatherCode
import com.example.weather_forecast.location.LocationHelper
import com.example.weather_forecast.ui.components.CitySearchBar
import com.example.weather_forecast.ui.components.DailyForecastList
import com.example.weather_forecast.ui.components.DayForecastUi
import com.example.weather_forecast.ui.components.LocationButton
import com.example.weather_forecast.ui.components.SearchHistoryRow
import com.example.weather_forecast.ui.components.TemperatureUnitToggle
import com.example.weather_forecast.ui.components.WeatherCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    onDayClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing = uiState is WeatherUiState.Loading
    val history by viewModel.history.collectAsState()
    val isCelsius by viewModel.isCelsius.collectAsState()
    var searchText by remember { mutableStateOf("") }

    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }
    val coroutineScope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            coroutineScope.launch {
                viewModel.setLoading()
                try {
                    val location = locationHelper.getCurrentLocation()
                    viewModel.searchByLocation(location.first, location.second)
                } catch (e: Exception) {
                    viewModel.setError(e.message ?: "Konum alınamadı")
                }
            }
        }
    }

    fun requestLocationWeather() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            coroutineScope.launch {
                viewModel.setLoading()
                try {
                    val location = locationHelper.getCurrentLocation()
                    viewModel.searchByLocation(location.first, location.second)
                } catch (e: Exception) {
                    viewModel.setError(e.message ?: "Konum alınamadı")
                }
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TemperatureUnitToggle(
                isCelsius = isCelsius,
                onToggle = { viewModel.toggleTemperatureUnit() }
            )

            when (val state = uiState) {
                is WeatherUiState.Idle -> {
                    Text("Bir şehir arayın")
                }
                is WeatherUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is WeatherUiState.Success -> {
                    val unit = if (isCelsius) "°C" else "°F"
                    val tempCelsius = state.weather.current.temperature
                    val displayTemp = if (isCelsius) tempCelsius else celsiusToFahrenheit(tempCelsius)

                    WeatherCard(
                        cityName = state.weather.cityName,
                        temperature = displayTemp,
                        humidity = state.weather.current.humidity,
                        windSpeed = state.weather.current.windSpeed,
                        description = mapWeatherCode(state.weather.current.weatherCode),
                        unit = unit
                    )

                    val dayList = state.weather.daily.time.indices.map { index ->
                        val maxTemp = state.weather.daily.temperatureMax[index]
                        val minTemp = state.weather.daily.temperatureMin[index]
                        val displayMax = if (isCelsius) maxTemp else celsiusToFahrenheit(maxTemp)
                        val displayMin = if (isCelsius) minTemp else celsiusToFahrenheit(minTemp)

                        DayForecastUi(
                            date = state.weather.daily.time[index],
                            minTemp = "${displayMin.toInt()}$unit",
                            maxTemp = "${displayMax.toInt()}$unit"
                        )
                    }

                    DailyForecastList(
                        days = dayList,
                        onDayClick = onDayClick,
                        modifier = Modifier.weight(1f)
                    )
                }
                is WeatherUiState.Error -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Hata: ${state.message}")
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Tekrar Dene")
                        }
                    }
                }
            }

            SearchHistoryRow(
                cities = history.map { it.cityName },
                onCitySelect = { cityName ->
                    searchText = cityName
                    viewModel.searchCity(cityName)
                }
            )

            CitySearchBar(
                searchText = searchText,
                onTextChange = { searchText = it },
                onSearchClick = { viewModel.searchCity(searchText) }
            )

            LocationButton(
                onClick = { requestLocationWeather() }
            )
        }
    }
}

private fun celsiusToFahrenheit(celsius: Double): Double {
    return celsius * 9 / 5 + 32
}