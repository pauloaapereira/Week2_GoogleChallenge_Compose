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
package com.example.androiddevchallenge.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.androiddevchallenge.R

private val AppFontFamily = FontFamily(
    fonts = listOf(
        Font(
            resId = R.font.indie_flower,
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        )
    )
)

private val DefaultTypography = Typography()
val typography = Typography(
    h1 = DefaultTypography.h1.copy(fontFamily = AppFontFamily),
    h2 = DefaultTypography.h2.copy(fontFamily = AppFontFamily),
    h3 = DefaultTypography.h3.copy(fontFamily = AppFontFamily),
    h4 = DefaultTypography.h4.copy(fontFamily = AppFontFamily),
    h5 = DefaultTypography.h5.copy(fontFamily = AppFontFamily),
    h6 = DefaultTypography.h6.copy(fontFamily = AppFontFamily),
    subtitle1 = DefaultTypography.subtitle1.copy(fontFamily = AppFontFamily),
    subtitle2 = DefaultTypography.subtitle2.copy(fontFamily = AppFontFamily),
    body1 = DefaultTypography.body1.copy(fontFamily = AppFontFamily),
    body2 = DefaultTypography.body2.copy(fontFamily = AppFontFamily),
    button = DefaultTypography.button.copy(fontFamily = AppFontFamily),
    caption = DefaultTypography.caption.copy(fontFamily = AppFontFamily),
    overline = DefaultTypography.overline.copy(fontFamily = AppFontFamily)
)
