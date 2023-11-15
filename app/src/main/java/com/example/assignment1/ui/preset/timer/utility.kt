package com.example.assignment1.ui.preset.timer

import androidx.compose.foundation.ScrollState
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit



fun detectedActivityToString(activity: Int) : String{
    return when(activity) {
        0 -> "IN_VEHICLE"
        1 -> "ON_BICYCLE"
        2 -> "ON_FOOT"
        3 -> "STILL"
        4 -> "UNKNOWN"
        5 -> "TILTING"
        7 -> "WALKING"
        8 -> "RUNNING"
        else -> "UNKNOWN"
    }
}


/**
 * getScrollFromDuration calculates a scrollState value based on elapsed duration
 * divided by max duration in range [0, maxScroll]
 *
 * @param time - elapsed duration
 * @param maxTime - max duration
 * @param maxScroll - max value possible for a particular scrollable composable
 * @param roundingInSeconds - Rounds time to nearest n seconds.
 * @return Int in range [0, maxScroll]
 */
fun getScrollFromDuration(time: Duration, maxTime: Duration, maxScroll: Int, roundingInSeconds: Int?) : Int {
    return if(roundingInSeconds != null) {
        ((time.toInt(DurationUnit.SECONDS).toFloat() /
                maxTime.toInt(DurationUnit.SECONDS).toFloat()*maxScroll) / roundingInSeconds).toInt()*roundingInSeconds
    } else {
        ((time.toInt(DurationUnit.SECONDS).toFloat() /
                maxTime.toInt(DurationUnit.SECONDS).toFloat()*maxScroll)).toInt()
    }
}


/**
 * getDurationFromScroll calculates a duration from a scrollState where the returned duration
 * is maxDuration * (current scroll / max scroll).
 * @param scrollState - scrollState of scrollable used to calculate duration
 * @param maxDuration - max duration
 * @param roundingInSeconds - rounds resulting time to nearest n seconds
 * @return Duration in  range [0, maxDuration]
 */
fun getDurationFromScroll(scrollState: ScrollState, maxDuration: Duration, roundingInSeconds: Int) : Duration {
    return ((((scrollState.value.toFloat() / scrollState.maxValue.toFloat()) * maxDuration.toInt(
        DurationUnit.SECONDS))/roundingInSeconds).roundToInt()*roundingInSeconds).seconds
}