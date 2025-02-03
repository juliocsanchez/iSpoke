package com.example.ispoke

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform