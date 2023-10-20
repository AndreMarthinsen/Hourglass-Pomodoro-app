package com.example.assignment1.data

import android.content.Context

interface AppContainer {
    val presetRepository : PresetRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val presetRepository: PresetRepository by lazy {
        OfflinePresetRepository(PresetDatabase.getDatabase(context).presetDao())
    }
}