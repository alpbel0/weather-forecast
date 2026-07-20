package com.example.weather_forecast.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private val IS_CELSIUS_KEY = booleanPreferencesKey("is_celsius")

    val isCelsius: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_CELSIUS_KEY] ?: true
        }

    suspend fun setIsCelsius(isCelsius: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_CELSIUS_KEY] = isCelsius
        }
    }
}