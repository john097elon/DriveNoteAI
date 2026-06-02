package com.drivenote.app.data.remote

import com.drivenote.app.data.remote.dto.SyncRequestDto
import com.drivenote.app.data.remote.dto.SyncResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HermesApiClient @Inject constructor(
    private val httpClient: HttpClient
) {
    suspend fun syncNotes(endpoint: String, request: SyncRequestDto): SyncResponseDto {
        val normalized = endpoint.trimEnd('/')
        return httpClient.post("$normalized/api/notes") {
            setBody(request)
        }.body()
    }
}
