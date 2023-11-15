package com.example.assignment1

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import com.example.assignment1.ui.theme.ProjectTheme
import com.example.assignment1.services.TimerService
import androidx.navigation.compose.rememberNavController
import com.example.assignment1.ui.navigation.PomodoroNavHost
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import androidx.compose.runtime.*
import com.example.assignment1.recievers.ActivityTransitionReceiver

@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {
    private var isBound = mutableStateOf(false)
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var isActivityPermissionGranted = mutableStateOf(false)
    private lateinit var activityTransitionClient : ActivityRecognitionClient

    private lateinit var timerService: TimerService
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerService.TimerBinder
            timerService = binder.getService()
            isBound.value = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound.value = false
        }
    }

    /**
     * createTransitionList creates a list of ActivityTransitions
     *
     * @return List<ActivityTransition> - A list of ActivityTransitions
     */
    private fun createTransitionList(): List<ActivityTransition> {
        val transitions = listOf(
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.ON_FOOT,
            DetectedActivity.STILL
        )

        return transitions.map {
                ActivityTransition.Builder()
                    .setActivityType(it)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .build()
            }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun registerForActivityDetectionResult() {
       requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            isActivityPermissionGranted.value = isGranted
       }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun requestActivityUpdatePermission() {
        when {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED -> {
                isActivityPermissionGranted.value = true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION) -> {
                //TODO: Is true when the user has denied the request once
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }


    private fun requestActivityUpdates() {
        val transitions = createTransitionList()
        val request = ActivityTransitionRequest(transitions)
        val intent = Intent(this.application, ActivityTransitionReceiver::class.java)
        val pending = PendingIntent.getBroadcast(this.application, 0, intent, PendingIntent.FLAG_MUTABLE)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val task = this.activityTransitionClient.requestActivityTransitionUpdates(request, pending)
//                .requestActivityUpdates(1000, pending);
            task.addOnSuccessListener {
                Log.d("ActivityTransition", "Success")
            }
            task.addOnFailureListener { e: Exception ->
                Log.d("ActivityTransition", "Failure: $e")
            }
        }
    }


    @SuppressLint("VisibleForTests")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        this.activityTransitionClient = ActivityRecognition.getClient(this.application)
        super.onCreate(savedInstanceState)

        registerForActivityDetectionResult()
        requestActivityUpdatePermission()
        requestActivityUpdates()

        Intent(this, TimerService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        setContent {
            ProjectTheme {
                val navController = rememberNavController()
                Surface {
                    if(isBound.value) {
                        PomodoroNavHost(
                            timerService = timerService,
                            navController = navController
                        )
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
    }
}
