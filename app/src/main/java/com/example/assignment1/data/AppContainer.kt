package com.example.assignment1.data

import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val presetRepository : PresetRepository
    val unlockablesRepository : UnlockablesRepository
}

/**
 * [AppContainer] implementation that provides instance of [PresetRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {

    override val presetRepository: PresetRepository by lazy {
        OfflinePresetRepository(PresetDatabase.getDatabase(context).presetDao())
    }

    override val unlockablesRepository = UnlockablesRepository(context)
}