package com.example.assignment1.ui.preset

import android.app.Application
import androidx.compose.foundation.ScrollState
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.assignment1.PomodoroTopAppBar
import com.example.assignment1.R
import com.example.assignment1.data.Preset
import com.example.assignment1.services.TimerService
import com.example.assignment1.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
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
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    timerService: TimerService
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
            modifier = modifier
                .padding(innerPadding),
            timerService = timerService
        )
    }
}

class MyViewModelFactory(private val mApplication: Application, private val mParam: String) : ViewModelProvider.Factory {


}



class ActiveTimerViewModel(val timerService: TimerService) : ViewModel() {
    private val defaultPreset = Preset(
        id = 0,
        name = "default",
        roundLength = 3,
        totalSessions = 2,
        focusLength = 3,
        breakLength = 1,
        longBreakLength = 3
    )

    var onTickEvent: () -> Unit = {}
    var onTimerFinished: () -> Unit = {}

    private var loadedPreset = defaultPreset

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

    private var isBreak = false;

    fun start () {
        timerService.currentTimeInSeconds.value = if (!isBreak) {
            loadedPreset.focusLength.minutes
        } else {
            if(Math.floorMod(elapsedRounds.intValue, loadedPreset.roundLength) == 0) {
                loadedPreset.longBreakLength.minutes
            } else {
                loadedPreset.breakLength.minutes
            }
        }
        timerService.start(
            onTickEvent = {
                this.currentTimerLength.value = timerService.currentTimeInSeconds.value
                onTickEvent()
            },
            onTimerFinish = {
                onTimerFinished()
                timerService.end()
                this.updateProgress()
                if(!this.finishedPreset.value) {
                    start()
                }
            }
        )
    }


    private fun updateProgress() {
        if(isBreak) {
            this.elapsedRounds.intValue += 1
        }
        if(Math.floorMod(elapsedRounds.intValue, loadedPreset.roundLength)==0) {
            this.elapsedSessions.intValue += 1
        }
        if(this.elapsedSessions.intValue == loadedPreset.totalSessions) {
            finishedPreset.value = true
        }
    }

    fun skip() {
        isBreak = !isBreak
        hasSkipped = true
        updateProgress()
    }

    fun end() {
        finishedPreset.value = true;
    }

    fun reset() {
        elapsedRounds.intValue = 0;
        elapsedSessions.intValue = 0;
    }


}

@Composable
fun ActiveTimerBody(
    //viewModel: ActiveTimerViewModel,
    modifier: Modifier = Modifier,
    timerService: TimerService
) {
    val scrollScope = rememberCoroutineScope()
    val scrollState = rememberScrollState(
        timerService.currentTimeInSeconds.value.absoluteValue.toInt(DurationUnit.SECONDS)
    )

    var timerReachedEnd by remember { mutableStateOf( false ) }

    val startTimer = {
        timerService.start(onTickEvent = {
            scrollScope.launch {
                scrollState.animateScrollTo(
                    timerService.currentTimeInSeconds.value.toInt(DurationUnit.SECONDS),
                )
            }
        }) {
            timerReachedEnd = true
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimerDisplay(hours = hours, minutes = minutes, seconds = seconds)
        TimerAdjustmentBar(
            scrollState = scrollState,
            onDragEnd = { timerAdjustmentTick = 0.0f }
        ) { adjustment ->
            timerAdjustmentTick += adjustment * timerAdjustCoefficient
            if (abs(timerAdjustmentTick) > 1) {
                timerService.adjustTime(timerAdjustmentTick.toInt() * -60)
                scrollScope.launch {
                    scrollState.animateScrollTo(
                        timerService.currentTimeInSeconds.value.toInt(
                            DurationUnit.SECONDS
                        )
                    )
                }
                timerAdjustmentTick = 0.0f
            }
        }
        Spacer(Modifier.height(30.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        )  {
            PlayPauseButton(
                isPaused = timerService.currentState.value == TimerService.State.Paused,
                onPlay = { startTimer() },
                onPause = { timerService.pause() },
                size = 100.dp
            )
            Spacer(Modifier.height(50.dp))
            ResetButton(
                timerService = timerService,
                onReset = {
                    timerService.end()
                    initialized = false
                },
                size = 75.dp
            )
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
    isPaused: Boolean,
    onPause: () -> Unit,
    onPlay: () -> Unit,
    size: Dp
) {
    IconButton(
        onClick = {
            if (isPaused) { onPause() } else { onPlay() }
        },
        modifier = Modifier.requiredSize(size)
    ) {
        val icon = if (isPaused) {
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
    timerService: TimerService,
    onReset: () -> Unit,
    size: Dp
) {
    IconButton(
        enabled = timerService.currentState.value != TimerService.State.Idle &&
                timerService.currentState.value != TimerService.State.Reset,
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
    onDragEnd: () -> Unit,
    onAdjustment: (adjustment: Float) -> Unit
) {
    Box {
        MinuteMarkers( scrollState )
        Box(
            modifier = Modifier
                .requiredWidth(300.dp)
                .requiredHeight(50.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = onDragEnd
                    ) { _, dragAmount ->
                        onAdjustment(dragAmount.x)
                    }
                }
        ) {

        }
    }
}


/**
 * A row of elements with indicators for each minute and each chunk of five minutes
 */
@Composable
fun MinuteMarkers (
    scrollState: ScrollState
) {
    Row(
        modifier = Modifier
            .requiredWidth(300.dp)
            .height(50.dp)
            .horizontalScroll(scrollState),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(20) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("  |  ", fontSize = 26.sp)
                if (it != 0) {
                    Text(((it-1)*5).toString(), fontSize = 14.sp)
                } else {
                    Text("", fontSize = 14.sp)
                }
            }
            Text("  |    |    |    |  ", fontSize = 16.sp)
        }
    }
}