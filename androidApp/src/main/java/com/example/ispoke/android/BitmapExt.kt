package com.example.ispoke.android

import android.graphics.Bitmap

fun Bitmap.centerCrop(desiredWith: Int, desiredHeight: Int): Bitmap{
    val xStart = (width - desiredWith) / 2
    val yStart = (height - desiredHeight) / 2

    if (xStart < 0 || yStart < 0 || desiredWith > width || desiredHeight > height){
        throw IllegalArgumentException("Argumentos inválidos para a centralização do cropping")
    }

    return Bitmap.createBitmap(this, xStart, yStart,desiredWith, desiredHeight)
}