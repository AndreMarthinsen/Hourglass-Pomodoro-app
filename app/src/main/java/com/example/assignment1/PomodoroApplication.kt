package com.example.assignment1

import android.app.Application
import com.example.assignment1.data.AppContainer
import com.example.assignment1.data.AppDataContainer

/**
 * Application class for the Pomodoro app adding a container for dependency injection.
 * Used to initialize the AppContainer.
 * */
class PomodoroApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}