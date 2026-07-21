package com.example.weather_forecast.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DayForecastUi(
    val date: String,
    val minTemp: String,
    val maxTemp: String
)

@Composable
fun DailyForecastList(
    days: List<DayForecastUi>,
    onDayClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Text("7 Günlük Tahmin", fontSize = 14.sp)
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(days.size) { index ->
            val day = days[index]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDayClick(index) }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(day.date)
                Text("${day.minTemp} / ${day.maxTemp}")
            }
        }
    }
}