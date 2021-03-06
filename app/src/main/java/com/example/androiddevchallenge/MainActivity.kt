/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.utils.getMillis
import com.example.androiddevchallenge.utils.getTimeFromMillis
import com.example.androiddevchallenge.utils.hideKeyboard
import com.example.androiddevchallenge.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@ExperimentalAnimationApi
@Composable
fun MyApp() {
    var isPlaying by rememberSaveable { mutableStateOf(false) }
    var isReady by rememberSaveable { mutableStateOf(false) }

    var hours by rememberSaveable { mutableStateOf(0L) }
    var minutes by rememberSaveable { mutableStateOf(0L) }
    var seconds by rememberSaveable { mutableStateOf(0L) }

    var progress by rememberSaveable { mutableStateOf(0f) }
    var initialMillis by rememberSaveable { mutableStateOf(0L) }

    fun isTimerReady() = hours > 0 || minutes > 0 || seconds > 0
    fun isFinished() = isPlaying && hours == 0L && minutes == 0L && seconds == 0L

    fun restartTimer() {
        isPlaying = false
        isReady = false
        hours = 0
        minutes = 0
        seconds = 0
        initialMillis = 0
    }

    val composableScope = rememberCoroutineScope()

    if (isPlaying) {
        composableScope.launch(Dispatchers.Main) {
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
                restartTimer()
            }
        }
    }

    Scaffold(
        topBar = {
            TimerHeader()
        },
        bottomBar = {
            TimerActions(
                isPlaying = isPlaying,
                isReady = isReady,
                onStartClick = {
                    isPlaying = !isPlaying
                },
                onRestartClick = { restartTimer() }
            )
        },
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Timer(
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            progress = progress,
            isPlaying = isPlaying
        ) { newHours, newMinutes, newSeconds ->
            if (newHours != hours)
                hours = newHours
            if (newMinutes != minutes)
                minutes = newMinutes
            if (newSeconds != seconds)
                seconds = newSeconds

            isReady = isTimerReady()
        }
    }
}

@Composable
fun TimerHeader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(.25f),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Timer", style = MaterialTheme.typography.h2, color = Color.White)
    }
}

@ExperimentalAnimationApi
@Composable
fun Timer(
    hours: Long,
    minutes: Long,
    seconds: Long,
    progress: Float,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onTimerValuesSet: (Long, Long, Long) -> Unit = { _, _, _ -> }
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(.6f),
        contentAlignment = Alignment.Center
    ) {
        TimerProgress(isVisible = isPlaying, progress = progress)
        Row {
            TimeField(
                isPlaying = isPlaying,
                time = hours,
                label = "Hours",
                timeRange = 0..23,
                onValueChange = { onTimerValuesSet(it, minutes, seconds) }
            )
            TimeField(
                isPlaying = isPlaying,
                time = minutes,
                label = "Minutes",
                timeRange = 0..59,
                onValueChange = { onTimerValuesSet(hours, it, seconds) }
            )
            TimeField(
                isPlaying = isPlaying,
                time = seconds,
                label = "Seconds",
                timeRange = 0..59,
                onValueChange = { onTimerValuesSet(hours, minutes, it) }
            )
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun TimerProgress(modifier: Modifier = Modifier, isVisible: Boolean, progress: Float) {
    AnimatedVisibility(
        modifier = modifier.fillMaxSize(),
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        val animatedProgress = animateFloatAsState(
            targetValue = progress,
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
        ).value

        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxHeight(.7f)
                    .fillMaxWidth(.85f)
                    .offset(y = (-32).dp),
                color = Color.White,
                strokeWidth = 3.dp,
                progress = animatedProgress
            )
        }
    }
}

@Composable
fun TimeField(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    time: Long,
    timeRange: IntRange,
    label: String,
    onValueChange: (Long) -> Unit
) {
    val view = LocalView.current

    fun validateText(text: String, onTextValid: (Long) -> Unit) {
        val timeNumber = text.toIntOrNull()
        if (timeNumber == null) {
            onTextValid(0)
        } else {
            if (timeNumber in timeRange) {
                onTextValid(text.toLong())
            } else if (text.contains('0')) {
                val timeNumberWithoutZero = text.filter { it != '0' }.toLong()
                if (timeNumberWithoutZero in timeRange)
                    onTextValid(timeNumberWithoutZero)
            }
        }
    }

    OutlinedTextField(
        enabled = !isPlaying,
        value = time.toString(),
        onValueChange = { text ->
            validateText(text) {
                onValueChange(it)
            }
        },
        label = { Text(text = label) },
        textStyle = MaterialTheme.typography.h4,
        modifier = modifier.size(88.dp),
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
            focusedLabelColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium),
            cursorColor = Color.White
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                view.hideKeyboard()
                view.clearFocus()
            }
        )
    )
}

@Composable
fun TimerActions(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    isReady: Boolean = false,
    onStartClick: () -> Unit,
    onRestartClick: () -> Unit
) {
    val context = LocalContext.current
    val timeNotSelectedMessage = stringResource(id = R.string.time_not_selected)
    val timerNotStartedMessage = stringResource(id = R.string.timer_not_started)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(.25f),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton(
            res = if (!isPlaying) R.drawable.ic_start else R.drawable.ic_pause,
            contentDescription = "Start Timer",
            onClick = { if (isReady) onStartClick() else context.toast(timeNotSelectedMessage) }
        )
        ActionButton(
            res = R.drawable.ic_restart,
            contentDescription = "Restart Timer",
            onClick = { if (isReady) onRestartClick() else context.toast(timerNotStartedMessage) }
        )
    }
}

@Composable
fun ActionButton(
    @DrawableRes res: Int,
    contentDescription: String? = null,
    onClick: () -> Unit = {}
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.size(64.dp),
    ) {
        Image(
            painter = painterResource(id = res),
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary)
        )
    }
}

@ExperimentalAnimationApi
@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@ExperimentalAnimationApi
@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
