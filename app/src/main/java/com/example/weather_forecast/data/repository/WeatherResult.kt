package com.example.weather_forecast.data.repository

import com.example.weather_forecast.data.network.CurrentWeather
import com.example.weather_forecast.data.network.DailyWeather

data class WeatherResult(
    val cityName: String,
    val current: CurrentWeather,
    val daily: DailyWeather
)