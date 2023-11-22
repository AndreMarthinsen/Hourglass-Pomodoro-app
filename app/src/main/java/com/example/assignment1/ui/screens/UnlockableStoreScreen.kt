package com.example.assignment1.ui.screens

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment1.ui.components.PomodoroTopAppBar
import com.example.assignment1.data.unlockable.Unlockable
import com.example.assignment1.utility.AppViewModelProvider
import com.example.assignment1.ui.navigation.NavigationDestination
import com.example.assignment1.ui.components.MetallicContainer
import com.example.assignment1.ui.components.RoundMetalButton
import com.example.assignment1.ui.components.ShinyBlackContainer
import com.example.assignment1.view_models.UnlockableStoreViewModel
import kotlinx.coroutines.launch


/**
 * Route for the unlockable store screen
 */
object UnlockableStoreDestination : NavigationDestination {
    override val route = "unlockable_store"
    override val titleRes = 8
}


/**
 * Screen for the unlockable store where the user can exchange currency from their
 * current balance with unlockables, such as new themes and sounds. Currently
 * just a proof of concept with no actual content unlockable.
 *
 * @param viewModel The view model for the unlockable store screen
 * @param navController Navigation controller for the screen
 * @param navigateBack Callback for when the back button is pressed
 */
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


/**
 * Body of the unlockable store screen
 *
 * @param unlockablesList List of unlockables to display
 * @param paddingValues Padding values for the body
 * @param debugSeedDatabase Callback for when the seed database button is pressed
 * @param debugClearDatabase Callback for when the clear database button is pressed
 * @param onPurchase Callback for when an unlockable is purchased
 */
@Preview
@Composable
fun StoreBody(
    unlockablesList: List<Unlockable> = exampleUnlockables,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    debugSeedDatabase: (List<Unlockable>) -> Unit = {},
    debugClearDatabase: () -> Unit = {},
    onPurchase: (Int) -> Unit = {}
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
                    onClick = { debugSeedDatabase(exampleUnlockables) }
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


/**
 * Display for a single unlockable object in the store
 *
 * @param unlockable The unlockable to display
 * @param onPurchase Callback for when the purchase button is pressed
 */
@Preview
@Composable
fun UnlockableObject(
    unlockable: Unlockable = exampleUnlockables[0],
    onPurchase: (Int) -> Unit = {}
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


// Example unlockables for testing
val exampleUnlockables = listOf(
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