package com.example.weather_forecast.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weather_forecast.data.network.mapWeatherCode

@Composable
fun DayDetailScreen(
    viewModel: WeatherViewModel,
    dayIndex: Int,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isCelsius by viewModel.isCelsius.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = onBackClick) {
            Text("Geri")
        }

        when (val state = uiState) {
            is WeatherUiState.Success -> {
                val daily = state.weather.daily
                val maxTemp = daily.temperatureMax[dayIndex]
                val minTemp = daily.temperatureMin[dayIndex]
                val unit = if (isCelsius) "°C" else "°F"
                val displayMax = if (isCelsius) maxTemp else maxTemp * 9 / 5 + 32
                val displayMin = if (isCelsius) minTemp else minTemp * 9 / 5 + 32

                Text(text = daily.time[dayIndex])
                Text(text = "En Yüksek: ${displayMax.toInt()}$unit")
                Text(text = "En Düşük: ${displayMin.toInt()}$unit")
                Text(text = mapWeatherCode(daily.weatherCode[dayIndex]))
            }
            else -> {
                Text("Veri bulunamadı")
            }
        }
    }
}