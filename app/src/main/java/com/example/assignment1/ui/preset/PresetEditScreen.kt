package com.example.assignment1.ui.preset

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment1.PomodoroTopAppBar
import com.example.assignment1.ui.AppViewModelProvider
import com.example.assignment1.ui.navigation.NavigationDestination
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.launch

object PresetEditDestination : NavigationDestination {
    override val route = "preset_edit"
    override val titleRes = 2
    const val presetIdArg = "presetId"
    val routeWithArgs = "$route/{$presetIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetEditScreen(
    navigateBack: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PresetEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
       topBar = {
           PomodoroTopAppBar(
               title = "Edit Preset",
               canNavigateBack = true,
               navigateUp = navigateBack,
               navController = navController
           )
       },
       modifier = modifier
    ) { innerPadding ->
       PresetEntryBody(
           presetUiState = viewModel.presetUiState,
           onPresetValueChange = viewModel::updateUiState,
           onSaveClick = {
               coroutineScope.launch{
                   viewModel.savePreset()
                   navigateBack()
               }
           },
           modifier = Modifier
               .padding(innerPadding)
       )
    }
}

@Composable
fun PresetEntryBody(
    presetUiState: PresetUiState,
    onPresetValueChange: (PresetDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.padding(top = 6.dp)
    ) {
        PresetEntryForm(
            presetDetails = presetUiState.presetDetails,
            onValueChange = onPresetValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = presetUiState.isEntryValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save preset")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetEntryForm(
    presetDetails: PresetDetails,
    modifier: Modifier = Modifier,
    onValueChange: (PresetDetails) -> Unit = {},
    enabled: Boolean = true
) {
    Column {
        OutlinedTextField(
            value = presetDetails.name,
            onValueChange = { onValueChange(presetDetails.copy(name = it)) },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = presetDetails.roundsInSession,
            onValueChange = { onValueChange(presetDetails.copy(roundsInSession = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Rounds per session") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = presetDetails.totalSessions,
            onValueChange = { onValueChange(presetDetails.copy(totalSessions = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Total Sessions") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = presetDetails.focusLength,
            onValueChange = { onValueChange(presetDetails.copy(focusLength = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Focus length") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = presetDetails.breakLength,
            onValueChange = { onValueChange(presetDetails.copy(breakLength = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Break length") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = presetDetails.longBreakLength,
            onValueChange = { onValueChange(presetDetails.copy(longBreakLength = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Long break length") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        /**
         * TODO: decide if this hint is necessary, make it dynamically respond to missing fields
         */
        /*if (enabled) {
            Text(
                text = "Required fields"//stringResource(R.string.required_fields),
                //modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }*/
    }
}