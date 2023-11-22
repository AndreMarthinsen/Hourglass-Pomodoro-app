package com.example.assignment1.view_models

import android.annotation.SuppressLint
import android.app.Application
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.assignment1.data.preset.Preset
import com.example.assignment1.data.preset.PresetRepository
import com.example.assignment1.services.TimerService
import kotlinx.coroutines.flow.first
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import androidx.lifecycle.viewModelScope
import com.example.assignment1.R
import com.example.assignment1.data.Settings
import com.example.assignment1.data.SettingsRepository
import com.example.assignment1.services.BonusManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.DurationUnit




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
    private lateinit var timerService : TimerService

    var seconds = mutableStateOf("00")
        private set
    var minutes = mutableStateOf("00")
        private set
    var hours = mutableStateOf("00")
        private set

    var loadedPreset = defaultPreset
        private set


    var elapsedRounds =  mutableIntStateOf(0 )
        private set
    var elapsedSessions = mutableIntStateOf( 0 )
        private set
    var finishedPreset = mutableStateOf( false )
        private set


    private var _currentTimerLength = mutableStateOf( 0.seconds )
    // For exposing the current timer start length
    val currentTimerLength: State<Duration> get() = _currentTimerLength

    var isBreak = mutableStateOf( false )
        private set
    private var timerServiceIsSetup = false
    var currentState = mutableStateOf( TimerService.State.Idle )
        private set

    init {
        updateTimeUnits(loadedPreset.focusLength.minutes)
        _currentTimerLength.value = loadedPreset.focusLength.minutes
    }

    fun setTimerService(timerService: TimerService) {
        val timerStartValue = _currentTimerLength.value
        this.timerService = timerService
        currentState = timerService.currentState
        _currentTimerLength = timerService.currentTimeInSeconds
        _currentTimerLength.value = timerStartValue
    }




    //Sets the new state of showCoinWarning
    fun updateShowCoinWarning(newState: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateCoinWarning(newState)
        }
    }

    /**
     * Starts the timerService with the current timer length
     */
    fun start () {
        Log.d("ActiveTimerViewModel", "start called")
        if(!timerServiceIsSetup) {
            setTimerServiceLengthFromPreset()
            timerServiceIsSetup = true
        }
        timerService.start(
            onTickEvent = {
                if(_currentTimerLength.value.toInt(DurationUnit.SECONDS) % 5 == 0) {
                    points.intValue += if(isBreak.value) {
                        BonusManager.getBreakBonus()
                    } else {
                        BonusManager.getFocusBonus()
                    }
                }
                updateTimeUnits(timerService.currentTimeInSeconds.value)
            },
            onTimerFinish = {
                //TODO Skip causes onTimerFinished to be called repeatedly
                //TODO Tilting device causes interface to lose track of state
                depositPoints()
                dingSound.start()
                progressTimer()
                pause()
                timerServiceIsSetup = false
                if(!this.finishedPreset.value) {
                    start()
                }
            }
        )
    }

    private fun depositPoints() {
        val pointsToDeposit = points.intValue
        points.intValue = 0
        viewModelScope.launch {
            settingsRepository.addCurrency(pointsToDeposit)
        }
    }


    private fun updateTimeUnits(duration: Duration) {
        duration.toComponents { hours, minutes, seconds, _ ->
            this@ActiveTimerViewModel.hours.value = hours.toInt().toString().padStart(2, '0')
            this@ActiveTimerViewModel.minutes.value = minutes.toString().padStart(2, '0')
            this@ActiveTimerViewModel.seconds.value = seconds.toString().padStart(2, '0')
        }
    }


    fun loadPreset(id: Int) {
        if(loadedPreset.id != id) {
            pause()
            reset()
            timerServiceIsSetup = false
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
                            this@ActiveTimerViewModel.setTimerServiceLengthFromPreset()
                            this@ActiveTimerViewModel.updateTimeUnits(this@ActiveTimerViewModel.currentTimerLength.value)
                        }
                    } catch (error: Error) {
                        Log.d("DB Access with id $id:", error.toString())
                    }
                }
            }
        }
    }


    /**
     * Progresses the timer to the next round or session, counting up elapsedRounds and elapsedSessions.
     * Updates string time units to reflect the new timer length.
     * Updates timer service with the new timer length.
     */
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
        setTimerServiceLengthFromPreset()
        updateTimeUnits(currentTimerLength.value)
    }

    /**
     * Sets up the timerService with the time of the current preset focusLength or
     * breakLength depending on the current state of isBreak.
     * Calling this method effectively resets the currently elapsed duration.
     */
    private fun setTimerServiceLengthFromPreset() {
        _currentTimerLength.value = if (!isBreak.value) {
            loadedPreset.focusLength.minutes
        } else {
            if(Math.floorMod(elapsedRounds.intValue, loadedPreset.roundsInSession) == 0) {
                loadedPreset.longBreakLength.minutes
            } else {
                loadedPreset.breakLength.minutes
            }
        }
    }


    /**
     * Skips the current timer and progresses it to the next round or session.
     * If the preset is finished, this method does nothing.
     */
    fun skip() {
        points.intValue = 0
        if(!finishedPreset.value) {
            pause()
            timerServiceIsSetup = false
            progressTimer()
        }
    }

    fun end() {
        finishedPreset.value = true
        timerService.end()
        setTimerServiceLengthFromPreset()
        timerServiceIsSetup = true
        updateTimeUnits(timerService.currentTimeInSeconds.value)
    }

    fun reset() {
        pause()
        points.intValue = 0
        elapsedRounds.intValue = 0
        elapsedSessions.intValue = 0
        finishedPreset.value = false
        isBreak.value = false
        setTimerServiceLengthFromPreset()
        updateTimeUnits(currentTimerLength.value)
    }

    fun setTimerLength(duration: Duration) {
        _currentTimerLength.value = duration
        updateTimeUnits(duration)
    }

    fun pause() {
        timerService.pause()
    }
}
