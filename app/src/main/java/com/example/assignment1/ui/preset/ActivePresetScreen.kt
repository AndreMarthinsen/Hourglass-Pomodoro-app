package com.example.assignment1.ui.preset

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment1.PomodoroTopAppBar
import com.example.assignment1.R
import com.example.assignment1.services.TimerService
import com.example.assignment1.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
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
    viewModel: ActiveTimerViewModel,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            PomodoroTopAppBar(
                title = "Active Preset",
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
    ) {
        innerPadding ->
        ActiveTimerBody(
            viewModel = viewModel,
            modifier = modifier
                .padding(innerPadding)
        )
    }
}

fun getScrollFromDuration(time: Duration, maxTime: Duration, maxScroll: Int, roundingInSeconds: Int?) : Int {
    return if(roundingInSeconds != null) {
        ((time.toInt(DurationUnit.SECONDS).toFloat() /
                maxTime.toInt(DurationUnit.SECONDS).toFloat()*maxScroll) / roundingInSeconds).toInt()*roundingInSeconds
    } else {
        ((time.toInt(DurationUnit.SECONDS).toFloat() /
                maxTime.toInt(DurationUnit.SECONDS).toFloat()*maxScroll)).toInt()
    }
}

fun getDurationFromScroll(scrollState: ScrollState, maxDuration: Duration, roundingInSeconds: Int) : Duration {
    return ((((scrollState.value.toFloat() / scrollState.maxValue.toFloat()) * maxDuration.toInt(DurationUnit.SECONDS))/roundingInSeconds).roundToInt()*roundingInSeconds).seconds
}


@Composable
fun ActiveTimerBody(
    viewModel: ActiveTimerViewModel,
    modifier: Modifier = Modifier
) {

    val scrollScope = rememberCoroutineScope()
    val scrollState = rememberScrollState(
        0//viewModel.currentTimerLength.value.toInt(DurationUnit.SECONDS)
    )
    viewModel.onTickEvent = {
        if(!scrollState.isScrollInProgress) {
            scrollScope.launch {
                scrollState.scrollTo(
                    getScrollFromDuration(
                        viewModel.currentTimerLength.value,
                        90.minutes,
                        scrollState.maxValue, null
                    ))
            }
        }
    }

    var isSyncing by remember { mutableStateOf(false ) }
    viewModel.onSync = {
        scrollScope.launch {
            scrollState.animateScrollTo(
                getScrollFromDuration(
                    viewModel.currentTimerLength.value,
                    90.minutes,
                    scrollState.maxValue, null
                ))
        }
    }

    viewModel.onTimerFinished = {}

    viewModel.refresh()


    val hours by viewModel.hours
    val minutes by viewModel.minutes
    val seconds by viewModel.seconds
    val timerState by viewModel.currentState
    val isBreak by viewModel.isBreak


    var scrollEvent by remember { mutableStateOf(false) }


    //TODO: This prevents thread problems, but might want a better solution
    if(scrollState.isScrollInProgress) {
        if(!isSyncing) {
            if (!scrollEvent) {
                viewModel.pause()
            }
            viewModel.currentTimerLength.value = getDurationFromScroll(
                scrollState, 90.minutes, 60
            )
        }
        scrollEvent = true
    } else if (scrollEvent) {
        scrollEvent = false
        if (!isSyncing) {
            viewModel.sync()
        }
        isSyncing = !isSyncing
    }


    viewModel.refresh()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // debug
//        Column() {
//            Text("scrollEvent:$scrollEvent  syncEvent: $isSyncing")
//            Text("${viewModel.currentTimerLength.value.toInt(DurationUnit.SECONDS).toFloat() /
//                    90.minutes.toInt(DurationUnit.SECONDS).toFloat()}")
//            Text("${scrollState.value.toFloat() / scrollState.maxValue.toFloat()}")
//            Text(if(isBreak){"break"}else{"focus"})
//        }
        Row() {
            Text("${viewModel.elapsedRounds.intValue} / ${viewModel.loadedPreset.roundsInSession}")
            Spacer(Modifier.width(100.dp))
            Text("${viewModel.elapsedSessions.intValue} / ${viewModel.loadedPreset.totalSessions}")
        }

        TimerDisplay(hours, minutes, seconds)
        TimerAdjustmentBar ( scrollState = scrollState )
        Spacer(Modifier.height(30.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        )  {
            PlayPauseButton(
                timerIsRunning = timerState == TimerService.State.Running,
                onPlay = { viewModel.start() },
                onPause = { viewModel.pause() },
                size = 100.dp
            )
            Spacer(Modifier.height(50.dp))
            ResetButton(
                enabled = timerState != TimerService.State.Idle && timerState != TimerService.State.Reset,
                onReset = {
                    viewModel.reset()
                    viewModel.sync()
                },
                size = 75.dp
            )
            Button(
                onClick = {
                    viewModel.skip()
                    viewModel.sync()
                }
            ) {
                Text("skip")
            }
        }
    }

}


/**
 * Displays the current value of the timer.
 */
@Composable
fun TimerDisplay (
    hours: String,
    minutes: String,
    seconds: String
) {
    Text(
        text = "$hours:$minutes:$seconds",
        fontSize = 72.sp
    )
}


/**
 * Button for either starting the timer or pausing it
 */
@Composable
fun PlayPauseButton (
    timerIsRunning: Boolean,
    onPause: () -> Unit,
    onPlay: () -> Unit,
    size: Dp
) {
    IconButton(
        onClick = {
            if (timerIsRunning) { onPause() } else { onPlay() }
        },
        modifier = Modifier.requiredSize(size)
    ) {
        val icon = if (timerIsRunning) {
            painterResource(id = R.drawable.pause_icon)
        } else {
            painterResource(id = R.drawable.play_icon)
        }
        Icon(
            icon, "Play/Pause", Modifier.requiredSize(size)
        )
    }
}


/**
 * Resets the current timer to it's start position.
 * TODO: What reset feature do we actually want here?
 */
@Composable
fun ResetButton(
    enabled: Boolean,
    onReset: () -> Unit,
    size: Dp
) {
    IconButton(
        enabled = enabled,
        onClick = {
            onReset()
        },
        modifier = Modifier.requiredSize(size)
    ) {
        Icon(
            Icons.Filled.Refresh, "Reset button", Modifier.requiredSize(size)
        )
    }
}


/**
 * A scrollable timer wheel allowing for adjusting the current time
 */
@Composable
fun TimerAdjustmentBar (
    scrollState: ScrollState,
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Filled.KeyboardArrowUp, "")
        MinuteMarkers( scrollState )
        Icon(Icons.Filled.KeyboardArrowDown, "")
    }
}


/**
 * A row of elements with indicators for each minute and each chunk of five minutes
 */
@Composable
fun MinuteMarkers (
    scrollState: ScrollState,
) {
    Row(
        modifier = Modifier
            .requiredWidth(300.dp)
            .height(50.dp)
            .horizontalScroll(scrollState)
           ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val spacerWidth = 18.sp
        Text(" ", fontSize = spacerWidth)
        repeat(20) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(){
                    Text("  ", fontSize = spacerWidth)
                    Text("|", fontSize = 26.sp)
                    Text("  ", fontSize = spacerWidth)
                }

                if (it != 0) {
                    Text(((it-1)*5).toString(), fontSize = 14.sp)
                } else {
                    Text("", fontSize = 14.sp)
                }
            }
            repeat(4) {
                Text("  ", fontSize = spacerWidth)
                Text("|", fontSize = 16.sp)
                Text("  ", fontSize = spacerWidth)
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row() {
                Text("  ", fontSize = spacerWidth)
                Text("|", fontSize = 26.sp)
                Text("   ", fontSize = spacerWidth)
            }
        }
    }
}