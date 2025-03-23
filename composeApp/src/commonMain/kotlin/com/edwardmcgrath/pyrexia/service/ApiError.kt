package com.edwardmcgrath.pyrexia.service

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException


data class ApiError(
    val message: String,
    val exception: Throwable,
    val debug: Boolean = false
) {
    val debugMessage: String get() {
        return exception.stackTraceToString()
    }
}

fun Exception.toErrorMessage(): String {
    val e = this
    return when (e) {
        is ClientRequestException -> "Client Error"
        is ServerResponseException -> "Server Error ${e.response.status }"
        is UnresolvedAddressException -> "No Internet"
        is IOException -> "Network Error"
        is SerializationException -> "Serialization Error"
        else -> "Network Error"
    }
}

fun Exception.toApiError(): ApiError {
    return ApiError(
        message = this.toErrorMessage(),
        exception = this
    )
}
