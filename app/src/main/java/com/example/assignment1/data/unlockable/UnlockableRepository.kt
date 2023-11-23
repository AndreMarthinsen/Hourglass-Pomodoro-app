package com.example.assignment1.data.unlockable

import kotlinx.coroutines.flow.Flow

/**
 * Interface that declares methods for interacting with database
 */
interface UnlockableRepository {
    /**
     * Gets all objects from unlockables table
     */
    fun getAllUnlockablesStream(): Flow<MutableList<Unlockable>>
    /**
     * Gets a single unlockable by id
     */
    fun getUnlockableStream(id: Int): Flow<Unlockable?>
    /**
     * Inserts an unlockable-object into the datasource
     */
    suspend fun insertUnlockable(unlockable: Unlockable)
    /**
     * Deletes an unlockable-object from the datasource
     */
    suspend fun deleteUnlockable(unlockable: Unlockable)
    /**
     * Updates an unlockable-object in the datasource
     */
    suspend fun updateUnlockable(unlockable: Unlockable)
}