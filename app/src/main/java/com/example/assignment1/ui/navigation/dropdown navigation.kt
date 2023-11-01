package com.example.assignment1.ui.navigation

import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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


@Composable
fun DropDownNavigation (
    navController: NavController,
    expanded: Boolean,
    currentRoute: String,
    onDismissRequest: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onDismissRequest() }
    ) {
        if(currentRoute != PresetsDestination.route) {
            Button(
                onClick = {
                    navController.navigate( PresetsDestination.route )
                }
            ) {
                Text("Presets")
            }
        }
        if(currentRoute != ActivePresetDestination.route) {
            Button(
                onClick = {
                    navController.navigate( ActivePresetDestination.route )
                }
            ) {
                Text("Timer")
            }
        }

    }
}