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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.snapFlingBehavior
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {
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
@Composable
fun MyApp() {
    var isPlaying by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TimerHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.25f)
            )
        },
        bottomBar = {
            TimerActions(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.25f),
                isPlaying = isPlaying,
                onStartClick = {
                    isPlaying = !isPlaying
                },
                onRestartClick = {
                }
            )
        },
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Body(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.5f)
        )
    }
}

@Composable
fun TimerHeader(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text = "Timer", style = MaterialTheme.typography.h2, color = Color.White)
    }
}

@Composable
fun Body(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Row {
            TimeSpinner(range = 0..23)
            TimeSpinner(range = 0..59)
            TimeSpinner(range = 0..59)
        }
    }
}

@Composable
fun TimeSpinner(selected: Int = 0, range: IntRange) {
    var selectedItem by rememberSaveable { mutableStateOf(0) }

    LazyColumn(
        modifier = Modifier.size(64.dp),
        flingBehavior = snapFlingBehavior()
    ) {
        items(range.toList()) { number ->
            Text(
                text = if (number < 10) "0$number" else number.toString(),
                style = MaterialTheme.typography.h2,
                color = Color.White
            )
        }
    }
}

@Composable
fun TimerActions(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    onStartClick: () -> Unit,
    onRestartClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton(
            res = if (!isPlaying) R.drawable.ic_start else R.drawable.ic_pause,
            contentDescription = "Start Timer",
            onClick = onStartClick
        )
        ActionButton(
            res = R.drawable.ic_restart,
            contentDescription = "Restart Timer",
            onClick = onRestartClick
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
        modifier = Modifier.size(64.dp)
    ) {
        Image(
            painter = painterResource(id = res),
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary)
        )
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
