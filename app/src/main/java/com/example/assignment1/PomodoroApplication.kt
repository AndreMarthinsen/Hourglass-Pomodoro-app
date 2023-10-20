package com.example.assignment1

import android.app.Application
import com.example.assignment1.data.AppContainer
import com.example.assignment1.data.AppDataContainer

class PomodoroApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}