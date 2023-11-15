package com.example.assignment1.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment1.PomodoroTopAppBar
import com.example.assignment1.R
import com.example.assignment1.data.Settings
import com.example.assignment1.ui.AppViewModelProvider
import com.example.assignment1.ui.navigation.NavigationDestination
import com.example.assignment1.ui.visuals.MetallicContainer
import com.example.assignment1.ui.visuals.ShinyBlackContainer
import kotlinx.coroutines.launch

object SettingsDestination : NavigationDestination{
    override val route = "settings"
    override val titleRes = 4
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Scaffold(
        topBar = {
            PomodoroTopAppBar(
                title = "Settings",
                canNavigateBack = true,
                navigateUp = navigateBack,
                navController = navController
            )
        }
    ) { paddingValues ->
        ShinyBlackContainer(
            modifier = modifier,
        ) {

            SettingsBody(
                modifier = modifier,
                paddingValues = paddingValues,
                viewModel = viewModel
            )
        }
    }
}



@Composable
fun SettingsBody(
    modifier: Modifier,
    paddingValues: PaddingValues,
    viewModel: SettingsViewModel
) {
    val settings : Settings by viewModel.settingsUiState.collectAsState()
    Column(
        modifier = modifier.padding(paddingValues)
    ) {
        SettingFrame(
            enabled = settings.showCoinWarning,
            name = "Coin Warnings",
            description = "Disables warnings about actions that lead to you losing built up coins earned for a focus of break session",
            onToggle = {
                viewModel.viewModelScope.launch {
                    viewModel.setCoinWarningsEnabled(it)
                }
            }
        )
    }
}


@Composable
fun SettingFrame(
    enabled: Boolean,
    name: String,
    description: String,
    onToggle: (Boolean) -> Unit
) {
    var descriptionExpanded by remember { mutableStateOf(false) }
    MetallicContainer(height = 100f, rounding = 6.dp) {
        Column (
            modifier = Modifier.fillMaxWidth()
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = enabled,
                    onCheckedChange = onToggle
                )
                Text(name, fontSize = 26.sp)
                IconButton(
                    onClick = { descriptionExpanded = !descriptionExpanded }
                ) {
                    Icon(
                        modifier = Modifier.size(26.dp),
                        painter = painterResource(id = R.drawable.info),
                        contentDescription = "Info"
                    )
                }
            }
            if(descriptionExpanded) {
                Text(description)
            }
        }
    }
}