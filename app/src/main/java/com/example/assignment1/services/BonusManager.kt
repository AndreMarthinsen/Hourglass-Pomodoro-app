package com.example.assignment1.services

import com.google.android.gms.location.DetectedActivity


/**
 * BonusManager is a singleton object that determines how the user gains currency
 * based on the latest observed user activity such as walking or sitting still.
 */
object BonusManager {
    var latestActivity: Int = DetectedActivity.STILL


    /**
     * Returns a multiplier for the timer based on the latest observed user activity.
     * During breaks, physical activity is rewarded.
     */
    fun getBreakBonus() : Int {
        return when (latestActivity) {
            DetectedActivity.ON_FOOT -> 2
            DetectedActivity.WALKING -> 2
            DetectedActivity.RUNNING -> 3
            DetectedActivity.ON_BICYCLE -> 3
            else -> 1
        }
    }


    /**
     * Returns a multiplier for the timer based on the latest observed user activity.
     * During focus periods, sitting still is rewarded.
     */
    fun getFocusBonus() : Int {
        return when (latestActivity) {
            DetectedActivity.STILL -> 2
            else -> 0
        }
    }
}
