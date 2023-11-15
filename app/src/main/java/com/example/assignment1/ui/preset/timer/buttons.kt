package com.example.assignment1.ui.preset.timer

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.assignment1.R
import com.example.assignment1.ui.visuals.RoundMetalButton


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