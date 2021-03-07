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

import android.media.RingtoneManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.ITimer
import com.example.androiddevchallenge.ui.ITimer.Companion.HOURS_RANGE
import com.example.androiddevchallenge.ui.ITimer.Companion.MINUTES_RANGE
import com.example.androiddevchallenge.ui.ITimer.Companion.SECONDS_RANGE
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.utils.hideKeyboard
import com.example.androiddevchallenge.utils.toast

class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                Timer()
            }
        }
    }
}

// Start building your app here!
@ExperimentalAnimationApi
@Composable
fun Timer() {
    val context = LocalContext.current
    val composableScope = rememberCoroutineScope()
    val alarmTone by remember {
        mutableStateOf(
            RingtoneManager.getRingtone(
                context,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            )
        )
    }

    val timerController = object : ITimer {
        override var isPlaying by rememberSaveable { mutableStateOf(false) }
        override var isReady by rememberSaveable { mutableStateOf(false) }
        override var isSoundOn by rememberSaveable { mutableStateOf(true) }
        override var hours by rememberSaveable { mutableStateOf(0L) }
        override var minutes by rememberSaveable { mutableStateOf(0L) }
        override var seconds by rememberSaveable { mutableStateOf(0L) }
        override var progress by rememberSaveable { mutableStateOf(0f) }
        override var initialMillis by rememberSaveable { mutableStateOf(0L) }
    }

    if (timerController.isPlaying) {
        val finishedLabel = stringResource(R.string.timer_finished)

        timerController.start(
            coroutineScope = composableScope,
            onFinish = {
                context.toast(finishedLabel)
                if (timerController.isSoundOn)
                    alarmTone.play()
            }
        )
    }

    Box(contentAlignment = Alignment.Center) {
        Scaffold(
            topBar = {
                Header(isPlaying = timerController.isPlaying)
            },
            bottomBar = {
                Actions(
                    isPlaying = timerController.isPlaying,
                    isReady = timerController.isReady,
                    isSoundOn = timerController.isSoundOn,
                    onSoundClick = {
                        timerController.isSoundOn = !timerController.isSoundOn
                        if (!timerController.isSoundOn && alarmTone.isPlaying) {
                            alarmTone.stop()
                        }
                    },
                    onStartClick = { timerController.isPlaying = !timerController.isPlaying },
                    onRestartClick = {
                        timerController.restart()
                        if (alarmTone.isPlaying)
                            alarmTone.stop()
                    }
                )
            },
            backgroundColor = MaterialTheme.colors.primary
        ) {
            Body(
                hours = timerController.hours,
                minutes = timerController.minutes,
                seconds = timerController.seconds,
                progress = timerController.progress,
                isPlaying = timerController.isPlaying
            ) { newHours, newMinutes, newSeconds ->
                if (newHours != timerController.hours)
                    timerController.hours = newHours
                if (newMinutes != timerController.minutes)
                    timerController.minutes = newMinutes
                if (newSeconds != timerController.seconds)
                    timerController.seconds = newSeconds

                timerController.isReady = timerController.isReadyToPlay()
            }
        }
        BackgroundShadow(isPlaying = timerController.isPlaying)
    }
}

@Composable
fun BackgroundShadow(isPlaying: Boolean) {
    val transition = rememberInfiniteTransition()
    val shadow = transition.animateFloat(
        initialValue = 5f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    ).value.dp

    val modifier: Modifier = if (isPlaying) Modifier.shadow(shadow) else Modifier.shadow(5.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(.98f)
    )
}

@Composable
fun Header(isPlaying: Boolean) {
    val transition = rememberInfiniteTransition()

    val scaling by transition.animateFloat(
        initialValue = .95f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    val modifier: Modifier = if (isPlaying) Modifier.scale(scaling) else Modifier

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(.25f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.timer),
            style = MaterialTheme.typography.h2,
            color = Color.White
        )
    }
}

@ExperimentalAnimationApi
@Composable
fun Body(
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
        TimerFields(hours, minutes, seconds, isPlaying, onTimerValuesSet)
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
fun TimerFields(
    hours: Long,
    minutes: Long,
    seconds: Long,
    isPlaying: Boolean,
    onTimerValuesSet: (Long, Long, Long) -> Unit = { _, _, _ -> }
) {

    val transition = rememberInfiniteTransition()

    val scaling by transition.animateFloat(
        initialValue = .9f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    val modifier: Modifier = if (isPlaying) Modifier.scale(scaling) else Modifier

    Row(modifier = modifier) {
        TimeField(
            isPlaying = isPlaying,
            time = hours,
            label = stringResource(R.string.hours),
            timeRange = HOURS_RANGE,
            onValueChange = { onTimerValuesSet(it, minutes, seconds) }
        )
        TimeField(
            isPlaying = isPlaying,
            time = minutes,
            label = stringResource(R.string.minutes),
            timeRange = MINUTES_RANGE,
            onValueChange = { onTimerValuesSet(hours, it, seconds) }
        )
        TimeField(
            isPlaying = isPlaying,
            time = seconds,
            label = stringResource(R.string.seconds),
            timeRange = SECONDS_RANGE,
            onValueChange = { onTimerValuesSet(hours, minutes, it) }
        )
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
            cursorColor = Color.White,
            disabledTextColor = Color.White
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
fun Actions(
    modifier: Modifier = Modifier,
    isSoundOn: Boolean = true,
    isPlaying: Boolean = false,
    isReady: Boolean = false,
    onSoundClick: () -> Unit,
    onStartClick: () -> Unit,
    onRestartClick: () -> Unit
) {
    val context = LocalContext.current
    val timeNotSelectedMessage = stringResource(id = R.string.time_not_selected)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(.25f),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton(
            res = if (!isSoundOn) R.drawable.ic_sound_off else R.drawable.ic_sound_on,
            contentDescription = stringResource(R.string.change_volume),
            onClick = { onSoundClick() }
        )
        ActionButton(
            res = if (!isPlaying) R.drawable.ic_start else R.drawable.ic_pause,
            contentDescription = stringResource(R.string.start_timer),
            onClick = { if (isReady) onStartClick() else context.toast(timeNotSelectedMessage) }
        )
        ActionButton(
            res = R.drawable.ic_restart,
            contentDescription = stringResource(R.string.restart_timer),
            onClick = { onRestartClick() }
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
        shape = RectangleShape
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
        Timer()
    }
}

@ExperimentalAnimationApi
@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        Timer()
    }
}
