package com.example.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


/**
 * Main screen with a textfield for entering the temperature along with a
 * drop down menu for selecting the desired temperature unit.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(navController: NavController) {
    var inputText by remember { mutableStateOf("0") }
    var unitSymbol by remember { mutableStateOf("°C") }
    var expanded by remember { mutableStateOf( false ) }
    // A function is used to convert the user input to celsius before
    // passing it on to the conversion page.
    var conversionFunction: (Float) -> Float by remember {
        mutableStateOf({ c -> c })
    }
    val unitSymbolToConversion = listOf(
        Pair("°C") {c: Float -> c},
        Pair("°F", ::celsiusFromFahrenheit),
        Pair("°K", ::celsiusFromKelvin)
    )

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        UsageHint()
        Spacer(modifier = Modifier.height(20.dp))
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Input field
            TemperatureInputField (
                text = inputText,
                onTextChanged = { t -> inputText = t }
            )
            ExposedDropdownMenuBox (
                expanded = expanded ,
                onExpandedChange = { expanded = !expanded }
            ) {
                UnitDropdownText(
                    expanded = expanded,
                    modifier = Modifier.menuAnchor(),
                    unitText = unitSymbol,
                    onClick = { expanded = !expanded }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    // Displays the non selected units only
                    unitSymbolToConversion.forEach { item ->
                        if (item.first != unitSymbol) {
                            UnitDropdownItem(text = item.first) {
                                unitSymbol = item.first
                                conversionFunction = item.second
                            }
                        }
                    }
                }
            }
        }
        ConvertButton(inputText, navController, conversionFunction)
    }
}


/**
 *  Text giving the user hints about usage of the application
 */
@Composable
fun UsageHint() {
    Text(
        text = "Input the temperature of your chosen unit before clicking convert",
        modifier = Modifier.requiredWidth(300.dp),
        fontSize = 32.sp,
        lineHeight = 36.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.primary
    )
}


/**
 *  TextField acting as a dropdown menu expansion butto
 *  @param expanded Sets state of dropdown icon
 *  @param unitText text to be displayed
 *  @param modifier base modifier
 *  @param onClick lambda run on click
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdownText (
    expanded: Boolean,
    unitText: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    TextField(
        value = unitText,
        onValueChange = {},
        readOnly = true,
        label = { Text("Unit") },
        modifier = modifier
            .width(100.dp)
            .clickable { onClick() },
        trailingIcon = {
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
    )
}


/**
 *  A dropdown item
 *
 *  @param text text to be displayed
 *  @param onClick lambda run on dlick
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdownItem (text: String, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(text) },
        onClick = { onClick() },
        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
    )
}


/**
 * A TextField Component where any input that cannot be parsed as a float is
 * ignored.
 *
 *  @param text Initial text of the input field
 *  @param onTextChanged A lambda function taking new input as an argument
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemperatureInputField(text: String, onTextChanged: (String) -> Unit) {
    TextField(
        value = text,
        modifier = Modifier.width(200.dp),
        onValueChange = { it.toFloatOrNull()?.run { onTextChanged(it) } },
        label = { Text("Temperature") },
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}


/**
 * A Button Composable sending the user to the window with the converted
 * values provided the temperatureText can be turned into a float.
 *
 * @param temperatureText text input from user
 * @param navController a NavController with the path Screen.ConversionScreen
 * @param conversion A conversion function from another unit to celsius
 */
@Composable
fun ConvertButton (
    temperatureText: String,
    navController: NavController,
    conversion: (Float)->Float)
{
    Button (
        onClick = {
            temperatureText.toFloatOrNull()?.run {
                navController.navigate(
                    Screen.ConversionsScreen.withArgs( conversion(this).toString()))}
        }
    ) {
        Text(text="Convert")
    }
}