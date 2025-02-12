package com.example.ispoke.android.classes

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class GestureCheck : ViewModel() {
    private val _detectedGestures = mutableStateListOf<String>()
    val detectedGestures: List<String> = _detectedGestures

    var gestureCount by mutableStateOf(0)
        private set

    fun addGesture(gesture: String) {
        val letra = gesture.lowercase()
        if (!_detectedGestures.contains(letra)) {
            _detectedGestures.add(letra)
            gestureCount++
        }
    }

}
