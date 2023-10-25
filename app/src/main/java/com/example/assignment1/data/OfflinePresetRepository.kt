package com.example.assignment1.data

import kotlinx.coroutines.flow.Flow

/**
 * The implementation of the PresetRepository-interface
 * Function point to presetDao-implementations
 *
 * @param presetDao - DAO that describes database-interaction
 */
class OfflinePresetRepository(private val presetDao: PresetDao) : PresetRepository {
    /**
     * Retrieve all presets from the given data source
     */
    override fun getAllPresetsStream(): Flow<List<Preset>> = presetDao.getAllPresets()
    /**
     * Get a preset from data source by ID
     */
    override fun getPresetStream(id: Int): Flow<Preset?> = presetDao.getPreset(id)
    /**
     * Insert a preset into the data source
     */
    override suspend fun insertPreset(preset: Preset) = presetDao.insert(preset)
    /**
     * Delete a preset from the data source
     */
    override suspend fun deletePreset(preset: Preset) = presetDao.delete(preset)
    /**
     * Update item in the data source
     */
    override suspend fun updatePreset(preset: Preset) = presetDao.update(preset)
}