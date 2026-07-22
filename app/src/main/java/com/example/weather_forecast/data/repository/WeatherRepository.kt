package com.example.weather_forecast.data.repository

import com.example.weather_forecast.data.local.CityHistory
import com.example.weather_forecast.data.local.CityHistoryDao
import com.example.weather_forecast.data.network.ForecastApiService
import com.example.weather_forecast.data.network.GeocodingApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val geocodingApi: GeocodingApiService,
    private val forecastApi: ForecastApiService,
    private val cityHistoryDao: CityHistoryDao
) {

    val history: Flow<List<CityHistory>> = cityHistoryDao.getAllHistory()

    suspend fun getWeatherForCity(cityName: String): Result<WeatherResult> {
        return try {
            val geocodingResponse = geocodingApi.searchCity(cityName)
            val coordinate = geocodingResponse.results?.firstOrNull()
                ?: return Result.failure(Exception("Şehir bulunamadı"))

            val forecastResponse = forecastApi.getForecast(
                latitude = coordinate.latitude,
                longitude = coordinate.longitude
            )

            cityHistoryDao.insert(
                CityHistory(
                    cityName = coordinate.name,
                    latitude = coordinate.latitude,
                    longitude = coordinate.longitude,
                    searchedAt = System.currentTimeMillis()
                )
            )

            Result.success(
                WeatherResult(
                    cityName = coordinate.name,
                    current = forecastResponse.current,
                    daily = forecastResponse.daily
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWeatherForCoordinates(latitude: Double, longitude: Double): Result<WeatherResult> {
        return try {
            val forecastResponse = forecastApi.getForecast(
                latitude = latitude,
                longitude = longitude
            )

            Result.success(
                WeatherResult(
                    cityName = "Konumunuz",
                    current = forecastResponse.current,
                    daily = forecastResponse.daily
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLastSearchedCity(): CityHistory? {
        return cityHistoryDao.getLastSearched()
    }
}