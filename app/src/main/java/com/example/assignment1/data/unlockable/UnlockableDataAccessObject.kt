package com.example.assignment1.data.unlockable

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * The Database Access Object that defines interaction with the unlockable-database
 * insert, update and delete are built-in functions
 * getAllUnlockables and getUnlockable run manually defined SQL-queries
 */
@Dao
interface UnlockableDataAccessObject {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(unlockable: Unlockable)

    @Update
    suspend fun update(unlockable: Unlockable)

    @Delete
    suspend fun delete(unlockable: Unlockable)

    @Query("SELECT * from unlockables ORDER BY name ASC")
    fun getAllUnlockables(): Flow<MutableList<Unlockable>>

    @Query("SELECT * from unlockables WHERE id = :id")
    fun getUnlockable(id: Int): Flow<Unlockable>
}