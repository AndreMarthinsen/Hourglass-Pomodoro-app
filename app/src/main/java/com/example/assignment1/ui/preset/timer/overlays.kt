package com.example.assignment1.ui.preset.timer

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
import androidx.compose.material3.CheckboxColors
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.DetectedActivity

@Composable
fun DebugOverlay(
    debugInfo: Map<String, Any>
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf( false) }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .background(Color.White.copy(alpha=0.5f), RoundedCornerShape(16.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {expanded = !expanded}
            ) {
                Text(if(!expanded){"debug"} else {"close"})
            }
            if(expanded) {
                debugInfo.forEach {
                    Text("${it.key}: ${it.value}", fontSize = 12.sp)
                }
                Column(
                    modifier = Modifier.border(2.dp, Color.Black),
                ) {
                    Text("Send activity:", fontSize = 12.sp)
                    Row {
                        listOf(
                            DetectedActivity.STILL,
                            DetectedActivity.WALKING,
                            DetectedActivity.IN_VEHICLE
                        ).forEach {
                            Button(
                                onClick = { sendFakeTransitionEvent(context, it) }
                            ) {
                                Text(detectedActivityToString(it), fontSize = 8.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ConfirmationOverlay(
    enabled: Boolean,
    onConfirmAction: () -> Unit,
    onReject: () -> Unit,
    onDisable: () -> Unit,
    message: @Composable () -> Unit
) {
    var isDisabled by remember { mutableStateOf(false) }
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
                    Button(onClick = { onReject() }) {
                        Text("No")
                    }
                    Button(onClick = { onConfirmAction() }) {
                        Text("Yes")
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var checked by remember { mutableStateOf(false) }
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { isChecked ->
                            isDisabled = !isDisabled
                            checked = isChecked
                        },
                    )
                    Text("Don't show again")
                }

            }
        }
    }
}