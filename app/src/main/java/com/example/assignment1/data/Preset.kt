package com.example.assignment1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "presets")
data class Preset (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val roundLength: Int,
    val totalSessions: Int,
    val focusLength: Int,
    val breakLength: Int,
)