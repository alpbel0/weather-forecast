package com.example.weather_forecast.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weather_forecast.data.local.AppDatabase
import com.example.weather_forecast.data.local.UserPreferencesRepository
import com.example.weather_forecast.data.network.RetrofitInstance
import com.example.weather_forecast.data.repository.WeatherRepository
import com.example.weather_forecast.ui.screens.DayDetailScreen
import com.example.weather_forecast.ui.screens.WeatherScreen
import com.example.weather_forecast.ui.screens.WeatherViewModel
import com.example.weather_forecast.ui.screens.WeatherViewModelFactory

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val viewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(
            repository = WeatherRepository(
                geocodingApi = RetrofitInstance.geocodingApi,
                forecastApi = RetrofitInstance.forecastApi,
                cityHistoryDao = AppDatabase.getInstance(context).cityHistoryDao()
            ),
            userPreferencesRepository = UserPreferencesRepository(context)
        )
    )

    NavHost(navController = navController, startDestination = "weather") {
        composable("weather") {
            WeatherScreen(
                viewModel = viewModel,
                onDayClick = { dayIndex ->
                    navController.navigate("detail/$dayIndex")
                }
            )
        }
        composable(
            route = "detail/{dayIndex}",
            arguments = listOf(navArgument("dayIndex") { type = NavType.IntType })
        ) { backStackEntry ->
            val dayIndex = backStackEntry.arguments?.getInt("dayIndex") ?: 0
            DayDetailScreen(
                viewModel = viewModel,
                dayIndex = dayIndex,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}