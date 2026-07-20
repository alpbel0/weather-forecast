package com.example.weather_forecast.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForecastResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current: CurrentWeather,
    val daily: DailyWeather
)

@JsonClass(generateAdapter = true)
data class CurrentWeather(
    val time: String,
    @Json(name = "temperature_2m")
    val temperature: Double,
    @Json(name = "relative_humidity_2m")
    val humidity: Int,
    @Json(name = "wind_speed_10m")
    val windSpeed: Double,
    @Json(name = "weather_code")
    val weatherCode: Int
)

@JsonClass(generateAdapter = true)
data class DailyWeather(
    val time: List<String>,
    @Json(name = "temperature_2m_max")
    val temperatureMax: List<Double>,
    @Json(name = "temperature_2m_min")
    val temperatureMin: List<Double>,
    @Json(name = "weather_code")
    val weatherCode: List<Int>
)