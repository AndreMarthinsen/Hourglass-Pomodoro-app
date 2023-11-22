package com.example.assignment1.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment1.data.Settings
import com.example.assignment1.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class NavbarViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    val settingsUiState: StateFlow<Settings> =
        settingsRepository.getFromSettingsStore().map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = Settings(0,false)
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun incrementCurrency(amount: Int) {
        settingsRepository.updateCurrency(settingsUiState.value.currency + amount)
    }
}