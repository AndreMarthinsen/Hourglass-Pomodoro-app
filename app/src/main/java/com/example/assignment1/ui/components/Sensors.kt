package com.example.assignment1.ui.components

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext


/**
 * This code was used for testing out reading values in real time from the accelerometer at an
 * early stage of the implementation phase.
 * We later discovered the Activity Recognition Transition API, and considered it to
 * be a more complete solution. This code is therefore not used in the app, but we include it
 * to show the the concept.
 */


/**
 * GravityWrapper prevents recomposition at every sensor data update.
 */
@Composable
fun GravityWrapper() {
    val sensorStatus = remember { mutableStateOf(FloatArray(3) { 0f }) }
    val gravityEventListener = object : SensorEventListener {
        // Callback for accuracy change, does nothing:
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

        // Callback for sensor value change, stores values:
        override fun onSensorChanged(event: SensorEvent) {

            if (event.sensor.type == Sensor.TYPE_GRAVITY) {

                sensorStatus.value = event.values
            }

        }
    }
    // Register event listener:
    GravitySensor(
        gravityEventListener = gravityEventListener,
        sensorStatus = sensorStatus
    )
}

/**
 * GravitySensor shows the raw accelerometer data. Sensing can be turned on or off using buttons.
 * @param gravityEventListener - event listener for sensor changes
 * @param sensorStatus - measured sensor values from accelerometer
 */
@Composable
fun GravitySensor(
    gravityEventListener: SensorEventListener,
    sensorStatus: MutableState<FloatArray>
) {

    var totalRecomposes = remember {mutableStateOf(0)}

    val ctx = LocalContext.current
    val sensorManager: SensorManager = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val gravitySensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

    // Counting number of recompositions, for debugging:
    totalRecomposes.value = 1 + totalRecomposes.value

    Column {
        Text(
            text = "Total recomposes ${totalRecomposes.value}"
        )

        // Activate sensing:
        Button(
            onClick = {
                sensorManager.registerListener(
                    gravityEventListener,
                    gravitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
        ) { Text("ON") }

        // Deactivate sensing
        Button(
            onClick = {
                sensorManager.unregisterListener(gravityEventListener)
            }
        ) { Text("OFF") }

        // Print values:
        for (v in sensorStatus.value) {
            Text(
                text = v.toString()
            )
        }

    }
}