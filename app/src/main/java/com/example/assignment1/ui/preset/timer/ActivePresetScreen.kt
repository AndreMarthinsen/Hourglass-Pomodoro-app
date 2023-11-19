package com.example.assignment1.ui.preset.timer

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.assignment1.PomodoroTopAppBar
import com.example.assignment1.R
import com.example.assignment1.data.Settings
import com.example.assignment1.services.TimerService
import com.example.assignment1.ui.navigation.NavigationDestination
import com.example.assignment1.ui.visuals.LitContainer
import com.example.assignment1.ui.visuals.ShinyBlackContainer
import com.example.assignment1.ui.visuals.ShinyMetalSurface
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes


val activeBreakLightColor = Color(130, 85, 255, 255)
val activeFocusLightColor = Color(255, 224, 70, 255)


/**
 * the navigation destination for the ActivePreset (active timer) screen
 * the route will contain which presetId is currently active
 */
object ActivePresetDestination : NavigationDestination {
    override val route = "preset_active"
    override val titleRes = 1
    val invalidID = -1
    val routeNoPreset = "$route/$invalidID"
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
        Log.d("ActiveTimerScreen compose", "presetID: $presetID")
        if(presetID != -1) {
            viewModel.loadPreset(presetID)
        }
        ActiveTimerBody(
            timerViewModel = viewModel,
            modifier = modifier
                .padding(innerPadding)
        )
    }
}


//@SuppressLint("CoroutineCreationDuringComposition")
//@Composable
//fun ActiveTimerBody(
//    timerViewModel: ActiveTimerViewModel,
//    modifier: Modifier = Modifier
//) {
//    val settings : Settings by timerViewModel.settingsUiState.collectAsState()
//    val scrollScope = rememberCoroutineScope()
//    val lightScope = rememberCoroutineScope()
//    val scrollState = rememberScrollState(0)
//    val currentLightColor = remember { Animatable(Color.DarkGray) }
//    val shouldPrompt = remember { mutableStateOf(false) }
//    timerViewModel.refresh()
//    val onPromptConfirmm: MutableState<() -> Unit> = remember { mutableStateOf({}) }
//    val activeBreakLightColor = Color(130, 85, 255, 255)
//    val activeFocusLightColor = Color(255, 224, 70, 255)
//    val hours by timerViewModel.hours
//    val minutes by timerViewModel.minutes
//    val seconds by timerViewModel.seconds
//    val timerState by timerViewModel.currentState
//    val isOnBreak by timerViewModel.isBreak
//
//    timerViewModel.onTickEvent = {
//        if(!scrollState.isScrollInProgress) {
//            scrollScope.launch {
//                scrollState.scrollTo(
//                    getScrollFromDuration(
//                        timerViewModel.currentTimerLength.value,
//                        90.minutes,
//                        scrollState.maxValue, null
//                    )
//                )
//            }
//        }
//    }
//
//    timerViewModel.onSync = {
//        scrollScope.launch {
//            scrollState.animateScrollTo(
//                getScrollFromDuration(
//                    timerViewModel.currentTimerLength.value,
//                    90.minutes,
//                    scrollState.maxValue, null
//                )
//            )
//        }
//    }
//
//    timerViewModel.onTimerFinished = {
//        lightScope.launch {
//            currentLightColor.animateTo(if (isOnBreak) {
//                activeBreakLightColor
//            } else {
//                activeFocusLightColor
//            }, tween(1000))
//        }
//    }
//
//    timerViewModel.refresh()
//
//    var scrollEventToHandle by remember { mutableStateOf(false) }
//
//    if(scrollState.isScrollInProgress) {
//        if (!scrollEventToHandle) {
//            timerViewModel.pause()
//            lightScope.launch {
//                currentLightColor.animateTo(Color.DarkGray, animationSpec = tween(1000))
//            }
//        }
//        timerViewModel.currentTimerLength.value = getDurationFromScroll(
//            scrollState, 90.minutes, 60
//        )
//        scrollEventToHandle = true
//    } else if (scrollEventToHandle) {
//        scrollEventToHandle = false
//        if(scrollState.value != getScrollFromDuration(
//                timerViewModel.currentTimerLength.value,
//                90.minutes,
//                scrollState.maxValue, null
//            )
//        ) {
//            timerViewModel.sync()
//        }
//    }
//
//    Box {
//        ShinyBlackContainer {
//            Spacer(Modifier.height(50.dp))
//            Column(
//                modifier = Modifier
//                    .background(
//                        Brush.linearGradient(listOf(Color.Gray, Color.White, Color.Gray)),
//                        RoundedCornerShape(16.dp)
//                    )
//                    .fillMaxWidth()
//                    .border(
//                        BorderStroke(
//                            2.dp,
//                            Brush.linearGradient(
//                                listOf(
//                                    Color.DarkGray,
//                                    Color.Gray,
//                                    Color.LightGray
//                                )
//                            )
//                        ),
//                        RoundedCornerShape(16.dp)
//                    )
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                ProgressDisplay(
//                    lightColor = currentLightColor.value,
//                    maxRounds = timerViewModel.loadedPreset.roundsInSession,
//                    elapsedRounds = timerViewModel.elapsedRounds.intValue,
//                    maxSessions = timerViewModel.loadedPreset.totalSessions,
//                    elapsedSessions = timerViewModel.elapsedSessions.intValue
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                TimerDisplay(
//                    lightColor = currentLightColor.value,
//                    hours = hours, minutes = minutes , seconds = seconds)
//                TimerAdjustmentBar (
//                    scrollState = scrollState,
//                    lightColor = currentLightColor.value
//                )
//            }
//
//            CoinDisplay(
//                lightColor = if(timerViewModel.points.intValue != 0) {
//                    currentLightColor.value
//                } else {
//                    Color.DarkGray
//                },
//                coins = timerViewModel.points.intValue
//            )
//
//            // BUTTON ROW
//            Row(
//                Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceEvenly,
//                verticalAlignment = Alignment.CenterVertically
//            )  {
//                ResetButton(
//                    enabled = timerState != TimerService.State.Idle && timerState != TimerService.State.Reset,
//                    onReset = {
//                        timerViewModel.reset()
//                        timerViewModel.sync()
//                        lightScope.launch {
//                            currentLightColor.animateTo(Color.DarkGray, animationSpec = tween(1000))
//                        }
//                    },
//                    size = 80.dp
//                )
//                PlayPauseButton(
//                    timerIsRunning = timerState == TimerService.State.Running,
//                    onPlay = {
//                        timerViewModel.start()
//                        lightScope.launch {
//                            currentLightColor.animateTo(
//                                if (isOnBreak) {
//                                    activeBreakLightColor
//                                } else {
//                                    activeFocusLightColor
//                                },
//                                animationSpec = tween(1000)
//                            )
//                        }},
//                    onPause = {
//                        timerViewModel.pause()
//                        lightScope.launch {
//                            currentLightColor.animateTo(Color.DarkGray, animationSpec = tween(1000))
//                        }},
//                    size = 120.dp
//                )
//                SkipButton(
//                    onClick = {
//                        if( settings.showCoinWarning ) {
//                            onPromptConfirmm.value = {
//                                timerViewModel.skip()
//                                timerViewModel.sync()
//                            }
//                            shouldPrompt.value = true
//                        } else {
//                            timerViewModel.skip()
//                            timerViewModel.sync()
//                        }
//                    }
//                )
//            }
//            Spacer(Modifier.height(30.dp))
//        }
//
//        ConfirmationOverlay(enabled = shouldPrompt.value && settings.showCoinWarning,
//            onConfirmAction = {
//                onPromptConfirmm.value()
//                shouldPrompt.value = false
//            },
//            onReject = {
//                shouldPrompt.value = false
//            },
//            onDisableWarning = {
//                timerViewModel.viewModelScope.launch {
//                    timerViewModel.settingsRepository.updateCoinWarning(false)
//                }
//            }
//        ) {
//            Text("${timerViewModel.points.intValue} coins will be lost")
//            Text("Do you want to proceed?")
//        }
//        DebugOverlay(debugInfo = mapOf(
//            "Timer state:" to timerViewModel.currentState.value,
//            "User activity: " to detectedActivityToString(BonusManager.latestActivity),
//            "Multiplier: " to if(isOnBreak) {BonusManager.getBreakBonus()} else {BonusManager.getFocusBonus() },
//            "Reward" to timerViewModel.points.intValue
//        ))
//    }
//}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ActiveTimerBody(
    timerViewModel: ActiveTimerViewModel,
    modifier: Modifier = Modifier
) {
    val settings : Settings by timerViewModel.settingsUiState.collectAsState()
    val scrollScope = rememberCoroutineScope()
    var scrollEventToHandle by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState(0)
    val currentLightColor = remember { Animatable(Color.DarkGray) }
    var shouldPrompt by remember { mutableStateOf(false) }
    val onPromptConfirm: MutableState<() -> Unit> = remember { mutableStateOf({}) }
    val hours by timerViewModel.hours
    val minutes by timerViewModel.minutes
    val seconds by timerViewModel.seconds
    val timerState by timerViewModel.currentState
    val isOnBreak by timerViewModel.isBreak


    LaunchedEffect(timerViewModel.currentTimerLength.value) {
        if(!scrollState.isScrollInProgress) {
            scrollScope.launch {
                scrollState.scrollTo(
                    getScrollFromDuration(
                        timerViewModel.currentTimerLength.value,
                        90.minutes,
                        scrollState.maxValue, null))
            }
        }
    }


    LaunchedEffect(timerViewModel.currentState.value, timerViewModel.isBreak.value) {
        val targetColor = if (timerViewModel.currentState.value == TimerService.State.Running) {
             if (isOnBreak) {
                activeBreakLightColor
            } else {
                activeFocusLightColor
            }
        } else { Color.DarkGray }
        currentLightColor.animateTo(targetColor, animationSpec = tween(1000))
    }

    if(scrollState.isScrollInProgress) {
        if (!scrollEventToHandle) {
            timerViewModel.pause()
        }
        timerViewModel.setTimerLength( getDurationFromScroll(
            scrollState, 90.minutes, 60
        ))
        scrollEventToHandle = true
    } else if (scrollEventToHandle) {
        scrollEventToHandle = false
        if(scrollState.value != getScrollFromDuration(
                timerViewModel.currentTimerLength.value,
                90.minutes,
                scrollState.maxValue, null
            )
        ) {
            scrollScope.launch {
                scrollState.animateScrollTo(
                        getScrollFromDuration(
                            timerViewModel.currentTimerLength.value,
                            90.minutes,
                            scrollState.maxValue, null
                        )
                    )
            }
        }
    }

    Box {
        ShinyBlackContainer {
            Spacer(Modifier.height(50.dp))
            ShinyMetalSurface {
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

            CoinDisplay(
                lightColor = if(timerViewModel.points.intValue != 0) {
                    currentLightColor.value
                } else {
                    Color.DarkGray
                },
                coins = timerViewModel.points.intValue
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            )  {
                ResetButton(
                    enabled = timerState != TimerService.State.Idle && timerState != TimerService.State.Reset,
                    onReset = { timerViewModel.reset() },
                    size = 80.dp
                )
                PlayPauseButton(
                    timerIsRunning = timerState == TimerService.State.Running,
                    onPlay = { timerViewModel.start() },
                    onPause = { timerViewModel.pause() },
                    size = 120.dp
                )
                SkipButton(
                    onClick = {
                        if( settings.showCoinWarning ) {
                            onPromptConfirm.value = {
                                timerViewModel.skip()
                            }
                            shouldPrompt = true
                        } else {
                            timerViewModel.skip()
                        }
                    }
                )
            }
            Spacer(Modifier.height(30.dp))
        }

        ConfirmationOverlay(enabled = shouldPrompt && settings.showCoinWarning,
            onConfirmAction = {
                onPromptConfirm.value()
                shouldPrompt = false
            },
            onReject = { shouldPrompt = false },
            onDisableWarning = {
                timerViewModel.updateShowCoinWarning(false)
            }
        ) {
            Text("${timerViewModel.points.intValue} coins will be lost")
            Text("Do you want to proceed?")
        }

        DebugOverlay(debugInfo = mapOf(
            "Timer state:" to timerViewModel.currentState.value,
            "User activity: " to detectedActivityToString(BonusManager.latestActivity),
            "Multiplier: " to if(isOnBreak) {BonusManager.getBreakBonus()} else {BonusManager.getFocusBonus() },
            "Reward" to timerViewModel.points.intValue
        ))
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

@Composable
fun CoinDisplay (
    lightColor: Color,
    coins: Int
) {
    LitContainer(
        lightColor = lightColor,
        height = 120f,
        rounding = 16.dp
    ) {
        Row (
            modifier = Modifier
                .width(100.dp)
                .padding(8.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = R.drawable.coin_svgrepo_com),
                contentDescription = "Image of coin with dollar sign",
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = "$coins",
                fontSize = 26.sp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
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
                    fontSize = 60.sp,
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

