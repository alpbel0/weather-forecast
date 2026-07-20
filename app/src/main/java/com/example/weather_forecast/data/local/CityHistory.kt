package com.example.weather_forecast.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city_history")
data class CityHistory(
    @PrimaryKey
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val searchedAt: Long
)