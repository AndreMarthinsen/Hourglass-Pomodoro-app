package com.example.assignment1.recievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.assignment1.ui.preset.timer.BonusActivities
import com.example.assignment1.ui.preset.timer.BonusManager
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.ActivityTransitionResult


/**
 * ActivityTransitionReceiver is a broadcast reciever intended for use with
 * the Google Play Activity Recognition API. It is used to detect when the user
 * changes activity state, such as walking, running, or biking.
 *
 * @see BonusManager - Used to set the multiplier for the timer based on activity
 */
class ActivityTransitionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.extras?.keySet()?.forEach {
            Log.d("ActivityTransition", "Key: ${it}, value: ${intent.extras?.get(it)}")
        }

        if (intent != null  && ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)
            if (result != null && result.transitionEvents.isNotEmpty()) {
                val latestEvent = result.transitionEvents.last()
                Log.d("ActivityTransition", "Received ${latestEvent.activityType}")
                val isActive = BonusActivities.contains(latestEvent.activityType) &&
                        latestEvent.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER
                BonusManager.setMultiplier(isActive)
                BonusManager.latestActivity = latestEvent.activityType
            }
        } else if (intent != null && ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            if(result != null) {
                val latestEvent = result.mostProbableActivity
                Log.d("ActivityTransition", "Received ${latestEvent.type} with confidence ${latestEvent.confidence}")
                val isActive = BonusActivities.contains(latestEvent.type)
                BonusManager.setMultiplier(isActive)
                BonusManager.latestActivity = latestEvent.type
            }
        }
    }
}