package com.example.project

import java.math.BigDecimal
import java.math.RoundingMode


fun roundFloat(number: Float): Float {
    return BigDecimal((number).toDouble())
        .setScale(1, RoundingMode.HALF_EVEN)
        .toFloat()
}

fun celsiusFromKelvin(degrees: Float): Float {
    return degrees - 273.15f
}

fun kelvinFromCelsius(degrees: Float): Float {
    return degrees + 273.15f
}

fun celsiusFromFahrenheit(degrees: Float): Float {
    return (5.0f/9.0f) * (degrees-32.0f)
}

fun fahrenheitFromCelsius(degrees: Float): Float {
    return degrees / (5.0f/9.0f) + 32.0f
}