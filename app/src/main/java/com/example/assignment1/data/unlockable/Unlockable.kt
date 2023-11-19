package com.example.assignment1.data.unlockable

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unlockables")
data class Unlockable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val cost: Int,
    var purchased: Boolean
)