package com.example.assignment1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Class that describes a preset-pomodoro session
 * roundLength: total length of the round in minutes, consisting of one or more focus sessions
 * totalSessions: the number of focus sessions in a round
 * focusLength: the length of a focus session in minutes
 * breakLength: the length of an inter-focus session break in minutes
 * longBreakLength: the length of a post-round break in minutes
 */
@Entity(tableName = "presets")
data class Preset (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val roundLength: Int,
    val totalSessions: Int,
    val focusLength: Int,
    val breakLength: Int,
    val longBreakLength: Int,
)