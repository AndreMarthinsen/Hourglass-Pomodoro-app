package com.example.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavController


/**
 *  Window displaying the converted values in celsius, fahrenheit and kelvin
 *  along with a back button for navigating back to the main screen
 *
 *  @param navController controller with Screen.InputScreen route
 *  @param degrees the user input temperature or null on invalid input
 */
@Composable
fun ConvertedScreen (navController: NavController, degrees: Float? ) {
    val convertedTemperatures = listOf(
        Pair(roundFloat(degrees?:0.0f),"°C"),
        Pair(roundFloat(fahrenheitFromCelsius(degrees?:0.0f)),"°F"),
        Pair(roundFloat(kelvinFromCelsius(degrees?:0.0f)),"°K"),
    )
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Column (horizontalAlignment = Alignment.End ) {
                convertedTemperatures.forEach { (temperature, _) -> Text (text="$temperature") }
            }
            Column (horizontalAlignment = Alignment.End ){
                convertedTemperatures.forEach { (_, unit) -> Text (text= unit) }
            }
        }
        Button (
            onClick = { navController.navigate(Screen.InputScreen.route) }
        ) {
            Text(text="Back")
        }
    }
}

