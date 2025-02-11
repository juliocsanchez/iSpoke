package com.example.ispoke.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.ispoke.android.theme.MyApplicationTheme
import com.example.ispoke.android.navigation.Routes
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
               Routes()
            }
        }
    }
}






