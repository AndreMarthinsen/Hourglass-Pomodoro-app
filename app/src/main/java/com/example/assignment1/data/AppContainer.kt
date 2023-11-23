package com.example.assignment1.data

import android.content.Context
import com.example.assignment1.data.preset.OfflinePresetRepository
import com.example.assignment1.data.preset.PresetDatabase
import com.example.assignment1.data.preset.PresetRepository
import com.example.assignment1.data.unlockable.OfflineUnlockableRepository
import com.example.assignment1.data.unlockable.UnlockableDatabase
import com.example.assignment1.data.unlockable.UnlockableRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val presetRepository : PresetRepository
    val unlockableRepository : UnlockableRepository
    val settingsRepository : SettingsRepository
}

/**
 * [AppContainer] implementation that provides instance of [PresetRepository],
 * [UnlockableRepository], [SettingsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {

    override val presetRepository: PresetRepository by lazy {
        OfflinePresetRepository(PresetDatabase.getDatabase(context).presetDao())
    }

    override val unlockableRepository : UnlockableRepository by lazy {
        OfflineUnlockableRepository(UnlockableDatabase.getDatabase(context).unlockableDao())
    }

    override val settingsRepository = SettingsRepository(context)
}