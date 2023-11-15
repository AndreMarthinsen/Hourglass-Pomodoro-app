package com.example.assignment1.data.preset

import android.content.Context
import androidx.room.Room
import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The SQL-lite Room-database implementation, currently offline and hosted locally on a device
 * If the preset-class is changed (e.g., by adding a member or function), version-int must be incremented
 */
@Database(entities = [Preset::class], version = 2, exportSchema = false)
abstract class PresetDatabase : RoomDatabase() {

    abstract fun presetDao(): PresetDataAccessObject

    companion object {
        @Volatile
        private var Instance: PresetDatabase? = null

        fun getDatabase(context: Context): PresetDatabase {
            return Instance ?: synchronized(this) {
                Room
                    .databaseBuilder(
                        context,
                        PresetDatabase::class.java,
                        "preset_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}