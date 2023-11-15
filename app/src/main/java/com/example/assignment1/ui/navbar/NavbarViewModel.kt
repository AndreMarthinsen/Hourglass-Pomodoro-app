package com.example.assignment1.ui.navbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment1.data.Unlockables
import com.example.assignment1.data.UnlockablesRepository
import com.example.assignment1.ui.preset.PresetsViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class NavbarViewModel(private val unlockablesRepository: UnlockablesRepository) : ViewModel() {

    val unlockablesUiState: StateFlow<Unlockables> =
        unlockablesRepository.getFromUnlockablesStore().map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(NavbarViewModel.TIMEOUT_MILLIS),
                initialValue = Unlockables(0)
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun incrementCurrency(amount: Int) {
        unlockablesRepository.updateCurrency(unlockablesUiState.value.currency + amount)
    }
}