package com.example.assignment1.ui.preset

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment1.data.Preset
import com.example.assignment1.data.PresetRepository
import com.example.assignment1.data.Unlockables
import com.example.assignment1.data.UnlockablesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PresetsViewModel(
    private val presetRepository: PresetRepository,
    private val unlockablesRepository: UnlockablesRepository
) : ViewModel() {
    val presetsUiState: StateFlow<PresetsUiState> =
        presetRepository.getAllPresetsStream().map { PresetsUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = PresetsUiState()
            )

    val unlockablesUiState: StateFlow<Unlockables> =
        unlockablesRepository.getFromUnlockablesStore().map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = Unlockables(0)
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

    suspend fun incrementCurrency(amount: Int) {
        unlockablesRepository.updateCurrency(unlockablesUiState.value.currency + amount)
    }
}

data class PresetsUiState(val presetList: List<Preset> = listOf())