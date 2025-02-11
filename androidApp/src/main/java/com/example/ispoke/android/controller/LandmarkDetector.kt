package com.example.ispoke.android.controller

import android.graphics.Bitmap

interface LandmarkDetector {
    fun detect(bitmap: Bitmap, rotation: Int): List<Detection>
}
