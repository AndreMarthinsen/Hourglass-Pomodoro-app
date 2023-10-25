package com.example.assignment1.ui.preset

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment1.data.Preset
import com.example.assignment1.data.PresetRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class PresetEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val presetRepository: PresetRepository
) : ViewModel() {
    var presetUiState by mutableStateOf(PresetUiState())
        private set

    //private val presetId: Int = checkNotNull(savedStateHandle[PresetEditDestination.presetIdArg])

    fun updateUiState(presetDetails: PresetDetails) {
        presetUiState =
            PresetUiState(presetDetails = presetDetails, isEntryValid = validateInput(presetDetails))
    }

    suspend fun savePreset() {
        if (validateInput()) {
            presetRepository.insertPreset(presetUiState.presetDetails.toPreset())
        }
    }

    private fun validateInput(uiState: PresetDetails = presetUiState.presetDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() &&
                    roundLength.isNotBlank() &&
                    totalSessions.isNotBlank() &&
                    focusLength.isNotBlank() &&
                    breakLength.isNotBlank()
        }
    }
}
data class PresetUiState(
    val presetDetails: PresetDetails = PresetDetails(),
    val isEntryValid: Boolean = false
)

data class PresetDetails(
    val id: Int = 0,
    val name: String = "",
    val roundLength: String = "",
    val totalSessions: String = "",
    val focusLength: String = "",
    val breakLength: String = "",
)

//TODO: check if explicitly initializing fields is necessary
fun PresetDetails.toPreset() : Preset = Preset(
    id = id,
    name = name,
    roundLength = roundLength.toInt()?: 0,
    totalSessions = totalSessions.toInt()?: 0,
    focusLength = focusLength.toInt()?: 0,
    breakLength = breakLength.toInt()?: 0
)

fun Preset.toPresetUiState(isEntryValid: Boolean = false): PresetUiState = PresetUiState(
    presetDetails = this.toPresetDetails(),
    isEntryValid = isEntryValid
)

fun Preset.toPresetDetails(): PresetDetails = PresetDetails(
    id = id,
    name = name,
    roundLength = roundLength.toString(),
    totalSessions = totalSessions.toString(),
    focusLength = focusLength.toString(),
    breakLength = breakLength.toString()
)