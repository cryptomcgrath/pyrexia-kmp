package com.edwardmcgrath.pyrexia

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform