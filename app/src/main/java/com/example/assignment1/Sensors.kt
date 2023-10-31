package com.example.assignment1

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext



@Composable
fun GravitySensor() {
    // Sensor values:
    var sensorStatus = remember { mutableStateOf(FloatArray(3){0f}) }
    var lastSensorStatus = remember { mutableStateOf(FloatArray(3){1f}) }

    val ctx = LocalContext.current
    val sensorManager: SensorManager = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager


    val gravitySensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    var buttonState = remember { mutableStateOf( false) }


    val gravityEventListener = object : SensorEventListener {
        // Callback for accuracy change, does nothing;
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

        // Callback for sensor value change, prints values
        override fun onSensorChanged(event: SensorEvent) {

            if (event.sensor.type == Sensor.TYPE_GRAVITY) {
                // @TODO store fewer value changes ?
                //if (abs(sensorStatus.value[0] - lastSensorStatus.value[0]) > 0.00002f) {

                //lastSensorStatus.value = sensorStatus.value
                sensorStatus.value = event.values
            }

            //}

            //    sensorStatus.value = event.values

            //println("hello, sensor value changed")
            //println(event.values)
        }
    }


    Column {
        Text(
            text = "Sensor testing"
        )


        // Activate sensing:
        Button(
            onClick = {
                buttonState.value = true            }
        ) { Text("ON") }

        // Deactivate sensing
        Button(
            onClick = {
                buttonState.value = false
            }
        ) { Text("OFF") }

        // Register event listener:
        fun register() { sensorManager.registerListener(
            gravityEventListener,
            gravitySensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )}

        // Unregister listener:
        //@TODO not yet working
        fun unregister() {
            sensorManager.unregisterListener(gravityEventListener)
        }

        if(buttonState.value) {register()}
        if(!buttonState.value){unregister()}

        // Print values:
        for (v in sensorStatus.value) {
            Text(
                text = v.toString()
            )
        }
        Text(buttonState.value.toString())
    }
}