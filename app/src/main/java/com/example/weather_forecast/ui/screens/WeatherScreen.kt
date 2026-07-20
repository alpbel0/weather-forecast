package com.example.weather_forecast.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.weather_forecast.data.network.mapWeatherCode
import com.example.weather_forecast.location.LocationHelper
import com.example.weather_forecast.ui.components.WeatherCard
import kotlinx.coroutines.launch

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    onDayClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("°C")
            Switch(
                checked = !isCelsius,
                onCheckedChange = { viewModel.toggleTemperatureUnit() }
            )
            Text("°F")
        }

        when (val state = uiState) {
            is WeatherUiState.Idle -> {
                Text("Bir şehir arayın")
            }
            is WeatherUiState.Loading -> {
                CircularProgressIndicator()
            }
            is WeatherUiState.Success -> {
                val tempCelsius = state.weather.current.temperature
                val displayTemp = if (isCelsius) tempCelsius else celsiusToFahrenheit(tempCelsius)
                val unit = if (isCelsius) "°C" else "°F"

                WeatherCard(
                    cityName = state.weather.cityName,
                    temperature = displayTemp,
                    humidity = state.weather.current.humidity,
                    windSpeed = state.weather.current.windSpeed,
                    description = mapWeatherCode(state.weather.current.weatherCode),
                    unit = unit
                )

                Text("7 Günlük Tahmin", fontSize = 14.sp)
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(state.weather.daily.time.size) { index ->
                        val maxTemp = state.weather.daily.temperatureMax[index]
                        val minTemp = state.weather.daily.temperatureMin[index]
                        val displayMax = if (isCelsius) maxTemp else celsiusToFahrenheit(maxTemp)
                        val displayMin = if (isCelsius) minTemp else celsiusToFahrenheit(minTemp)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDayClick(index) }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(state.weather.daily.time[index])
                            Text("${displayMin.toInt()}$unit / ${displayMax.toInt()}$unit")
                        }
                    }
                }
            }
            is WeatherUiState.Error -> {
                Text("Hata: ${state.message}")
            }
        }

        if (history.isNotEmpty()) {
            Text("Geçmiş Aramalar", fontSize = 14.sp)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(history) { cityHistory ->
                    AssistChip(
                        onClick = {
                            searchText = cityHistory.cityName
                            viewModel.searchCity(cityHistory.cityName)
                        },
                        label = { Text(cityHistory.cityName) }
                    )
                }
            }
        }

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Şehir ara") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.searchCity(searchText) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ara", fontSize = 18.sp)
        }

        Button(
            onClick = {
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
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Konumumu Kullan", fontSize = 18.sp)
        }
    }
}

private fun celsiusToFahrenheit(celsius: Double): Double {
    return celsius * 9 / 5 + 32
}