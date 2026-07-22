package com.example.weather_forecast.di

import android.content.Context
import androidx.room.Room
import com.example.weather_forecast.data.local.AppDatabase
import com.example.weather_forecast.data.local.CityHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "weather_database"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    @Singleton
    fun provideCityHistoryDao(database: AppDatabase): CityHistoryDao {
        return database.cityHistoryDao()
    }
}