package com.example.assignment1.data.unlockable

import kotlinx.coroutines.flow.Flow

/**
 * The implementation of the UnlockableRepository-interface
 * Functions point to unlockableDao-implementations
 *
 * @param unlockableDataAccessObject - DAO that describes database-interaction
 */
class OfflineUnlockableRepository(
    private val unlockableDataAccessObject: UnlockableDataAccessObject) : UnlockableRepository
{
    override fun getAllUnlockablesStream(): Flow<MutableList<Unlockable>> = unlockableDataAccessObject.getAllUnlockables()
    override fun getUnlockableStream(id: Int): Flow<Unlockable?> = unlockableDataAccessObject.getUnlockable(id)
    override suspend fun deleteUnlockable(unlockable: Unlockable) = unlockableDataAccessObject.delete(unlockable)
    override suspend fun insertUnlockable(unlockable: Unlockable) = unlockableDataAccessObject.insert(unlockable)
    override suspend fun updateUnlockable(unlockable: Unlockable) = unlockableDataAccessObject.update(unlockable)
}