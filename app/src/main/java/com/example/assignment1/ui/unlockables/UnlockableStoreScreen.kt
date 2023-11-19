package com.example.assignment1.ui.unlockables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment1.PomodoroTopAppBar
import com.example.assignment1.data.unlockable.Unlockable
import com.example.assignment1.ui.AppViewModelProvider
import com.example.assignment1.ui.navigation.NavigationDestination
import com.example.assignment1.ui.visuals.MetallicContainer
import com.example.assignment1.ui.visuals.RoundMetalButton
import com.example.assignment1.ui.visuals.ShinyBlackContainer
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object UnlockableStoreDestination : NavigationDestination {
    override val route = "unlockable_store"
    override val titleRes = 8
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnlockableStoreScreen(
    viewModel: UnlockableStoreViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavController,
    navigateBack: () -> Unit
) {
    val unlockables by viewModel.unlockablesUiState.collectAsState()
    val settings by viewModel.settingsUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            PomodoroTopAppBar(
                title = "Unlockables Store",
                canNavigateBack = true,
                navigateUp = navigateBack,
                navController = navController)
        }
    ) { paddingValues ->
        StoreBody(
            unlockablesList = unlockables.unlockableList,
            paddingValues,
            debugSeedDatabase = { viewModel.seedDatabase(it) },
            debugClearDatabase = { coroutineScope.launch {
                viewModel.debugClearDatabase()}
            },
            onPurchase = {coroutineScope.launch {
                viewModel.purchaseUnlockable(it)
                }
            }
        )
    }
}

@Composable
fun StoreBody(
    unlockablesList: List<Unlockable>,
    paddingValues: PaddingValues,
    debugSeedDatabase: (List<Unlockable>) -> Unit,
    debugClearDatabase: () -> Unit,
    onPurchase: (Int) -> Unit
) {
    ShinyBlackContainer {
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            unlockablesList.map { it ->
                UnlockableObject(
                    unlockable = it,
                    onPurchase = onPurchase)
                Spacer(modifier = Modifier.height(20.dp))
            }
            Row {
                Button(
                    onClick = { debugSeedDatabase(
                        listOf(
                            Unlockable(
                                name = "Amazing prize",
                                cost = 25,
                                purchased = false
                            ),
                            Unlockable(
                                name = "Incredible prize",
                                cost = 50,
                                purchased = false
                            ),
                            Unlockable(
                                name = "Mindblowing prize",
                                cost = 100,
                                purchased = false
                            )
                        )
                    ) }
                ) {
                    Text(text = "Seed database")
                }
                Button(onClick = { debugClearDatabase() }) {
                    Text(text = "Clear database")
                }
            }

        }
    }
}

@Composable
fun UnlockableObject(
    unlockable: Unlockable,
    onPurchase: (Int) -> Unit
) {
    MetallicContainer(height = 50f, rounding = 6.dp) {
        Column(
            modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth(0.7f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = unlockable.name,
                    modifier = Modifier
                        .padding(5.dp))
                Text(
                    text = unlockable.cost.toString(),
                    modifier = Modifier
                        .padding(5.dp))
            }
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Purchased: ",
                    modifier = Modifier
                        .padding(5.dp))
                Checkbox(checked = unlockable.purchased,
                    onCheckedChange = null,
                    modifier = Modifier
                        .padding(5.dp))
            }
            Row {
                RoundMetalButton(
                    size = 30.dp,
                    onClick = { onPurchase(unlockable.id) }
                ) {
                   Icon(
                       Icons.Outlined.ShoppingCart,
                       null
                   )
                }
            }
        }
        
    }

}