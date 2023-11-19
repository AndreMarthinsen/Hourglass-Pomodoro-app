package com.example.assignment1.ui.navigation

import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.assignment1.ui.preset.timer.ActivePresetDestination
import com.example.assignment1.ui.preset.PresetsDestination
import com.example.assignment1.ui.settings.SettingsDestination


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
        if((navController.currentDestination ?: "") != ActivePresetDestination.route) {
            Button(
                onClick = {
                    navController.navigate( ActivePresetDestination.routeNoPreset )
                }
            ) {
                Text("Timer")
            }
        }
        if(currentRoute != SettingsDestination.route) {
            Button(
                onClick = {
                    navController.navigate( SettingsDestination.route )
                }
            ) {
                Text("Settings")
            }
        }

    }
}