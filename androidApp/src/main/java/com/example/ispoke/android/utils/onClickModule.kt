package com.example.ispoke.android.utils

import androidx.navigation.NavController

fun onCLickModule(navController : NavController, letter: Char, imageResId: Int, title: String){
    navController.navigate("gesture/$letter/$imageResId/$title")
}