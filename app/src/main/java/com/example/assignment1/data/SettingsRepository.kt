package com.example.assignment1.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

data class Settings(
    val currency: Int,
    val showCoinWarning: Boolean,
)

const val Settings_NAME = "pomodoro_preferences"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Settings_NAME
)

class SettingsRepository(
    val context : Context
) {

    private val TAG: String = "UnlockablesRepository"

    private object SettingsKeys {
        val CURRENCY = intPreferencesKey("currency")
        val SHOW_COIN_WARNING = booleanPreferencesKey("showCoinWarning")
        val UNLOCKABLES = stringPreferencesKey("unlockables")
    }

    val settings: Flow<Settings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading unlockables.", exception)
            } else {
                throw exception
            }
        }.map {preferences ->
            mapSettings(preferences)
        }

    suspend fun updateCurrency(currency: Int) {
        context.dataStore.edit { preferences ->
            preferences[SettingsKeys.CURRENCY] = currency
        }
    }

    suspend fun addCurrency(currency: Int) {
        context.dataStore.edit() { preferences ->
            val currentCurrency = preferences[SettingsKeys.CURRENCY] ?: 0
            preferences[SettingsKeys.CURRENCY] = currentCurrency + currency
        }
    }

    suspend fun updateCoinWarning(newState: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SettingsKeys.SHOW_COIN_WARNING] = newState
        }
    }


    fun getFromSettingsStore() = context.dataStore.data.map {
        mapSettings(it.toPreferences())
    }


    private fun mapSettings(preferences: Preferences): com.example.assignment1.data.Settings {
        val currency: Int = preferences[SettingsKeys.CURRENCY] ?: 0
        val showCoinWarning: Boolean = preferences[SettingsKeys.SHOW_COIN_WARNING] ?: true

        return Settings(
            currency,
            showCoinWarning)
    }
}