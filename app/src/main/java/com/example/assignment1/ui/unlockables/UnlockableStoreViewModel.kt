package com.example.assignment1.ui.unlockables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment1.data.Settings
import com.example.assignment1.data.SettingsRepository
import com.example.assignment1.data.unlockable.Unlockable
import com.example.assignment1.data.unlockable.UnlockableRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
                initialValue = Settings(0,false)
            )

    fun seedDatabase(unlockables: List<Unlockable>) {
        viewModelScope.launch {
            unlockables.map {
                unlockableRepository.insertUnlockable(it)
            }
        }
    }

    suspend fun purchaseUnlockable(unlockableId: Int) {
        unlockablesUiState.value.unlockableList.find {unlockable ->
            unlockable.id == unlockableId}?.let {
            if (settingsUiState.value.currency >= it.cost && !it.purchased) {
                settingsRepository.updateCurrency(settingsUiState.value.currency - it.cost)
                it.purchased = true
                unlockablesUiState.value.unlockableList.removeIf {
                    it.id == unlockableId
                }
                unlockablesUiState.value.unlockableList.add(it)
                unlockableRepository.updateUnlockable(it)
            }
        }
    }

    suspend fun debugClearDatabase() {
        unlockablesUiState.value.unlockableList.map {
            unlockableRepository.deleteUnlockable(it)
        }
    }

}

data class UnlockablesUiState(val unlockableList: MutableList<Unlockable> = mutableListOf())