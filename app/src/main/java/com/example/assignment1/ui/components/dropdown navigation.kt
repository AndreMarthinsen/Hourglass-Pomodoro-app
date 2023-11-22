package com.example.assignment1.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.assignment1.ui.screens.active_timer_screen.ActivePresetDestination
import com.example.assignment1.ui.screens.PresetsDestination
import com.example.assignment1.ui.screens.SettingsDestination


/**
 * Dropdown menu providing navigation to other screens.
 *
 * @param navController  The navController used to navigate to other screens.
 * @param expanded Whether the dropdown menu is expanded or not.
 * @param currentRoute The current route of the screen.
 * @param onDismissRequest The callback to be called when the dropdown menu is dismissed.
 */
@Composable
fun DropDownNavigation (
    navController: NavController = rememberNavController(),
    expanded: Boolean = true,
    currentRoute: String = "",
    onDismissRequest: () -> Unit = {}
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