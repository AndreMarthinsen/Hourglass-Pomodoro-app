package com.example.assignment1.data

import android.content.Context
import androidx.room.Room
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Preset::class], version = 1, exportSchema = false)
abstract class PresetDatabase : RoomDatabase() {

    abstract fun presetDao(): PresetDao

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