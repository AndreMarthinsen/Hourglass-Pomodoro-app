package com.example.assignment1.data.unlockable

import kotlinx.coroutines.flow.Flow

interface UnlockableRepository {

    fun getAllUnlockablesStream(): Flow<MutableList<Unlockable>>

    fun getUnlockableStream(id: Int): Flow<Unlockable?>

    suspend fun insertUnlockable(unlockable: Unlockable)

    suspend fun deleteUnlockable(unlockable: Unlockable)

    suspend fun updateUnlockable(unlockable: Unlockable)
}