package com.edwardmcgrath.pyrexia.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class PyrexiaService {

    private val client = HttpClient() {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys=true } )
        }
    }

    suspend fun login(url: String, email: String, password: String): String {
        val response: LoginResponse = client.post("${url}/users/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequestDto(email, password))
            }.body()
        tokenMap[url] = response.token
        return response.token
    }

    suspend fun getStatList(url: String): GetStatList {
        val token = tokenMap[url].orEmpty()
        val response: GetStatList =
            client.get("${url}/stat/list") {
                    header(HEADER_TOKEN, token)
                }.body()
        return response
    }

    suspend fun getStat(url: String, id: Int): GetStat {
        val token = tokenMap[url].orEmpty()
        val response: GetStat =
            client.get("${url}/stat/{$id}") {
                header(HEADER_TOKEN, token)
            }.body()
        return response
    }

    suspend fun statIncrease(url: String, id: Int): GetStat {
        val token = tokenMap[url].orEmpty()
        val response: GetStat =
            client.post("${url}/stat/${id}/increase") {
                contentType(ContentType.Application.Json)
                header(HEADER_TOKEN, token)
            }.body()
        return response
    }

    suspend fun statDecrease(url: String, id: Int): GetStat {
        val token = tokenMap[url].orEmpty()
        val response: GetStat =
            client.post("${url}/stat/${id}/decrease") {
                contentType(ContentType.Application.Json)
                header(HEADER_TOKEN, token)
            }.body()
        return response
    }

    suspend fun refill(url: String, controlId: Int, id: Int): GetStat {
        val token = tokenMap[url].orEmpty()
        client.post("${url}/controls/${controlId}/refill") {
            contentType(ContentType.Application.Json)
            header(HEADER_TOKEN, token)
        }
        return getStat(url, id)
    }

    companion object {
        val tokenMap = mutableMapOf<String, String>()
        val instance: PyrexiaService by lazy { PyrexiaService() }
    }
}

private const val HEADER_TOKEN = "x-access-token"
