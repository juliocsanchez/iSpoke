package com.example.ispoke.android

import android.graphics.Bitmap

interface LandmarkClassificator {
    fun classify( bitmap: Bitmap, rotation: Int) : List<Classificator>
}