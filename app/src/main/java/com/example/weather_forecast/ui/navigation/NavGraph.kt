package com.example.weather_forecast.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weather_forecast.ui.screens.DayDetailScreen
import com.example.weather_forecast.ui.screens.WeatherScreen
import com.example.weather_forecast.ui.screens.WeatherViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "weather") {
        composable("weather") {
            val parentEntry = remember(it) {
                navController.getBackStackEntry("weather")
            }
            val viewModel: WeatherViewModel = hiltViewModel(parentEntry)
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
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("weather")
            }
            val viewModel: WeatherViewModel = hiltViewModel(parentEntry)
            DayDetailScreen(
                viewModel = viewModel,
                dayIndex = dayIndex,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}