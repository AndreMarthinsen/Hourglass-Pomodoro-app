package com.example.assignment1.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.example.assignment1.services.TimerService
import com.example.assignment1.ui.preset.ActivePresetDestination
import com.example.assignment1.ui.preset.ActiveTimerScreen
import com.example.assignment1.ui.preset.PresetEditDestination
import com.example.assignment1.ui.preset.PresetEditScreen
import com.example.assignment1.ui.preset.PresetsDestination
import com.example.assignment1.ui.preset.PresetsScreen
import com.example.assignment1.ui.settings.SettingsDestination
import com.example.assignment1.ui.settings.SettingsScreen

@Composable
fun PomodoroNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    timerService: TimerService
) {
    NavHost(
        navController = navController,
        startDestination = ActivePresetDestination.route,
        modifier = modifier
    ) {
        composable(route = ActivePresetDestination.route) {
            ActiveTimerScreen(
                navigateBack = { navController.popBackStack() },
                timerService = timerService
            )
        }
        composable(route = PresetEditDestination.route) {
            PresetEditScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(route = PresetsDestination.route) {
            PresetsScreen(
                navigateToPresetEdit = { navController.navigate(PresetEditDestination.route)}
            )
        }
        composable(route = SettingsDestination.route) {
            SettingsScreen(
                navigateBack = { navController.popBackStack() },
                )
        }
    }
}