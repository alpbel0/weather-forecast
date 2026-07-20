package com.example.weather_forecast.data.network


import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApiService {
    @GET("v1/search")
    suspend fun searchCity(@Query("name") name: String): GeocodingResponse
}