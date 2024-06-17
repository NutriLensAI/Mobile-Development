package com.capstone.mobiledevelopment.nutrilens.view.utils

import android.animation.TimeInterpolator

class CustomInterpolator : TimeInterpolator {
    override fun getInterpolation(input: Float): Float {
        // Create a piecewise linear function with small stops
        return when {
            input < 0.2f -> input * 0.9f
            input < 0.4f -> 0.18f + (input - 0.2f) * 0.8f
            input < 0.6f -> 0.34f + (input - 0.4f) * 0.7f
            input < 0.8f -> 0.48f + (input - 0.6f) * 0.6f
            else -> 0.6f + (input - 0.8f) * 0.4f
        }
    }
}