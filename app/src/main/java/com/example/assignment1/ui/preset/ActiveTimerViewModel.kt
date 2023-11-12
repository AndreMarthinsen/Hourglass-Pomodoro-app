package com.example.assignment1.ui.preset

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.SystemClock
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.assignment1.data.Preset
import com.example.assignment1.data.PresetRepository
import com.example.assignment1.services.TimerService
import com.example.assignment1.services.pad
import kotlinx.coroutines.flow.first
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import androidx.lifecycle.viewModelScope
import com.example.assignment1.PomodoroApplication
import com.example.assignment1.R
import com.google.android.gms.common.internal.safeparcel.SafeParcelableSerializer
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit

object BonusMultiplierManager {
    private const val ACTIVE_MULTIPLIER = 2 // Multiplier for active activities
    private const val DEFAULT_MULTIPLIER = 1 // Default multiplier

    private var currentMultiplier: Int = DEFAULT_MULTIPLIER

    fun setMultiplier(isActive: Boolean) {
        currentMultiplier = if (isActive) ACTIVE_MULTIPLIER else DEFAULT_MULTIPLIER
    }

    fun getMultiplier(): Int {
        return currentMultiplier
    }
}

val BonusActivities = listOf(
    DetectedActivity.WALKING,
    DetectedActivity.RUNNING,
    DetectedActivity.ON_BICYCLE,
    DetectedActivity.ON_FOOT
)
class ActivityTransitionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("ActivityTransition", "Received + ${intent.toString()}")
        if (intent != null && ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)
            if (result != null && !result.transitionEvents.isEmpty()) {
                // Get the latest activity transition event
                val latestEvent = result.transitionEvents.last()

                // Determine if the user is active (e.g., walking or cycling)
                val isActive = BonusActivities.contains(latestEvent.activityType) &&
                        latestEvent.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER
                BonusMultiplierManager.setMultiplier(isActive)
            }
        }
    }
}

class ActiveTimerViewModel(
    private val presetRepository: PresetRepository, application: Application
) : AndroidViewModel(application) {
    private val defaultPreset = Preset(
        id = 0,
        name = "default",
        roundsInSession = 3,
        totalSessions = 2,
        focusLength = 25,
        breakLength = 5,
        longBreakLength = 25
    )

    val points = mutableStateOf(0)

    private val dingSound: MediaPlayer = MediaPlayer.create(this.getApplication(), R.raw.timer_ding)

    @SuppressLint("StaticFieldLeak")
    lateinit var timerService : TimerService

    var userState = mutableStateOf("")
    var seconds = mutableStateOf("00")
        private set
    var minutes = mutableStateOf("00")
        private set
    var hours = mutableStateOf("00")
        private set

    var onTickEvent: () -> Unit = {}
    var onTimerFinished: () -> Unit = {}
    var onSync: () -> Unit = {}

    var loadedPreset = defaultPreset
        private set

    var presetName = mutableStateOf( loadedPreset.name )
        private set
    var elapsedRounds =  mutableIntStateOf(0 )
        private set
    var elapsedSessions = mutableIntStateOf( 0 )
        private set
    var finishedPreset = mutableStateOf( false )
        private set
    private var hasSkipped = false

    // For exposing the current timer start length
    var currentTimerLength = mutableStateOf( 0.seconds )

    var isBreak = mutableStateOf( false )
        private set
    private var isSetup = false
    var currentState = mutableStateOf( TimerService.State.Idle )

    private fun setup() {
        currentState = timerService.currentState
        currentTimerLength = timerService.currentTimeInSeconds
        timerService.currentTimeInSeconds.value = if (!isBreak.value) {
            loadedPreset.focusLength.minutes
        } else {
            if(Math.floorMod(elapsedRounds.intValue, loadedPreset.roundsInSession) == 0) {
                loadedPreset.longBreakLength.minutes
            } else {
                loadedPreset.breakLength.minutes
            }
        }
        isSetup = true
    }

    private fun sendFakeTransitionEvent() {
        val intent = Intent(this.getApplication(), ActivityTransitionReceiver::class.java)
        val events: ArrayList<ActivityTransitionEvent> = arrayListOf()

        // create fake events
        events.add(
            ActivityTransitionEvent(
                DetectedActivity.ON_BICYCLE,
                ActivityTransition.ACTIVITY_TRANSITION_ENTER,
                SystemClock.elapsedRealtimeNanos()
            )
        )

        // finally, serialize and send
        val result = ActivityTransitionResult(events)
        SafeParcelableSerializer.serializeToIntentExtra(
            result,
            intent,
            "com.google.android.location.internal.EXTRA_ACTIVITY_TRANSITION_RESULT"
        )
        this.getApplication<PomodoroApplication>().sendBroadcast(intent)
    }

    fun start () {
        if(!isSetup) {
            setup()
            isSetup = true
        }
        timerService.start(
            onTickEvent = {
//                this.currentTimerLength.value = timerService.currentTimeInSeconds.value
                onTickEvent()
                this.sendFakeTransitionEvent()
                if(currentTimerLength.value.toInt(DurationUnit.SECONDS) % 5 == 0) {
                    points.value += 1 * BonusMultiplierManager.getMultiplier()
                }
                updateTimeUnits()
            },
            onTimerFinish = {
                //TODO Skip causes onTimerFinished to be called repeatedly
                //TODO Tilting device causes interface to lose track of state
                onTimerFinished()
                dingSound.start()
                this.progressTimer()
                pause()
                isSetup = false
                if(!this.finishedPreset.value) {
                    start()
                }
            }
        )
    }

    private fun updateTimeUnits() {
        this.currentTimerLength.value.toComponents { hours, minutes, seconds, _ ->
            this@ActiveTimerViewModel.hours.value = hours.toInt().pad()
            this@ActiveTimerViewModel.minutes.value = minutes.pad()
            this@ActiveTimerViewModel.seconds.value = seconds.pad()
        }
    }

    fun loadPreset(id: Int) {
        presetRepository.getPresetStream(id).let { flow ->
            viewModelScope.launch {
                try {

                    flow.first {preset ->
                        if(preset != null) {
                            preset.id == id
                        } else {
                            false
                        }
                    }?.run {
                        loadedPreset = this
                    }
                } catch (error: Error) {
                    Log.d("DB Access with id $id:", error.toString())
                }
            }
        }
    }


    private fun progressTimer() {
        isBreak.value = !isBreak.value
        if(isBreak.value) {
            this.elapsedRounds.intValue += 1
        }
        // Updates elapsed sessions each time elapsed rounds is max
        if(elapsedRounds.intValue != 0 && Math.floorMod(elapsedRounds.intValue, loadedPreset.roundsInSession)==0) {
            this.elapsedSessions.intValue += 1
            elapsedRounds.intValue = 0
        }
        if(this.elapsedSessions.intValue == loadedPreset.totalSessions) {
            finishedPreset.value = true
        }
    }

    fun refresh() {
        updateTimeUnits()
        if(!isSetup) {
            setup()
            refresh()
            sync()
            isSetup = true
        }
    }

    fun skip() {
        if(!finishedPreset.value) {
            hasSkipped = true
            pause()
            isSetup = false
            progressTimer()
            refresh()
        }
    }

    fun end() {
        finishedPreset.value = true
        timerService.end()
        setup()
        isSetup = true
        updateTimeUnits()
    }

    fun reset() {
        pause()
        elapsedRounds.intValue = 0
        elapsedSessions.intValue = 0
        finishedPreset.value = false
        isBreak.value = false
        setup()
    }

    fun adjustTime(time: Int) {
        timerService.adjustTime(time)
//        currentTimerLength = timerService.currentTimeInSeconds
        updateTimeUnits()
    }

    fun pause() {
        timerService.pause()
    }

    fun sync() {
        onSync()
    }


}
