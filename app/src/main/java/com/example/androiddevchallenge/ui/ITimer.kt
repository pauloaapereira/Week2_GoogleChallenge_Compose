package com.example.androiddevchallenge.ui

import com.example.androiddevchallenge.utils.getMillis
import com.example.androiddevchallenge.utils.getTimeFromMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface ITimer {

    companion object {
        val HOURS_RANGE = 0..23
        val MINUTES_RANGE = 0..59
        val SECONDS_RANGE = 0..59
    }

    var isPlaying: Boolean
    var isSoundOn: Boolean
    var isReady: Boolean

    var hours: Long
    var minutes: Long
    var seconds: Long

    var progress: Float
    var initialMillis: Long
    
    fun isFinished() = isPlaying && hours == 0L && minutes == 0L && seconds == 0L
    fun isReadyToPlay() = hours > 0 || minutes > 0 || seconds > 0
    fun restart() {
        isPlaying = false
        isReady = false
        hours = 0
        minutes = 0
        seconds = 0
        initialMillis = 0
    }
    fun start(coroutineScope: CoroutineScope, onFinish: () -> Unit = {}) {
        coroutineScope.launch {
            while ((hours != 0L || minutes != 0L || seconds != 0L) && isPlaying) {
                val millis = getMillis(hours, minutes, seconds)

                if (initialMillis == 0L) {
                    initialMillis = millis
                }

                progress = (millis * 100f / initialMillis) / 100f

                delay(1000)
                getTimeFromMillis(millis - 1000L) { h, m, s ->
                    if (isPlaying) {
                        hours = h
                        minutes = m
                        seconds = s
                    }
                }
            }

            if (isFinished()) {
                progress = 0f
                delay(1000)
                restart()
                onFinish()
            }
        }
    }
}