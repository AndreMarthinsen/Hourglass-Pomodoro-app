package com.example.assignment1.ui.unlockables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment1.data.Settings
import com.example.assignment1.data.SettingsRepository
import com.example.assignment1.data.preset.Preset
import com.example.assignment1.data.unlockable.Unlockable
import com.example.assignment1.data.unlockable.UnlockableRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class UnlockableStoreViewModel(
    private val settingsRepository: SettingsRepository,
    private val unlockableRepository: UnlockableRepository
) : ViewModel() {
    val unlockablesUiState: StateFlow<UnlockablesUiState> =
        unlockableRepository.getAllUnlockablesStream().map { UnlockablesUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = UnlockablesUiState()
            )

    val settingsUiState: StateFlow<Settings> =
        settingsRepository.getFromSettingsStore().map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = Settings(0, true)
            )

}

data class UnlockablesUiState(val unlockableList: List<Unlockable> = listOf())