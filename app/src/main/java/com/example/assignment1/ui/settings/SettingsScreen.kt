package com.example.assignment1.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.assignment1.ui.navigation.NavigationDestination

object SettingsDestination : NavigationDestination{
    override val route = "settings"
    override val titleRes = 4
}

@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {

}