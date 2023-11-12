package com.example.assignment1.ui.preset

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object PresetsDestination : NavigationDestination {
    override val route = "presets"
    override val titleRes = 3
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetsScreen(
    navigateToPresetEdit: (Int) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: PresetsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val presetsUiState by viewModel.presetsUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
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
                onClick = { navigateToPresetEdit(0) },
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
            presetList = presetsUiState.presetList,
            modifier = modifier
            .padding(innerPadding),
            onEdit = { navigateToPresetEdit(it) },
            onDelete = {
                coroutineScope.launch {
                    viewModel.deletePreset(it)
                }
            }
        )
    }
}

@Composable
fun PresetsBody(
    presetList: List<Preset>,
    modifier: Modifier = Modifier,
    onDelete: (Int) -> Unit,
    onEdit: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        PresetList(
            presetList = presetList,
            onDelete = onDelete,
            onEdit = onEdit)
    }
}

@Composable
private fun PresetList(
    presetList: List<Preset>,
    modifier: Modifier = Modifier,
    onDelete: (Int) -> Unit,
    onEdit: (Int) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(items = presetList, key = {it.id}) {preset ->
            PresetDisplay(
                preset = preset,
                onDelete = onDelete,
                onEdit = onEdit)
        }
    }
}

@Composable
private fun PresetDisplay(
    preset: Preset,
    onStart: ()->Unit = {},
    modifier: Modifier = Modifier,
    onDelete: (Int) -> Unit,
    onEdit: (Int) -> Unit
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

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
            Box {
                IconButton(
                    enabled = true,
                    onClick = {isExpanded = !isExpanded},
                    content = {
                        Icon(
                            Icons.Filled.MoreVert,
                            "Presets options"
                        )
                    }
                )
                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }

                ) {
                    DropdownMenuItem(
                        text = { Text("Edit preset")},
                        onClick = { onEdit(preset.id) },
                        trailingIcon = {
                            Icon(
                                Icons.Filled.Edit,
                                "Edit preset"
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete preset") },
                        onClick = {
                            onDelete(preset.id)
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Filled.Delete,
                                "Delete Preset"
                            )
                        }
                    )
                }
            }
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