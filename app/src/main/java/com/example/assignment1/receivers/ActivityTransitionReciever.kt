package com.example.assignment1.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.assignment1.services.BonusManager
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.ActivityTransitionResult


/**
 * ActivityTransitionReceiver is a broadcast receiver intended for use with
 * the Google Play Activity Recognition API. It is used to detect when the user
 * changes activity state, such as walking, running, or biking.
 * Upon receiving an activity transition, the latest activity is stored in BonusManager
 * which then determines how it impacts how the user gains currency.
 *
 * @see BonusManager - Used to set the multiplier for the timer based on activity
 */
class ActivityTransitionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null  && ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)
            if (result != null && result.transitionEvents.isNotEmpty()) {
                val latestEvent = result.transitionEvents.last()
                BonusManager.latestActivity = latestEvent.activityType
            }
        } else if (intent != null && ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            if(result != null) {
                val latestEvent = result.mostProbableActivity
                BonusManager.latestActivity = latestEvent.type
            }
        }
    }
}