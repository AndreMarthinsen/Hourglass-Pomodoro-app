package com.example.assignment1.data.unlockable

import android.content.Context
import androidx.compose.runtime.rememberCoroutineScope
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@Database(entities = [Unlockable::class], version = 1, exportSchema = false)
abstract class UnlockableDatabase : RoomDatabase() {
    abstract fun unlockableDao(): UnlockableDataAccessObject

    companion object {
        @Volatile
        private var Instance: UnlockableDatabase? = null

        fun getDatabase(context: Context): UnlockableDatabase {
            return Instance ?: synchronized(this) {
                Room
                    .databaseBuilder(
                        context,
                        UnlockableDatabase::class.java,
                        "unlockable_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(seedDataBaseCallback(context))
                    .build()
                    .also { Instance = it }
            }
        }

        private fun seedDataBaseCallback(context: Context) : Callback {
            return object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    var dao = getDatabase(context).unlockableDao()
                    GlobalScope.launch {
                        dao.insert(Unlockable(
                            name = "Prize 1",
                            cost = 50,
                            purchased = false))
                        dao.insert(Unlockable(
                            name = "Prize 2",
                            cost = 75,
                            purchased = false))
                        dao.insert(Unlockable(
                            name = "Prize 3",
                            cost = 100,
                            purchased = false
                        ))
                    }
                }
            }
        }
    }
}