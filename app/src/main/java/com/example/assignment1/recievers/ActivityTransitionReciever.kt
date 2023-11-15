package com.example.assignment1.recievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.assignment1.ui.preset.timer.BonusActivities
import com.example.assignment1.ui.preset.timer.BonusMultiplierManager
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.ActivityTransitionResult

class ActivityTransitionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.extras?.keySet()?.forEach {
            Log.d("ActivityTransition", "Key: ${it}, value: ${intent.extras?.get(it)}")
        }

        if (intent != null  && ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)
            if (result != null && result.transitionEvents.isNotEmpty()) {
                // Get the latest activity transition event
                val latestEvent = result.transitionEvents.last()
                Log.d("ActivityTransition", "Received ${latestEvent.activityType}")
                // Determine if the user is active (e.g., walking or cycling)
                val isActive = BonusActivities.contains(latestEvent.activityType) &&
                        latestEvent.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER
                BonusMultiplierManager.setMultiplier(isActive)
                BonusMultiplierManager.latestActivity = latestEvent.activityType
            }
        } else if (intent != null && ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            // Get the latest activity transition event
            if(result != null) {
                val latestEvent = result.mostProbableActivity
                Log.d("ActivityTransition", "Received ${latestEvent.type} with confidence ${latestEvent.confidence}")
                // Determine if the user is active (e.g., walking or cycling)
                val isActive = BonusActivities.contains(latestEvent.type)
                BonusMultiplierManager.setMultiplier(isActive)
                BonusMultiplierManager.latestActivity = latestEvent.type
            }
        }
    }
}