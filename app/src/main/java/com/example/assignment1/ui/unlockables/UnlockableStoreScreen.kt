package com.example.assignment1.ui.unlockables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assignment1.ui.AppViewModelProvider
import com.example.assignment1.ui.navigation.NavigationDestination

object UnlockableStoreDestination : NavigationDestination {
    override val route = "unlockable_store"
    override val titleRes = 8
}

@Composable
fun UnlockableStoreScreen(
    viewModel: UnlockableStoreViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val unlockablesList by viewModel.unlockablesUiState.collectAsState()

    Column {
        unlockablesList.unlockableList.map {
            Row {
                Text(it.name)
                Text(it.cost.toString())
                Text("Purchased: ")
                Checkbox(checked = it.purchased,
                    onCheckedChange = null,
                    enabled = false)
            }
        }
    }
}