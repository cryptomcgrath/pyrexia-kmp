package com.edwardmcgrath.pyrexia

import kotlinx.serialization.Serializable


@Serializable
sealed interface Target {
    @Serializable
    data object LoginTarget : Target

    @Serializable
    data class StatsTarget(val url: String) : Target

    @Serializable
    data class StatInfoTarget(val url: String, val id: Int): Target
}
