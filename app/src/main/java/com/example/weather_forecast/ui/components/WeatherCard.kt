package com.example.weather_forecast.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WeatherCard(
    cityName: String,
    temperature: Double,
    humidity: Int,
    windSpeed: Double,
    description: String,
    unit: String = "°C",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = cityName)
            Text(text = "$temperature$unit")
            Text(text = description)
            Text(text = "Nem: %$humidity")
            Text(text = "Rüzgar: $windSpeed km/s")
        }
    }
}