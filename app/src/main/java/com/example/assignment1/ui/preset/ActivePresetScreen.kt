package com.example.assignment1.ui.preset

import android.annotation.SuppressLint
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.assignment1.PomodoroTopAppBar
import com.example.assignment1.R
import com.example.assignment1.services.TimerService
import com.example.assignment1.ui.navigation.NavigationDestination
import com.example.assignment1.ui.visuals.LitContainer
import com.example.assignment1.ui.visuals.RoundMetalButton
import kotlinx.coroutines.launch
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
    navController: NavController,
    viewModel: ActiveTimerViewModel,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    presetID: Int
) {
    Scaffold(
        topBar = {
            PomodoroTopAppBar(
                title = "Active Preset",
                canNavigateBack = true,
                navigateUp = navigateBack,
                navController = navController
            )
        },
    ) {
        innerPadding ->
        viewModel.loadPreset(presetID)
        ActiveTimerBody(
            timerViewModel = viewModel,
            modifier = modifier
                .padding(innerPadding)
        )
    }
}



@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ActiveTimerBody(
    timerViewModel: ActiveTimerViewModel,
    modifier: Modifier = Modifier
) {
    val scrollScope = rememberCoroutineScope()
    val lightScope = rememberCoroutineScope()
    val scrollState = rememberScrollState(0)
    val currentLightColor = remember { Animatable(Color.DarkGray) }
    val activeBreakLightColor = Color(130, 85, 255, 255)
    val activeFocusLightColor = Color(255, 224, 70, 255)
    val hours by timerViewModel.hours
    val minutes by timerViewModel.minutes
    val seconds by timerViewModel.seconds
    val timerState by timerViewModel.currentState
    val isOnBreak by timerViewModel.isBreak


    timerViewModel.onTickEvent = {
        if(!scrollState.isScrollInProgress) {
            scrollScope.launch {
                scrollState.scrollTo(
                    getScrollFromDuration(
                        timerViewModel.currentTimerLength.value,
                        90.minutes,
                        scrollState.maxValue, null
                    ))
            }
        }
    }

    timerViewModel.onSync = {
        scrollScope.launch {
            scrollState.animateScrollTo(
                getScrollFromDuration(
                    timerViewModel.currentTimerLength.value,
                    90.minutes,
                    scrollState.maxValue, null
                ))
        }
    }

    timerViewModel.onTimerFinished = {
        lightScope.launch {
            currentLightColor.animateTo(if (isOnBreak) {
                activeBreakLightColor
            } else {
                activeFocusLightColor
            }, tween(1000))
        }
    }

    timerViewModel.refresh()

    var scrollEventToHandle by remember { mutableStateOf(false) }
    
    //TODO: This prevents thread problems, but might want a better solution
    if(scrollState.isScrollInProgress) {
        if (!scrollEventToHandle) {
            timerViewModel.pause()
            lightScope.launch {
                currentLightColor.animateTo(Color.DarkGray, animationSpec = tween(1000))
            }
        }
        timerViewModel.currentTimerLength.value = getDurationFromScroll(
            scrollState, 90.minutes, 60
        )
        scrollEventToHandle = true
    } else if (scrollEventToHandle) {
        scrollEventToHandle = false
        if(scrollState.value != getScrollFromDuration(
                timerViewModel.currentTimerLength.value,
                90.minutes,
                scrollState.maxValue, null
            )) {
            timerViewModel.sync()
        }
    }

    timerViewModel.refresh()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Black, Color.DarkGray, Color.Black)
                )
            )
            .padding(30.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(50.dp))
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(listOf(Color.Gray, Color.White, Color.Gray)),
                    RoundedCornerShape(16.dp)
                )
                .fillMaxWidth()
                .border(
                    BorderStroke(
                        2.dp,
                        Brush.linearGradient(listOf(Color.DarkGray, Color.Gray, Color.LightGray))
                    ),
                    RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ProgressDisplay(
                lightColor = currentLightColor.value,
                maxRounds = timerViewModel.loadedPreset.roundsInSession,
                elapsedRounds = timerViewModel.elapsedRounds.intValue,
                maxSessions = timerViewModel.loadedPreset.totalSessions,
                elapsedSessions = timerViewModel.elapsedSessions.intValue
            )
            Spacer(modifier = Modifier.height(8.dp))
            TimerDisplay(
                lightColor = currentLightColor.value,
                hours = hours, minutes = minutes , seconds = seconds)
            TimerAdjustmentBar (
                scrollState = scrollState,
                lightColor = currentLightColor.value
            )
        }

        // BUTTON ROW
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        )  {
            ResetButton(
                enabled = timerState != TimerService.State.Idle && timerState != TimerService.State.Reset,
                onReset = {
                    timerViewModel.reset()
                    timerViewModel.sync()
                    lightScope.launch {
                        currentLightColor.animateTo(Color.DarkGray, animationSpec = tween(1000))
                    }
                },
                size = 80.dp
            )
            PlayPauseButton(
                timerIsRunning = timerState == TimerService.State.Running,
                onPlay = {
                    timerViewModel.start()
                    lightScope.launch {
                        currentLightColor.animateTo(
                            if (isOnBreak) {
                                activeBreakLightColor
                            } else {
                                activeFocusLightColor
                            },
                            animationSpec = tween(1000)
                        )
                    }},
                onPause = {
                    timerViewModel.pause()
                    lightScope.launch {
                        currentLightColor.animateTo(Color.DarkGray, animationSpec = tween(1000))
                    }},
                size = 120.dp
            )
            SkipButton(
                onClick = {
                    timerViewModel.skip()
                    timerViewModel.sync()
                }
            )
        }
        Spacer(Modifier.height(30.dp))
    }
}


@Composable
fun ProgressDisplay(
    lightColor: Color,
    maxRounds: Int,
    elapsedRounds: Int,
    maxSessions: Int,
    elapsedSessions: Int
) {
    Row {
        LitContainer(
            lightColor = lightColor,
            height = 100f,
            rounding = 6.dp
        ) {
            Text("$elapsedRounds / $maxRounds")
        }
        Spacer(Modifier.width(100.dp))
        LitContainer(
            lightColor = lightColor,
            height = 100f,
            rounding = 6.dp
        ) {
            Text("$elapsedSessions / $maxSessions")
        }
    }
}



/**
 * Displays the current value of the timer.
 */
@Composable
fun TimerDisplay (
    lightColor: Color,
    hours: String,
    minutes: String,
    seconds: String
) {
    LitContainer(
        lightColor = lightColor,
        height = 300f,
        rounding = 16.dp
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(
                text = "$hours:$minutes:$seconds",
                style = TextStyle(
                    fontSize = 68.sp,
                    shadow = Shadow(
                        color = Color.White,
                        blurRadius =20f
                    )
                ),
                modifier = Modifier.padding(0.dp)
            )
        }
    }
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
    RoundMetalButton(
        size = size,
        onClick = {
            if (timerIsRunning) { onPause() } else { onPlay() }
        }
    ) {
        val icon = if (timerIsRunning) {
            painterResource(id = R.drawable.pause_icon)
        } else {
            painterResource(id = R.drawable.play_icon)
        }
        Icon(
            painter = icon,
            contentDescription = "Play/Pause",
            modifier = Modifier
                .requiredSize(size / 2)
                .blur(2.dp),
            tint = Color.Black
        )
        Icon(
            painter = icon,
            contentDescription = "Play/Pause",
            modifier = Modifier
                .requiredSize(size/2),
            tint = Color.LightGray
        )
    }
}

@Composable
fun SkipButton(
    onClick : () -> Unit
) {
    RoundMetalButton(size = 80.dp, onClick = { onClick() }) {
        Text("skip", fontWeight = FontWeight.Bold)
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
    RoundMetalButton(
        onClick = {
            onReset()
        },
        size = size
    ) {
        Icon(
            Icons.Filled.Refresh, "Reset button", Modifier.requiredSize(size/2)
        )
    }
}


/**
 * A scrollable timer wheel allowing for adjusting the current time
 */
@Composable
fun TimerAdjustmentBar (
    scrollState: ScrollState,
    lightColor: Color
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Filled.KeyboardArrowUp, "")
        MinuteMarkers( scrollState, lightColor )
        Icon(Icons.Filled.KeyboardArrowDown, "")
    }
}


/**
 * A row of elements with indicators for each minute and each chunk of five minutes
 */
@Composable
fun MinuteMarkers (
    scrollState: ScrollState,
    lightColor: Color
) {
    Box(modifier = Modifier
        .requiredWidth(300.dp)
        .background(
            Brush.linearGradient(
                colors = listOf(Color.Gray, Color.White, Color.Gray)
            ),
            RoundedCornerShape(8.dp)
        )
        .border(
            BorderStroke(
                2.dp, Brush.linearGradient(
                    colors = listOf(Color.DarkGray, Color.White),
                    start = Offset(0f, 0.0f),
                    end = Offset(50f, 300f)
                )
            ),
            RoundedCornerShape(8.dp)
        )
        .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .requiredWidth(300.dp)
                .horizontalScroll(scrollState)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(lightColor, Color.Black.copy(alpha = 0.0f)),
                        end = Offset(0f, 200f)
                    ),
                    alpha = 0.5f,
                    shape = RoundedCornerShape(8.dp)
                )
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
}


/**
 * getScrollFromDuration calculates a scrollState value based on elapsed duration
 * divided by max duration in range [0, maxScroll]
 *
 * @param time - elapsed duration
 * @param maxTime - max duration
 * @param maxScroll - max value possible for a particular scrollable composable
 * @param roundingInSeconds - Rounds time to nearest n seconds.
 * @return Int in range [0, maxScroll]
 */
fun getScrollFromDuration(time: Duration, maxTime: Duration, maxScroll: Int, roundingInSeconds: Int?) : Int {
    return if(roundingInSeconds != null) {
        ((time.toInt(DurationUnit.SECONDS).toFloat() /
                maxTime.toInt(DurationUnit.SECONDS).toFloat()*maxScroll) / roundingInSeconds).toInt()*roundingInSeconds
    } else {
        ((time.toInt(DurationUnit.SECONDS).toFloat() /
                maxTime.toInt(DurationUnit.SECONDS).toFloat()*maxScroll)).toInt()
    }
}


/**
 * getDurationFromScroll calculates a duration from a scrollState where the returned duration
 * is maxDuration * (current scroll / max scroll).
 * @param scrollState - scrollState of scrollable used to calculate duration
 * @param maxDuration - max duration
 * @param roundingInSeconds - rounds resulting time to nearest n seconds
 * @return Duration in  range [0, maxDuration]
 */
fun getDurationFromScroll(scrollState: ScrollState, maxDuration: Duration, roundingInSeconds: Int) : Duration {
    return ((((scrollState.value.toFloat() / scrollState.maxValue.toFloat()) * maxDuration.toInt(DurationUnit.SECONDS))/roundingInSeconds).roundToInt()*roundingInSeconds).seconds
}