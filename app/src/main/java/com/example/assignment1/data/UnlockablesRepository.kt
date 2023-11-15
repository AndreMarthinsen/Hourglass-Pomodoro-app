package com.example.assignment1.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

data class Unlockables(
    val currency: Int
)

const val UNLOCKABLES_NAME = "unlockable_preferences"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = UNLOCKABLES_NAME
)

class UnlockablesRepository(
    val context : Context
) {

    private val TAG: String = "UnlockablesRepository"

    private object UnlockablesKeys {
        val CURRENCY = intPreferencesKey("currency")
    }

    val unlockables: Flow<Unlockables> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading unlockables.", exception)
            } else {
                throw exception
            }
        }.map {preferences ->
            mapUnlockables(preferences)
        }

    suspend fun updateCurrency(currency: Int) {
        context.dataStore.edit { unlockables ->
            unlockables[UnlockablesKeys.CURRENCY] = currency
        }
    }

    suspend fun fetchInitialUnlockables() =
        mapUnlockables(context.dataStore.data.first().toPreferences())

    fun getFromUnlockablesStore() = context.dataStore.data.map {
        mapUnlockables(it.toPreferences())
    }

    private fun mapUnlockables(preferences: Preferences): Unlockables {
        val currency: Int = preferences[UnlockablesKeys.CURRENCY] ?: 0

        return Unlockables(currency)
    }
}