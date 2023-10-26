package com.example.assignment1.ui.preset

import android.annotation.SuppressLint
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment1.PomodoroTopAppBar
import com.example.assignment1.R
import com.example.assignment1.services.TimerService
import com.example.assignment1.ui.navigation.NavigationDestination
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


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ActiveTimerBody(
    viewModel: ActiveTimerViewModel,
    modifier: Modifier = Modifier
) {

    val scrollScope = rememberCoroutineScope()
    val lightScope = rememberCoroutineScope()
    val scrollState = rememberScrollState(0)
    val currentLightColor = remember { Animatable(Color.DarkGray) }
    val activeBreakLightColor = Color(130, 85, 255, 255)
    val activeFocusLightColor = Color(255, 224, 70, 255)
    val hours by viewModel.hours
    val minutes by viewModel.minutes
    val seconds by viewModel.seconds
    val timerState by viewModel.currentState
    val isBreak by viewModel.isBreak


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

    viewModel.onTimerFinished = {
        lightScope.launch {
            currentLightColor.animateTo(if (isBreak) {
                activeBreakLightColor
            } else {
                activeFocusLightColor
            }, tween(1000))
        }
    }

    viewModel.refresh()





    var scrollEventToHandle by remember { mutableStateOf(false) }


    //TODO: This prevents thread problems, but might want a better solution
    if(scrollState.isScrollInProgress) {
        if (!scrollEventToHandle) {
            viewModel.pause()
            lightScope.launch {
                currentLightColor.animateTo(Color.DarkGray, animationSpec = tween(1000))
            }
        }
        viewModel.currentTimerLength.value = getDurationFromScroll(
            scrollState, 90.minutes, 60
        )
        scrollEventToHandle = true
    } else if (scrollEventToHandle) {
        scrollEventToHandle = false
        if(scrollState.value != getScrollFromDuration(
                viewModel.currentTimerLength.value,
                90.minutes,
                scrollState.maxValue, null
            )) {
            viewModel.sync()
        }
    }

    viewModel.refresh()

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
//        Column() {
//            Text("scrollEvent:$scrollEvent  syncEvent: $isSyncing")
//            Text("${viewModel.currentTimerLength.value.toInt(DurationUnit.SECONDS).toFloat() /
//                    90.minutes.toInt(DurationUnit.SECONDS).toFloat()}")
//            Text("${scrollState.value.toFloat() / scrollState.maxValue.toFloat()}")
//            Text(if(isBreak){"break"}else{"focus"})
//        }
        Spacer(Modifier.height(50.dp))
        // INFO DISPLAY
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

            Row() {
                LitContainer(
                    lightColor = currentLightColor.value,
                    height = 100f,
                    rounding = 6.dp
                ) {
                    Text("${viewModel.elapsedRounds.intValue} / ${viewModel.loadedPreset.roundsInSession}")
                }
                Spacer(Modifier.width(100.dp))
                LitContainer(
                    lightColor = currentLightColor.value,
                    height = 100f,
                    rounding = 6.dp
                ) {
                    Text("${viewModel.elapsedSessions.intValue} / ${viewModel.loadedPreset.totalSessions}")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LitContainer(
                lightColor = currentLightColor.value,
                height = 300f,
                rounding = 16.dp
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    TimerDisplay(hours = hours, minutes = minutes, seconds = seconds)
                }

            }

            //TimerDisplay(hours, minutes, seconds)
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
                    viewModel.reset()
                    viewModel.sync()
                    lightScope.launch {
                        currentLightColor.animateTo(Color.DarkGray, animationSpec = tween(1000))
                    }
                },
                size = 80.dp
            )
            PlayPauseButton(
                timerIsRunning = timerState == TimerService.State.Running,
                onPlay = {
                    viewModel.start()
                    lightScope.launch {

                        currentLightColor.animateTo(
                            if(isBreak) {activeBreakLightColor} else {activeFocusLightColor},
                            animationSpec = tween(1000)
                        )
                    }
                         },
                onPause = {
                    viewModel.pause()
                    lightScope.launch {
                        currentLightColor.animateTo(Color.DarkGray, animationSpec = tween(1000))
                    }
                          },
                size = 120.dp
            )
            Box(
                modifier = Modifier
                    .requiredSize(80.dp)
                    .background(
                        Brush.linearGradient(listOf(Color.Gray, Color.White, Color.Gray)),
                        RoundedCornerShape(80.dp)
                    )
                    .border(
                        BorderStroke(
                            2.dp,
                            Brush.linearGradient(listOf(Color.White, Color.DarkGray))
                        ),
                        RoundedCornerShape(80.dp)
                    )
                    .clickable {
                        viewModel.skip()
                        viewModel.sync()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("skip", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(30.dp))
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
        style = TextStyle(
            fontSize = 68.sp,
            shadow = Shadow(
                color = Color.White,
//                offset = Offset(10f, 10f),
                blurRadius =20f
            )
        ),
        modifier = Modifier.padding(0.dp)
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
        modifier = Modifier
            .requiredSize(size)
            .background(
                Brush.linearGradient(listOf(Color.Gray, Color.White, Color.Gray)),
                RoundedCornerShape(size)
            )
            .border(
                BorderStroke(2.dp, Brush.linearGradient(listOf(Color.White, Color.DarkGray))),
                RoundedCornerShape(size)
            )
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
                .requiredSize(size/2)
        )
    }
}

@Composable
fun LitContainer(
    lightColor: Color,
    height: Float,
    rounding: Dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                Color.LightGray,
//                Brush.linearGradient(
//                    colors = listOf(Color.White, Color.LightGray)
//                ),
                RoundedCornerShape(rounding)
            )
            .border(
                BorderStroke(
                    2.dp,
                    Brush.linearGradient(
                        colors = listOf(Color.DarkGray, Color.White),
                        start = Offset(0f, 0.0f),
                        end = Offset(0f, height)
                    )
                ),
                RoundedCornerShape(rounding)
            )
    ) {
        Box( // LIGHTING OVERLAY
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(lightColor, Color.Black.copy(alpha = 0.0f)),
                        end = Offset(0f, height)
                    ),
                    alpha = 0.5f,
                    shape = RoundedCornerShape(rounding)
                )
        ) {
            content()
        }
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
        modifier = Modifier
            .requiredSize(size)
            .background(
                Brush.linearGradient(listOf(Color.Gray, Color.White, Color.Gray)),
                RoundedCornerShape(size)
            )
            .border(
                BorderStroke(2.dp, Brush.linearGradient(listOf(Color.White, Color.DarkGray))),
                RoundedCornerShape(size)
            )
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