package com.example.ispoke.android.controller

import android.graphics.RectF

data class Detection(
    val label: String,
    val confidence: Float,
    val boundingBox: RectF
)
