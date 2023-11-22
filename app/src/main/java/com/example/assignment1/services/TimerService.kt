package com.example.assignment1.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import androidx.compose.runtime.mutableStateOf
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


/**
 * TimerService is a service that manages a timer. It is intended to be used
 * with the ActiveTimerViewModel to provide management of state when the
 * application is minimized.
 */
class TimerService : Service() {

    // Timer used to instantiate a new thread where a lambda is called every n milliseconds
    private lateinit var timer: Timer

    private val binder = TimerBinder()

    @Volatile
    var currentTimeInSeconds =  mutableStateOf (Duration.ZERO)

    var currentState = mutableStateOf( State.Idle )
        private set

    override fun onBind(p0: Intent?) = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
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
            timer.cancel() // Terminates timer thread if already running.
        }
        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            currentTimeInSeconds.value = currentTimeInSeconds.value.minus(1.seconds)
            onTickEvent()
            if (currentTimeInSeconds.value <= 0.seconds) {
                onTimerFinish()
            }
        }
    }


    /**
     * Stops the timer without resetting elapsed time.
     */
    fun pause() {
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
        }
    }



    /**
     * Enumerator encoding the current state of the timer managed by the service
     */
    enum class State {
        Idle,
        Running,
        Paused,
        Reset
    }

    /**
     * Binder class used to bind the service to an activity
     */
    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }
}

