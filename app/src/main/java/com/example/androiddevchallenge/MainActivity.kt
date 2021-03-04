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
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.BottomAppBar
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    Scaffold(
        topBar = { Header() },
        bottomBar = { Footer() },
        floatingActionButton = { FooterActionButton() },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center
    ) {
    }
}

@Composable
fun Header() {
    Surface(
        color = MaterialTheme.colors.primary,
        elevation = 5.dp,
        shape = MaterialTheme.shapes.large.copy(
            topEnd = CornerSize(0.dp),
            topStart = CornerSize(0.dp)
        ),
        content = {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "Timer", style = MaterialTheme.typography.h3)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.2f)
    )
}

@Composable
fun Footer() {
    BottomAppBar(
        elevation = 5.dp,
        cutoutShape = MaterialTheme.shapes.large
    ) {
    }
}

@Composable
fun FooterActionButton() {
    FloatingActionButton(onClick = { /*TODO*/ }) {
        Image(
            painter = painterResource(id = R.drawable.ic_start),
            contentDescription = "Start Timer"
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
