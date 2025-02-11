package com.example.ispoke.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.ispoke.android.theme.MyApplicationTheme
import com.example.ispoke.android.navigation.Routes


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
               Routes()
            }
        }
    }
}






