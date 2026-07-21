package com.example.weather_forecast.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchHistoryRow(
    cities: List<String>,
    onCitySelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (cities.isEmpty()) return

    Column(modifier = modifier) {
        Text("Geçmiş Aramalar", fontSize = 14.sp)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cities) { cityName ->
                AssistChip(
                    onClick = { onCitySelect(cityName) },
                    label = { Text(cityName) }
                )
            }
        }
    }
}