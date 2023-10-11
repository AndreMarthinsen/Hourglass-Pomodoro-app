package com.example.assignment1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp


/**
 * Window displaying the saved timer presets the user can choose from.
 * The presets should provide some way for the user to start a new session
 * with that particular preset, edit the preset, delete the preset, as well as
 * adding a new preset.
 */
@Composable
fun PresetsWindow() {

}


/**
 *
 */
data class Preset (
    val name: String,
    val roundLength: Int,
    val totalSessions: Int,
    val focusLength: Int,
    val breakLength: Int,
    val longBreakLength: Int,)

/**
 *
 */
class SamplePreset : PreviewParameterProvider<Preset> {
    override val values = sequenceOf(
        Preset("Preset One",1,3, 25, 5, 25)
    )
}




@Preview
@Composable
fun PresetDisplay(
    @PreviewParameter(SamplePreset::class)preset: Preset,
    onExpandInteraction: ()->Unit = {},
    onStart: ()->Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Face,
                "Timer Icon"
            )
            Text( preset.name )
            IconButton(
                enabled = true,
                onClick= onExpandInteraction,
                content = {
                    Icon(
                        Icons.Filled.MoreVert,
                        "Expand options"
                    )
                }
            )
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(Color.Black)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {// TODO: Clock icon
            Text("" + preset.focusLength + " / " + preset.breakLength + "")
            Text("Sessions: " + preset.totalSessions)
            IconButton(
                onClick = onStart,
                content = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon( // TODO: Size won't change with modifier
                            Icons.Filled.KeyboardArrowRight,
                            "Start Icon"
                        )
                    }
                }
            )
        }
    }

}