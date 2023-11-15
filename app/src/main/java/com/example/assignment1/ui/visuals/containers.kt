package com.example.assignment1.ui.visuals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A container emulating a lit, indented metallic surface. The content is
 * displayed on top of the visual.
 *
 * @param lightColor - color used to 'light' the container
 * @param height - height offset used for the gradient of the lighting top down
 * @param rounding - rounding of the containers corners
 * @param content - content to be displayed in the lit container surface
 */
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

@Composable
fun MetallicContainer(
    height: Float,
    rounding: Dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                Color.LightGray,
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
        content()
    }
}



@Composable
fun RoundMetalButton(
    size: Dp,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .requiredSize(size)
            .background(
                Brush.linearGradient(listOf(Color.Gray, Color.White, Color.Gray)),
                RoundedCornerShape(size)
            )
            .border(
                BorderStroke(
                    2.dp,
                    Brush.linearGradient(listOf(Color.White, Color.DarkGray))
                ),
                RoundedCornerShape(size)
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}


