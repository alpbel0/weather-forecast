package com.example.weather_forecast.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CityHistory::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityHistoryDao(): CityHistoryDao
}