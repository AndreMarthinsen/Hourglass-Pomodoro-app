package com.example.assignment1.ui.preset.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DebugOverlay(
    debugInfo: Map<String, Any>
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .background(Color.White.copy(alpha=0.5f), RoundedCornerShape(16.dp))
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Debug")
            debugInfo.forEach {
                Text("${it.key}: ${it.value}", fontSize = 12.sp)
            }
        }
    }
}
@Composable
fun ConfirmationOverlay(
    enabled: Boolean,
    onConfirmAction: () -> Unit,
    onReject: () -> Unit,
    message: @Composable () -> Unit
) {
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
                    .padding(32.dp),
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
            }
        }
    }

}