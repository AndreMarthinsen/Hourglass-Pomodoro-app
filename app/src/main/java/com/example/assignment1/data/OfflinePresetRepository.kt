package com.example.assignment1.data

import kotlinx.coroutines.flow.Flow

/**
 * The implementation of the PresetRepository-interface
 * Function point to presetDao-implementations
 *
 * @param presetDataAccessObject - DAO that describes database-interaction
 */
class OfflinePresetRepository(private val presetDataAccessObject: PresetDataAccessObject) : PresetRepository {
    /**
     * Retrieve all presets from the given data source
     */
    override fun getAllPresetsStream(): Flow<List<Preset>> = presetDataAccessObject.getAllPresets()
    /**
     * Get a preset from data source by ID
     */
    override fun getPresetStream(id: Int): Flow<Preset?> = presetDataAccessObject.getPreset(id)
    /**
     * Insert a preset into the data source
     */
    override suspend fun insertPreset(preset: Preset) = presetDataAccessObject.insert(preset)
    /**
     * Delete a preset from the data source
     */
    override suspend fun deletePreset(preset: Preset) = presetDataAccessObject.delete(preset)
    /**
     * Update item in the data source
     */
    override suspend fun updatePreset(preset: Preset) = presetDataAccessObject.update(preset)
}