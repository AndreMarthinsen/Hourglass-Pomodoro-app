package com.example.assignment1.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment1.data.Settings
import com.example.assignment1.data.SettingsRepository
import com.example.assignment1.ui.navbar.NavbarViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel (
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settingsUiState: StateFlow<Settings> =
        settingsRepository.getFromSettingsStore().map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(1000),
                initialValue = Settings(0,false)
            )

    suspend fun setDisableCoinWarnings(enabled: Boolean) {
        settingsRepository.updateCoinWarning(enabled)
    }

}