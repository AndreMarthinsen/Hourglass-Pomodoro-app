package com.example.assignment1.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.assignment1.PomodoroApplication
import com.example.assignment1.ui.navbar.NavbarViewModel
import com.example.assignment1.ui.preset.timer.ActiveTimerViewModel
import com.example.assignment1.ui.preset.PresetEditViewModel
import com.example.assignment1.ui.preset.PresetsViewModel

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
                pomodoroApplication().container.unlockablesRepository
            )
        }
        initializer {
            ActiveTimerViewModel(
                pomodoroApplication().container.presetRepository,
                pomodoroApplication()
            )
        }
        initializer {
            NavbarViewModel(
                pomodoroApplication().container.unlockablesRepository
            )
        }
    }
}

fun CreationExtras.pomodoroApplication(): PomodoroApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PomodoroApplication)