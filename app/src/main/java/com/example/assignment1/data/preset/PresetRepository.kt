package com.example.assignment1.data.preset

import kotlinx.coroutines.flow.Flow

/**
 * Interface that declares methods for interacting with database
 */
interface PresetRepository {
    /**
     * Retrieve all presets from the given data source
     */
    fun getAllPresetsStream(): Flow<List<Preset>>

    /**
     * Get a preset from data source by ID
     */
    fun getPresetStream(id: Int): Flow<Preset?>
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