package com.example.assignment1.ui.preset

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
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
import com.example.assignment1.PomodoroTopAppBar
import com.example.assignment1.R
import com.example.assignment1.services.TimerService
import com.example.assignment1.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
import kotlin.math.abs
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



@Composable
fun ActiveTimerBody(
    viewModel: ActiveTimerViewModel,
    modifier: Modifier = Modifier
) {
    viewModel.refresh()
    val scrollScope = rememberCoroutineScope()
    val scrollState = rememberScrollState(
        viewModel.currentTimerLength.value.toInt(DurationUnit.SECONDS)
    )

    viewModel.onTickEvent = {
        scrollScope.launch {
            scrollState.animateScrollTo(
                viewModel.currentTimerLength.value.toInt(DurationUnit.SECONDS),
            )
        }
    }
    viewModel.onTimerFinished = {}

    val timerAdjustCoefficient = 0.01f
    var timerAdjustmentTick by remember { mutableFloatStateOf(0.0f) }

    val hours by viewModel.hours
    val minutes by viewModel.minutes
    val seconds by viewModel.seconds
    val timerState by viewModel.currentState


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row() {
            Text("${viewModel.elapsedRounds.intValue} / ${viewModel.loadedPreset.roundsInSession}")
            Spacer(Modifier.width(100.dp))
            Text("${viewModel.elapsedSessions.intValue} / ${viewModel.loadedPreset.totalSessions}")
        }

        TimerDisplay(hours, minutes, seconds)
        TimerAdjustmentBar(
            scrollState = scrollState,
            onDragEnd = { timerAdjustmentTick = 0.0f },
            onAdjustment = { adjustment ->
                timerAdjustmentTick += adjustment * timerAdjustCoefficient
                if (abs(timerAdjustmentTick) > 1) {
                    viewModel.adjustTime(timerAdjustmentTick.toInt() * -60)
                    timerAdjustmentTick = 0.0f
                    scrollScope.launch {
                        scrollState.animateScrollTo(
                            viewModel.currentTimerLength.value.toInt(
                                DurationUnit.SECONDS
                            )
                        )
                    }
                }
            }
        )
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
                },
                size = 75.dp
            )
            Button(
                onClick = { viewModel.skip()}
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