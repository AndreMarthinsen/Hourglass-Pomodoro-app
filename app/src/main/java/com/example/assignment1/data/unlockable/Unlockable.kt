package com.example.assignment1.data.unlockable

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Class that describes an unlockable reward object
 * name: name of the object
 * cost: how many units of reward currency the object costs
 * purchased: bool that tracks if the object has been unlocked
 */
@Entity(tableName = "unlockables")
data class Unlockable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val cost: Int,
    var purchased: Boolean
)