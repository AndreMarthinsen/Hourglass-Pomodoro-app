package com.example.assignment1.ui.preset

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment1.PomodoroTopAppBar
import com.example.assignment1.R
import com.example.assignment1.services.TimerService
import com.example.assignment1.services.formatTime
import com.example.assignment1.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

/**
 * the navigation destination for the ActivePreset (active timer) screen
 * the route will contain which presetId is currently active
 */
object ActivePresetDestination : NavigationDestination {
    override val route = "preset_active"
    override val titleRes = 1
    const val presetIdArg = "presetId"
    val routeWithArgs = "$route/{$presetIdArg}"
}

/**
 * The display of the currently running timer. If no active timer is selected
 * or active, a default config timer should start if the user presses play.
 *
 * Should show:
 * current timer
 * Options to:
 * start, pause, reset or skip current timer
 * stop the whole timer process
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveTimerScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            PomodoroTopAppBar(
                title = "Active Preset",
                canNavigateBack = true,
                navigateUp = navigateBack)
        },
    ) {
        innerPadding ->
        ActiveTimerBody(
            modifer = modifier
                .padding(innerPadding)
        )
    }
}

@Composable
fun ActiveTimerBody(modifer: Modifier = Modifier) {

}


@Composable
fun TimerScreen(timerService: TimerService) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val scrollScope = rememberCoroutineScope()
        val scrollState = rememberScrollState(
            timerService.currentTimeInSeconds.value.absoluteValue.toInt(DurationUnit.SECONDS)
        )
        var timerFinished by remember { mutableStateOf( false ) }

        val timerFunction = {
            timerService.start(onTickEvent = {
                scrollScope.launch {
                    scrollState.animateScrollTo(
                        timerService.currentTimeInSeconds.value.toInt(DurationUnit.SECONDS),
                    )
                }
            }) {
                timerFinished = true
                timerService.end()
            }
        }

        var initialized by remember { mutableStateOf(false) }
        if(!initialized) {
            timerService.setTime(1.minutes)
            initialized = true
        }

        val hours by timerService.hours
        val minutes by timerService.minutes
        val seconds by timerService.seconds

        val timerAdjustCoefficient = 0.01f
        var timerAdjustmentTick by remember { mutableFloatStateOf(0.0f) }

        var momentum by remember { mutableFloatStateOf( 0.0f ) }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatTime(hours, minutes, seconds),
                fontSize = 72.sp
            )
            Box(
            ){
                Row(
                    modifier = Modifier
                        .requiredWidth(300.dp)
                        .height(50.dp)
                        .horizontalScroll(scrollState),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(100) {
                        Text("  |  ", fontSize = 36.sp)
                        repeat(4) {
                            Text("  |  ", fontSize = 16.sp)
                        }

                    }
                }
                Box(modifier = Modifier
                    .requiredWidth(300.dp)
                    .requiredHeight(50.dp)
                    .pointerInput(Unit) {
                        detectDragGestures (
                            onDragEnd = { timerAdjustmentTick = 0.0f }
                        ) { change, dragAmount ->
                            timerAdjustmentTick += dragAmount.x * timerAdjustCoefficient
                            if ( abs(timerAdjustmentTick) > 1) {
                                timerService.adjustTime(timerAdjustmentTick.toInt() * -60)
                                scrollScope.launch {
                                    scrollState.animateScrollTo(timerService.currentTimeInSeconds.value.toInt(
                                        DurationUnit.SECONDS))
                                }
                                timerAdjustmentTick = 0.0f
                            }
                        }
                    }
                )
            }
            Spacer(Modifier.height(20.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            )  {
                IconButton(
                    onClick = {
                        when (timerService.currentState.value) {
                            TimerService.State.Idle -> timerFunction()
                            TimerService.State.Running -> {
                                timerService.pause()
                            }
                            TimerService.State.Paused -> timerFunction()
                            TimerService.State.Reset -> timerFunction()
                        }
                    }
                ) {
                    val icon = when (timerService.currentState.value) {
                        TimerService.State.Idle -> painterResource(id = R.drawable.play_icon)
                        TimerService.State.Running -> painterResource(id = R.drawable.pause_icon)
                        TimerService.State.Paused ->  painterResource(id = R.drawable.play_icon)
                        TimerService.State.Reset ->  painterResource(id = R.drawable.play_icon)
                    }
                    Icon(
                        icon, "Play/Pause", Modifier.requiredSize(100.dp)
                    )
                }

                IconButton(
                    enabled = timerService.currentState.value != TimerService.State.Idle &&
                            timerService.currentState.value != TimerService.State.Reset,
                    onClick = {
                        timerService.end()
                        initialized = false
                    }, modifier = Modifier.requiredSize(100.dp)
                ) {
                    Icon(
                        Icons.Filled.Refresh, "Play", Modifier.requiredSize(100.dp)
                    )
                }
            }
        }
    }
}
