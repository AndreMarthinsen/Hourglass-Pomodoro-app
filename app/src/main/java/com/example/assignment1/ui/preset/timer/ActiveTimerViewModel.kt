package com.example.assignment1.ui.preset.timer

import android.annotation.SuppressLint
import android.app.Application
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.assignment1.data.preset.Preset
import com.example.assignment1.data.preset.PresetRepository
import com.example.assignment1.services.TimerService
import com.example.assignment1.services.pad
import kotlinx.coroutines.flow.first
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import androidx.lifecycle.viewModelScope
import com.example.assignment1.PomodoroApplication
import com.example.assignment1.R
import com.example.assignment1.data.Settings
import com.example.assignment1.data.SettingsRepository
import com.example.assignment1.data.dataStore
import com.google.android.gms.location.DetectedActivity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit

object BonusMultiplierManager {
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
}

val BonusActivities = listOf(
    DetectedActivity.WALKING,
    DetectedActivity.RUNNING,
    DetectedActivity.ON_BICYCLE,
    DetectedActivity.ON_FOOT
)


class ActiveTimerViewModel(
    private val presetRepository: PresetRepository,
    private val settingsRepository: SettingsRepository,
    application: Application,
) : AndroidViewModel(application) {
    private val defaultPreset = Preset(
        id = -10000,
        name = "default",
        roundsInSession = 3,
        totalSessions = 2,
        focusLength = 25,
        breakLength = 5,
        longBreakLength = 25
    )

    /**
    * Fetches a stateFlow of settings from the settingsRepository
    * @see updateShowCoinWarning for setting a new value for showing warning
    * */
    val settingsUiState: StateFlow<Settings> =
        settingsRepository.getFromSettingsStore().map {it}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = Settings(0, true)
            )

    val points = mutableIntStateOf(0)

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


    //Sets the new state of showCoinWarning
    fun updateShowCoinWarning(newState: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateCoinWarning(newState)
        }
    }

    fun start () {
        if(!isSetup) {
            setup()
            isSetup = true
        }
        timerService.start(
            onTickEvent = {
//                sendFakeTransitionEvent()
                onTickEvent()
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
        if(loadedPreset.id != id) {
            isSetup = false
            presetRepository.getPresetStream(id).let { flow ->
                viewModelScope.launch {
                    try {
                        flow.first { preset ->
                            if(preset != null) {
                                preset.id == id
                            } else {
                                false
                            }
                        }?.run {
                            Log.d("DB Access with id $id:", "Successfully loaded $this");
                            loadedPreset = this
                            this@ActiveTimerViewModel.setup()
                        }
                    } catch (error: Error) {
                        Log.d("DB Access with id $id:", error.toString())
                    }
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
        this.getApplication<PomodoroApplication>().dataStore.data
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
