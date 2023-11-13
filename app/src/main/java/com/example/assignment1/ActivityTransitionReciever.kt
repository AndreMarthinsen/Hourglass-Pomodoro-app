package com.example.assignment1

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.assignment1.ui.preset.BonusActivities
import com.example.assignment1.ui.preset.BonusMultiplierManager
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult

class ActivityTransitionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("ActivityTransition", "Received + ${intent.toString()}")
        if (intent != null && ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)
            if (result != null && result.transitionEvents.isNotEmpty()) {
                // Get the latest activity transition event
                val latestEvent = result.transitionEvents.last()

                // Determine if the user is active (e.g., walking or cycling)
                val isActive = BonusActivities.contains(latestEvent.activityType) &&
                        latestEvent.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER
                BonusMultiplierManager.setMultiplier(isActive)
                BonusMultiplierManager.latestActivity = latestEvent.activityType
            }
        }
    }
}