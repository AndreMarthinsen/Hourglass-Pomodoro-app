package com.example.assignment1.ui.preset

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment1.PomodoroTopAppBar
import com.example.assignment1.data.Preset
import com.example.assignment1.ui.AppViewModelProvider
import com.example.assignment1.ui.navigation.NavigationDestination

object PresetsDestination : NavigationDestination {
    override val route = "presets"
    override val titleRes = 3
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetsScreen(
    navigateToPresetEdit: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: PresetsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val presetsUiState by viewModel.presetsUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    
    Scaffold(
        topBar = {
            PomodoroTopAppBar(
                title = "Presets",
                canNavigateBack = false,
                scrollBehavior = scrollBehavior,
                navController = navController
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToPresetEdit,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "To Presets"
                )
            }
        }
    ) {
        innerPadding ->
        PresetsBody(
            presetsUiState.presetList,
            modifier
            .padding(innerPadding)
        )
    }
}

@Composable
fun PresetsBody(
    presetList: List<Preset>,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        PresetList(presetList = presetList)
    }
}

@Composable
private fun PresetList(
    presetList: List<Preset>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = presetList, key = {it.id}) {preset ->
            PresetDisplay(preset = preset)
        }
    }
}

@Composable
private fun PresetDisplay(
    preset: Preset,
    onExpandInteraction: ()->Unit = {},
    onStart: ()->Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Face,
                "Timer Icon"
            )
            Text(preset.name)
            IconButton(
                enabled = true,
                onClick = onExpandInteraction,
                content = {
                    Icon(
                        Icons.Filled.MoreVert,
                        "Expand options"
                    )
                }
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color.Black)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {// TODO: Clock icon
            Text("" + preset.focusLength + " / " + preset.breakLength + "")
            Text("Sessions: " + preset.totalSessions)
            IconButton(
                onClick = onStart,
                content = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon( // TODO: Size won't change with modifier
                            Icons.Filled.KeyboardArrowRight,
                            "Start Icon"
                        )
                    }
                }
            )
        }
    }
}