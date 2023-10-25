package com.example.assignment1.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import androidx.compose.runtime.mutableStateOf
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds



fun formatDurationDifference(targetDuration: Duration, elapsedDuration: Duration) : String {
    return (targetDuration - elapsedDuration).toComponents { hours, minutes, seconds, _ ->
        "${hours.toInt().pad()}:${minutes.pad()}:${seconds.pad()}"
    }
}

fun Int.pad(): String {
    return this.toString().padStart(2, '0')
}

class TimerService : Service() {

    /**
     * Enumerator encoding the current state of the timer managed by the service
     */
    enum class State {
        Idle,
        Running,
        Paused,
        Reset
    }

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    // Timer object that
    private lateinit var timer: Timer
    private val binder = TimerBinder()
    private var targetTime = 0.seconds



    var currentTimeInSeconds =  mutableStateOf (Duration.ZERO)

    var seconds = mutableStateOf("00")
        private set
    var minutes = mutableStateOf("00")
        private set
    var hours = mutableStateOf("00")
        private set
    var currentState = mutableStateOf( State.Idle )
        private set


    override fun onBind(p0: Intent?) = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }


    fun configure(
        targetTime: Duration,
        startTime: Duration,
    ) {
        this.targetTime = targetTime
        this.currentTimeInSeconds.value = startTime
        updateTimeUnits()
    }


    /**
     * Starts a new timer in a separate thread. Will terminate any ongoing
     * timer before starting a new one.
     *
     * @param onTickEvent A lambda called every second while the timer is running
     */
    fun start(
        onTickEvent: () -> Unit,
        onTimerFinish: () -> Unit
    ) {
        currentState.value = State.Running
        if(this::timer.isInitialized) {
            timer.cancel() // Terminates timer thread
        }
        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            currentTimeInSeconds.value = currentTimeInSeconds.value.minus(1.seconds)
            updateTimeUnits()
            onTickEvent()
            if (currentTimeInSeconds.value <= 0.seconds) {
                onTimerFinish()
            }
        }
    }


    /**
     * Stops the timer without resetting elapsedSeconds.
     */
    fun pause() {
        //TODO: Find way of keeping track of sub second elapsed time for better resume
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        currentState.value = State.Paused
    }


    /**
     * Stops the timer if running and resets elapsedSeconds. State is set to Reset
     * only if the timer was running when end() was called.
     */
    fun end() {
        currentTimeInSeconds.value = Duration.ZERO
        if (this::timer.isInitialized) {
            timer.cancel()
            currentState.value = State.Reset
            updateTimeUnits()
        }
    }

    /**
     * adjustTime() adds or subtracts seconds from elapsedSeconds. If elapsedSeconds
     * would go below zero, the final value is clamped to zero.
     *
     * @param timeDelta Seconds to add or subtract from the timer.
     */
    fun adjustTime(timeDelta: Int) {
        currentTimeInSeconds.value = currentTimeInSeconds.value.plus(timeDelta.seconds)

        if(currentTimeInSeconds.value < 0.seconds) {
            currentTimeInSeconds.value = 0.seconds
        }
        updateTimeUnits()
    }


    /**
     * Sets the current time in seconds duration to a new duration.
     * Negative durations are ignored.
     */
    fun setTime(newDurationInSeconds: Duration) {
        if (newDurationInSeconds >= 0.seconds) {
            currentTimeInSeconds.value = newDurationInSeconds
            updateTimeUnits()
        }
    }



    /**
     * Updates the string representation of the time units hours, minutes and
     * seconds.
     */
    private fun updateTimeUnits() {
        currentTimeInSeconds.value.toComponents { hours, minutes, seconds, _ ->
            this@TimerService.hours.value = hours.toInt().pad()
            this@TimerService.minutes.value = minutes.pad()
            this@TimerService.seconds.value = seconds.pad()
        }
    }
}

