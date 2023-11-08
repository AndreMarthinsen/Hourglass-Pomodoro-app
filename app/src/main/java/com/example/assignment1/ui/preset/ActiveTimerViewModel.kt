package com.example.assignment1.ui.preset

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import kotlinx.coroutines.launch

class ActiveTimerViewModel(
    private val presetRepository: PresetRepository
) : ViewModel() {
    private val defaultPreset = Preset(
        id = 0,
        name = "default",
        roundsInSession = 3,
        totalSessions = 2,
        focusLength = 25,
        breakLength = 5,
        longBreakLength = 25
    )

//    companion object {
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                MyViewModel(
//                    timerService =
//                )
//            }
//        }
//    }




    @SuppressLint("StaticFieldLeak")
    lateinit var timerService : TimerService

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

    fun start () {
        if(!isSetup) {
            setup()
            isSetup = true
        }
        timerService.start(
            onTickEvent = {
//                this.currentTimerLength.value = timerService.currentTimeInSeconds.value
                onTickEvent()
                updateTimeUnits()
            },
            onTimerFinish = {
                onTimerFinished()
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
