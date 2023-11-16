package com.example.assignment1.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.assignment1.services.TimerService
import com.example.assignment1.ui.AppViewModelProvider
import com.example.assignment1.ui.preset.timer.ActivePresetDestination
import com.example.assignment1.ui.preset.timer.ActiveTimerScreen
import com.example.assignment1.ui.preset.timer.ActiveTimerViewModel
import com.example.assignment1.ui.preset.PresetEditDestination
import com.example.assignment1.ui.preset.PresetEditScreen
import com.example.assignment1.ui.preset.PresetsDestination
import com.example.assignment1.ui.preset.PresetsScreen
import com.example.assignment1.ui.settings.SettingsDestination
import com.example.assignment1.ui.settings.SettingsScreen
import com.example.assignment1.ui.unlockables.UnlockableStoreDestination
import com.example.assignment1.ui.unlockables.UnlockableStoreScreen

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
//    timerService: TimerService
    timerViewModel: ActiveTimerViewModel
) {
    NavHost(
        navController = navController,
        startDestination = ActivePresetDestination.routeWithArgs,
        modifier = modifier
    ) {
        /**
         * the navigateBack-params provide a callback for the back-button on the various screens
         * in our case it tells the navController to return to the previous item on the navstack
         */
        composable(
            route = ActivePresetDestination.routeWithArgs,
            arguments = listOf(navArgument("presetId") {
                defaultValue = -1
                type = NavType.IntType
            })
        ) { backStackEntry ->
            ActiveTimerScreen(
                navigateBack = { navController.popBackStack() },
                viewModel = timerViewModel,
                navController = navController,
                presetID = backStackEntry.arguments?.getInt("presetId")?:0
            )
        }
        composable(
            route = PresetEditDestination.routeWithArgs,
            arguments = listOf(navArgument("presetId") {type = NavType.IntType})
        ) {
            PresetEditScreen(
                navigateBack = { navController.popBackStack() },
                navController = navController,
                presetId = it.arguments?.getInt("presetId")?:0,
            )
        }
        composable(route = PresetsDestination.route) {
            PresetsScreen(
                navigateToPresetEdit = { navController.navigate("${PresetEditDestination.route}/${it}")},
                navigateToActivePreset = {navController.navigate("${ActivePresetDestination.route}/${it}")},
                navController = navController
            )
        }
        composable(route = SettingsDestination.route) {
            SettingsScreen(
                navigateBack = { navController.popBackStack() },
                navController = navController,
                viewModel = viewModel(factory = AppViewModelProvider.Factory)
                )
        }
        composable(route = UnlockableStoreDestination.route) {
            UnlockableStoreScreen(
                navigateBack = { navController.popBackStack() },
                navController = navController)
        }
    }
}