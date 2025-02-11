package com.example.ispoke.android.utils

import android.content.Context

fun getDrawableID(context: Context, name: String): Int {
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}