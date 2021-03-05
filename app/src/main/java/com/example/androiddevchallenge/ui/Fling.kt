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
package com.example.androiddevchallenge.ui

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.defaultDecayAnimationSpec
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlin.math.abs

@Composable
fun snapFlingBehavior(): FlingBehavior {
    val flingSpec = defaultDecayAnimationSpec()
    return remember(flingSpec) {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                var velocityLeft = initialVelocity
                var lastValue = 0f
                AnimationState(
                    initialValue = 0f,
                    initialVelocity = initialVelocity,
                ).animateDecay(flingSpec) {
                    val delta = value - lastValue
                    val left = scrollBy(delta)
                    lastValue = value
                    velocityLeft = this.velocity
                    // avoid rounding errors and stop if anything is unconsumed
                    if (abs(left) > 0.5f) {
                        this.cancelAnimation()
                    }
                }
                return velocityLeft
            }
        }
    }
}
