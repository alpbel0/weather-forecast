package com.example.weather_forecast.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CityHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: CityHistory)

    @Query("SELECT * FROM city_history ORDER BY searchedAt DESC")
    fun getAllHistory(): Flow<List<CityHistory>>

    @Query("SELECT * FROM city_history ORDER BY searchedAt DESC LIMIT 1")
    suspend fun getLastSearched(): CityHistory?
}