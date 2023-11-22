package com.example.assignment1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.assignment1.ui.components.PomodoroTopAppBar
import com.example.assignment1.R
import com.example.assignment1.data.preset.Preset
import com.example.assignment1.utility.AppViewModelProvider
import com.example.assignment1.ui.navigation.NavigationDestination
import com.example.assignment1.view_models.PresetsViewModel
import com.example.assignment1.ui.components.MetallicContainer
import com.example.assignment1.ui.components.RoundMetalButton
import com.example.assignment1.ui.components.ShinyBlackContainer
import kotlinx.coroutines.launch

object PresetsDestination : NavigationDestination {
    override val route = "presets"
    override val titleRes = 3
}


/**
 * Screen for viewing currently saved presets. The presets provides the user with
 * the ability to edit, delete, and start a preset.
 *
 * @param navigateToPresetEdit Callback for when the user wants to edit a preset
 * @param navigateToActivePreset Callback for when the user wants to start a preset
 * @param navigateBack Callback for when the user wants to navigate back
 * @param modifier Modifier for the screen
 * @param navController Navigation controller for the screen
 * @param viewModel The view model for the presets screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PresetsScreen(
    navigateToPresetEdit: (Int) -> Unit = {},
    navigateToActivePreset: (Int) -> Unit = {},
    navigateBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: PresetsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val presetsUiState by viewModel.presetsUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    
    Scaffold(
        topBar = {
            PomodoroTopAppBar(
                title = "Presets",
                canNavigateBack = true,
                navigateUp = navigateBack,
                scrollBehavior = scrollBehavior,
                navController = navController
            )
        },
        floatingActionButton = {
            RoundMetalButton(size = 80.dp, onClick = { navigateToPresetEdit(0)  }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "To Presets"
                )
            }
        }
    ) {
        innerPadding ->
        ShinyBlackContainer {
            PresetsBody(
                presetList = presetsUiState.presetList,
                modifier = modifier
                    .padding(innerPadding),
                onEdit = { navigateToPresetEdit(it) },
                onDelete = {
                    coroutineScope.launch {
                        viewModel.deletePreset(it)
                    }
                },
                onStart = { navigateToActivePreset(it) }
            )
        }
    }
}


/**
 * Body for the presets screen. Displays a list of presets.
 *
 * @param presetList The list of presets to display
 * @param modifier Modifier for the body
 * @param onDelete Callback for when a preset button is pressed
 * @param onEdit Callback for when an edit button is pressed
 * @param onStart Callback for when a start button is pressed
 */
@Preview
@Composable
fun PresetsBody(
    modifier: Modifier = Modifier,
    presetList: List<Preset> = listOf(
        Preset(0, "Test", 4, 2, 25, 5, 15)),
    onDelete: (Int) -> Unit = {},
    onEdit: (Int) -> Unit = {},
    onStart: (Int) -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        PresetList(
            presetList = presetList,
            onDelete = onDelete,
            onEdit = onEdit,
            onStart = onStart)
    }
}


/**
 * Displays a list of presets.
 *
 * @param presetList The list of presets to display
 * @param modifier Modifier for the list
 * @param onDelete Callback for when a preset button is pressed
 * @param onEdit Callback for when an edit button is pressed
 * @param onStart Callback for when a start button is pressed
 */
@Preview
@Composable
private fun PresetList(
    modifier: Modifier = Modifier,
    presetList: List<Preset> = listOf(
        Preset(0, "Test", 4, 2, 25, 5, 15)),
    onDelete: (Int) -> Unit = {},
    onEdit: (Int) -> Unit = {},
    onStart: (Int) -> Unit = {}
) {
    LazyColumn(modifier = modifier) {
        items(items = presetList, key = {it.id}) {preset ->
            PresetDisplay(
                preset = preset,
                onDelete = onDelete,
                onEdit = onEdit,
                onStart = onStart)
        }
    }
}


/**
 * Displays a single preset with information about break length, focus length
 * along with buttons for editing, deleting and starting the preset.
 *
 * @param modifier Modifier for the preset
 * @param preset The preset to display
 * @param onDelete Callback for when the delete button is pressed
 * @param onEdit Callback for when the edit button is pressed
 * @param onStart Callback for when the start button is pressed
 */
@Preview
@Composable
private fun PresetDisplay(
    modifier: Modifier = Modifier,
    preset: Preset = Preset(0, "Test", 4, 2, 25, 5, 15),
    onStart: (Int) -> Unit = {},
    onDelete: (Int) -> Unit = {},
    onEdit: (Int) -> Unit = {}
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    MetallicContainer(height = 40f, rounding = 6.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                        onClick = { isExpanded = !isExpanded },
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
                            text = { Text("Edit preset") },
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
                Row() {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.focus_icon),
                        contentDescription = "Break Icon",
                    )
                    Text(" ${preset.focusLength}m")
                }
                Row() {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.break_icon),
                        contentDescription = "Break Icon",
                    )
                    Text(" ${preset.breakLength}m / ${preset.longBreakLength}m")
                }
                Row() {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.goal_icon),
                        contentDescription = "Break Icon",
                    )
                    Text(" ${preset.roundsInSession} x ${preset.totalSessions}")
                }
                IconButton(
                    onClick = { onStart(preset.id) },
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
}