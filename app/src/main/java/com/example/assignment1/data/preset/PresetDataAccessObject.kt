package com.example.assignment1.data.preset

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * The Database Access Object that defines interaction with the preset-database
 * insert, update and delete are built-in functions
 * getAll and getPreset run manually defined SQL-queries
 */
@Dao
interface PresetDataAccessObject {
    //In the case of an insert-conflict, the new item is ignored
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(preset: Preset)

    @Update
    suspend fun update(preset: Preset)

    @Delete
    suspend fun delete(preset: Preset)

    @Query("SELECT * from presets ORDER BY name ASC")
    fun getAllPresets(): Flow<List<Preset>>

    @Query("SELECT * from presets WHERE id = :id")
    fun getPreset(id: Int): Flow<Preset>
}