package com.example.assignment1.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
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
@Preview
@Composable
fun LitContainer(
    lightColor: Color = Color.Yellow,
    height: Float = 80f,
    rounding: Dp = 16.dp,
    content: @Composable () -> Unit = {Text("  Lit Container  ")}
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


/**
 * A container emulating a metallic surface. The content is displayed on top of the
 * visual. The base container is a Box Composable.
 *
 * @param height - height offset used for the gradient metallic sheen.
 * @param rounding - rounding of the containers corners
 * @param content - content to be displayed on the surface
 */
@Preview
@Composable
fun MetallicContainer(
    height: Float = 80f,
    rounding: Dp = 16.dp,
    content: @Composable () -> Unit = {Text("  Metallic Container  ")}
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


/**
 * A round button with a metallic sheen. The content is displayed on top of the
 * visual. Content should be an icon or text indicating the use of the button.
 *
 * @param size - size of the button
 * @param onClick - lambda called when the button is clicked
 * @param content - content to be displayed in the button
 */
@Preview
@Composable
fun RoundMetalButton(
    size: Dp = 80.dp,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {Text("  Button  ")}
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


/**
 * A metallic surface with rounded corners. Borders give an illusion of slight elevation.
 * The content is displayed on top of the metal surface. Content will be centered
 * by default and treated as a column.
 *
 * @param content - content to be displayed on the surface
 */
@Preview
@Composable
fun ShinyMetalSurface(
    content: @Composable () -> Unit = {Text("  Shiny Metal Surface  ")}
) {
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
                    Brush.linearGradient(
                        listOf(
                            Color.DarkGray,
                            Color.Gray,
                            Color.LightGray
                        )
                    )
                ),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        content()
    }

}


/**
 * A container emulating a shiny black surface. The content is displayed on top of the
 * visual. The base container is a Column Composable. Used as a background for the
 * application screens.
 *
 * @param verticalArrangement - vertical arrangement of the content
 * @param horizontalAlignment - horizontal alignment of the content
 * @param content - content to be displayed on the surface
 */
@Preview
@Composable
fun ShinyBlackContainer(
    modifier : Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.SpaceBetween,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable () -> Unit  = {Text("  Shiny Black Container  ", color = Color.White)},

) {
    Column(
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(Color.Black, Color.DarkGray, Color.Black)
                )
            )
            .fillMaxSize()
            .padding(30.dp)
    ){
        content()
    }
}