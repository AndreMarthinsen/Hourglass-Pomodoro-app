package com.example.assignment1.ui.preset.timer

import com.google.android.gms.location.DetectedActivity
import java.util.concurrent.atomic.AtomicInteger

object BonusManager {
    private const val ACTIVE_MULTIPLIER = 2 // Multiplier for active activities
    private const val DEFAULT_MULTIPLIER = 1 // Default multiplier
    public var latestActivity: Int = 3

    private var currentMultiplier: Int = DEFAULT_MULTIPLIER

    fun setMultiplier(isActive: Boolean) {
        currentMultiplier = if (isActive) ACTIVE_MULTIPLIER else DEFAULT_MULTIPLIER
    }

    fun getMultiplier(): Int {
        return currentMultiplier
    }

    fun getBreakBonus() : Int {
        return when (latestActivity) {
            DetectedActivity.ON_FOOT -> 2
            DetectedActivity.WALKING -> 2
            DetectedActivity.RUNNING -> 3
            DetectedActivity.ON_BICYCLE -> 3
            else -> 1
        }
    }

    fun getFocusBonus() : Int {
        return when (latestActivity) {
            DetectedActivity.STILL -> 2
            else -> 0
        }
    }
}

val BonusActivities = listOf(
    DetectedActivity.WALKING,
    DetectedActivity.RUNNING,
    DetectedActivity.ON_BICYCLE,
    DetectedActivity.ON_FOOT
)