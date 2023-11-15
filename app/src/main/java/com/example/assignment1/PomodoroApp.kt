package com.example.assignment1

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.assignment1.data.Settings
import com.example.assignment1.services.TimerService
import com.example.assignment1.ui.AppViewModelProvider
import com.example.assignment1.ui.navbar.NavbarViewModel
import com.example.assignment1.ui.navigation.DropDownNavigation
import com.example.assignment1.ui.navigation.PomodoroNavHost
import com.example.assignment1.ui.unlockables.UnlockableStoreDestination
import com.example.assignment1.ui.visuals.MetallicContainer

@Composable
fun PomodoroApp(
    navController: NavHostController = rememberNavController(),
    timerService: TimerService
) {
    PomodoroNavHost (
        navController = navController,
        timerService = timerService
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {},
    navController: NavController,
    viewModel: NavbarViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val unlockables : Settings by viewModel.settingsUiState.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    Column() {
        CenterAlignedTopAppBar(
            title = { Text(title) },
            modifier = modifier,
            scrollBehavior = scrollBehavior,
            navigationIcon = {
                if (canNavigateBack) {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            },
            actions = {
                CurrencyDisplay(
                    currency = unlockables.currency
                ) { navController.navigate(UnlockableStoreDestination.route) }
                IconButton(onClick = {expanded = !expanded}) {
                    Icon(imageVector = Icons.Filled.Menu,contentDescription = null)
                }
            }
        )
        DropDownNavigation(navController = navController, expanded = expanded, currentRoute = navController.currentDestination?.route.toString()) {
            expanded = !expanded
        }
    }

}

@Composable
fun CurrencyDisplay(
    currency: Int,
    navigateToStore: () -> Unit
) {
    MetallicContainer(height = 5f, rounding = 6.dp) {
        Row(
            modifier = Modifier
                .width(60.dp)
                .clickable { navigateToStore() },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.coin_svgrepo_com),
                contentDescription = "Image of coin with dollar sign",
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = "$currency",
                fontSize = 26.sp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
        }
    }
}