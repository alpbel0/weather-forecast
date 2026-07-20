package com.example.weather_forecast.data.network

fun mapWeatherCode(code: Int): String {
    return when (code) {
        0 -> "Açık"
        1, 2 -> "Az Bulutlu"
        3 -> "Kapalı"
        45, 48 -> "Sisli"
        51, 53, 55 -> "Çisenti"
        61, 63, 65 -> "Yağmurlu"
        71, 73, 75 -> "Karlı"
        80, 81, 82 -> "Sağanak Yağmur"
        95 -> "Fırtınalı"
        else -> "Bilinmiyor"
    }
}