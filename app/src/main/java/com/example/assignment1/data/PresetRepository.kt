package com.example.assignment1.data

import kotlinx.coroutines.flow.Flow

interface PresetRepository {
    /**
     * Retrieve all presets from the given data source
     */
    fun getAllPresetsStream(): Flow<List<Preset>>

    /**
     * Insert a preset into the data source
     */
    suspend fun insertPreset(preset: Preset)

    /**
     * Delete a preset from the data source
     */
    suspend fun deletePreset(preset: Preset)

    /**
     * Update item in the data source
     */
    suspend fun updatePreset(preset: Preset)
}