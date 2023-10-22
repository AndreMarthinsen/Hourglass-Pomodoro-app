package com.example.assignment1.data

import kotlinx.coroutines.flow.Flow

class OfflinePresetRepository(private val presetDao: PresetDao) : PresetRepository {
    override fun getAllPresetsStream(): Flow<List<Preset>> = presetDao.getAllPresets()

    override fun getPresetStream(id: Int): Flow<Preset?> = presetDao.getPreset(id)

    override suspend fun insertPreset(preset: Preset) = presetDao.insert(preset)

    override suspend fun deletePreset(preset: Preset) = presetDao.delete(preset)

    override suspend fun updatePreset(preset: Preset) = presetDao.update(preset)
}