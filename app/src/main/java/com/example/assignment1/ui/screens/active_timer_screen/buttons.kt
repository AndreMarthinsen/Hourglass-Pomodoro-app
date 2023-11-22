package com.example.assignment1.ui.screens.active_timer_screen

import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.assignment1.R
import com.example.assignment1.ui.components.RoundMetalButton


/**
 * Button for either starting the timer or pausing it
 *
 * @param timerIsRunning Whether the timer is currently running. Will determine icon and lambda being used when clicked.
 * @param onPause Callback for when the timer is paused
 * @param onPlay Callback for when the timer is started
 * @param size Size of the button
 */
@Preview
@Composable
fun PlayPauseButton (
    timerIsRunning: Boolean = true,
    onPause: () -> Unit = {},
    onPlay: () -> Unit = {},
    size: Dp = 80.dp
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


/**
 * Button for skipping the current timer
 *
 * @param onClick Callback for when the button is clicked
 */
@Preview
@Composable
fun SkipButton(
    onClick : () -> Unit = {}
) {
    RoundMetalButton(size = 80.dp, onClick = { onClick() }) {
        Text("skip", fontWeight = FontWeight.Bold)
    }
}


/**
 * Resets the current timer to it's start position.
 *
 * @param onReset Callback for when the button is clicked
 * @param size Size of the button
 */
@Preview
@Composable
fun ResetButton(
    onReset: () -> Unit = {},
    size: Dp = 80.dp
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