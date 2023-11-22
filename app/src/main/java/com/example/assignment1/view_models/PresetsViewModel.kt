package com.example.assignment1.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment1.data.preset.Preset
import com.example.assignment1.data.preset.PresetRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PresetsViewModel(
    private val presetRepository: PresetRepository,
) : ViewModel() {
    val presetsUiState: StateFlow<PresetsUiState> =
        presetRepository.getAllPresetsStream().map { PresetsUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = PresetsUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun deletePreset(presetId: Int) {
        presetsUiState.value.presetList.find {preset->
            preset.id == presetId
        }?.let {
            presetRepository.deletePreset(
                it
            )
        }
    }
}

data class PresetsUiState(val presetList: List<Preset> = listOf())