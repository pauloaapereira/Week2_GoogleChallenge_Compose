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
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.utils.hideKeyboard

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
                onRestartClick = {
                }
            )
        },
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Timer(isPlaying = isPlaying) { canPlay ->
            isReady = canPlay
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
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onTimerValuesSet: (Boolean) -> Unit = {}
) {
    var hours by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf("") }

    fun isAnyValueSet() = hours.isNotBlank() || minutes.isNotBlank() || seconds.isNotBlank()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(.6f),
        contentAlignment = Alignment.Center
    ) {
        TimerProgress(isVisible = isPlaying, progress = 0f)
        Row {
            TimeField(
                time = hours,
                label = "Hours",
                timeRange = 0..23,
                onValueChange = {
                    hours = it
                    onTimerValuesSet(isAnyValueSet())
                }
            )
            TimeField(
                time = minutes,
                label = "Minutes",
                timeRange = 0..59,
                onValueChange = {
                    minutes = it
                    onTimerValuesSet(isAnyValueSet())
                }
            )
            TimeField(
                time = seconds,
                label = "Seconds",
                timeRange = 0..59,
                onValueChange = {
                    seconds = it
                    onTimerValuesSet(isAnyValueSet())
                }
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
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxHeight(.7f)
                    .fillMaxWidth(.85f)
                    .offset(y = (-32).dp),
                color = Color.White,
                strokeWidth = 3.dp,
                progress = progress
            )
        }
    }
}

@Composable
fun TimeField(
    modifier: Modifier = Modifier,
    time: String,
    timeRange: IntRange,
    label: String,
    onValueChange: (String) -> Unit
) {
    val view = LocalView.current

    OutlinedTextField(
        value = time,
        onValueChange = { text ->
            if (text.isBlank() || text.toInt() in timeRange)
                onValueChange(text)
        },
        label = { Text(text = label) },
        textStyle = MaterialTheme.typography.h3,
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
                if (time.isNotBlank() && time.toInt() < 10) {
                    onValueChange("0$time")
                }
                view.hideKeyboard()
                view.clearFocus()
            }
        ),
        placeholder = { Text(text = "00", style = MaterialTheme.typography.h3) }
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
            onClick = { if (isReady) onStartClick() }
        )
        ActionButton(
            res = R.drawable.ic_restart,
            contentDescription = "Restart Timer",
            onClick = { if (isReady) onRestartClick() }
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
