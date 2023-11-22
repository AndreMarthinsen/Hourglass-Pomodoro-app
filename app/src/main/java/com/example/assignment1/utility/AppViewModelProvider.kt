package com.example.assignment1.utility

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.assignment1.PomodoroApplication
import com.example.assignment1.view_models.ActiveTimerViewModel
import com.example.assignment1.view_models.NavbarViewModel
import com.example.assignment1.view_models.PresetEditViewModel
import com.example.assignment1.view_models.PresetsViewModel
import com.example.assignment1.view_models.SettingsViewModel
import com.example.assignment1.view_models.UnlockableStoreViewModel


/**
 * Factory for view models where dependencies are injected.
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            PresetEditViewModel(
                this.createSavedStateHandle(),
                pomodoroApplication().container.presetRepository
            )
        }
        initializer {
            PresetsViewModel(
                pomodoroApplication().container.presetRepository,
            )
        }
        initializer {
            ActiveTimerViewModel(
                pomodoroApplication().container.presetRepository,
                pomodoroApplication().container.settingsRepository,
                pomodoroApplication()
            )
        }
        initializer {
            NavbarViewModel(
                pomodoroApplication().container.settingsRepository
            )
        }
        initializer {
            SettingsViewModel(
                pomodoroApplication().container.settingsRepository
            )

        }
        initializer {
            UnlockableStoreViewModel(
                pomodoroApplication().container.settingsRepository,
                pomodoroApplication().container.unlockableRepository
            )
        }
    }
}

/**
 * Helper function to get the application from the creation extras.
 */
fun CreationExtras.pomodoroApplication(): PomodoroApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PomodoroApplication)