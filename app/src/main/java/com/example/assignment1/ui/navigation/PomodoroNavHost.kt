package com.example.assignment1.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.example.assignment1.services.TimerService
import com.example.assignment1.ui.preset.ActivePresetDestination
import com.example.assignment1.ui.preset.ActiveTimerScreen
import com.example.assignment1.ui.preset.ActiveTimerViewModel
import com.example.assignment1.ui.preset.PresetEditDestination
import com.example.assignment1.ui.preset.PresetEditScreen
import com.example.assignment1.ui.preset.PresetsDestination
import com.example.assignment1.ui.preset.PresetsScreen
import com.example.assignment1.ui.settings.SettingsDestination
import com.example.assignment1.ui.settings.SettingsScreen

/**
 * The application's mavhost, describing and facilitating navigation
 * Every navigation destination (every distinct screen) is listed here
 *
 * @param navController - the navigation controller, tracks navigation state
 * @param modifier - standard modifier-object
 * @param timerService - object used for time-tracking in ActivePresetScreen, see TimerService.kt
 */
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
        /**
         * the navigateBack-params provide a callback for the back-button on the various screens
         * in our case it tells the navController to return to the previous item on the navstack
         */
        composable(route = ActivePresetDestination.route) {
            val parentEntry = remember(it){
                navController.getBackStackEntry(ActivePresetDestination.route)
            }
            val parentViewModel = viewModel<ActiveTimerViewModel>(parentEntry)
            parentViewModel.timerService = timerService
            ActiveTimerScreen(
                navigateBack = { navController.navigate(PresetsDestination.route) },
                viewModel = parentViewModel
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