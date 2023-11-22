package com.example.assignment1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment1.utility.detectedActivityToString
import com.example.assignment1.utility.sendFakeTransitionEvent
import com.google.android.gms.location.DetectedActivity


/**
 * Overlay providing debug features for the application. Displays informmation
 * about registered user activity and allows the user to send fake activity events
 * as if they were coming from the Google Play Activity Detection API.
 *
 * @param debugInfo - map of debug information to be displayed
 */
@Preview
@Composable
fun DebugOverlay(
    debugInfo: Map<String, Any> = mapOf(
        "Activity" to "Still",
        "Confidence" to 100,
        "Pomodoro" to 0)
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf( false) }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {expanded = !expanded}
            ) {
                Text(if(!expanded){"debug"} else {"close"})
            }
            if(expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha=0.5f), RoundedCornerShape(16.dp))
                        .border(2.dp, Color.Black)
                        .padding(16.dp),
                ) {
                    debugInfo.forEach {
                        Text("${it.key}: ${it.value}", fontSize = 12.sp)
                    }
                    Column {
                        Text("Send activity:", fontSize = 12.sp)
                        Row {
                            listOf(
                                DetectedActivity.STILL,
                                DetectedActivity.WALKING,
                                DetectedActivity.IN_VEHICLE,
                                DetectedActivity.RUNNING
                            ).forEach {
                                Button(
                                    onClick = { sendFakeTransitionEvent(context, it) }
                                ) {
                                    Text(detectedActivityToString(it), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}


/**
 * Overlay that displays a generic confirmation dialog. The content appears
 * centered on the screen with a darkened background. The overlay is only displayed
 * when the enabled parameter is true.
 *
 * @param enabled whether the overlay should be displayed
 * @param onConfirmAction action to be performed when the user confirms
 * @param onReject action to be performed when the user rejects
 * @param onDisableWarning action to be performed when the user disables the warning
 * @param message content to be displayed on the overlay. Should give the user information about what they are confirming
 */
@Preview
@Composable
fun ConfirmationOverlay(
    enabled: Boolean = true,
    onConfirmAction: () -> Unit = {},
    onReject: () -> Unit = {},
    onDisableWarning: () -> Unit = {},
    message: @Composable () -> Unit = {Text("You're about to do something. Are you sure?")}
) {
    var isDisabled by remember { mutableStateOf(false) }
    var disableWarningsChecked by remember { mutableStateOf(false) }
    if(enabled) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                message()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        onReject()
                        if(disableWarningsChecked) {
                            onDisableWarning()
                        }
                    }) {
                        Text("No")
                    }
                    Button(onClick = {
                        onConfirmAction()
                        if(disableWarningsChecked) {
                            onDisableWarning()
                        }
                    }) {
                        Text("Yes")
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Checkbox(
                        checked = disableWarningsChecked,
                        onCheckedChange = { isChecked ->
                            isDisabled = !isDisabled
                            disableWarningsChecked = isChecked
                        },
                    )
                    Text("Don't show again")
                }

            }
        }
    }
}