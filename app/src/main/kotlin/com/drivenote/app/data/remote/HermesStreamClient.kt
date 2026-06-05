package com.drivenote.app.data.remote

import com.drivenote.app.data.remote.dto.ChatCompletionMessageDto
import com.drivenote.app.data.remote.dto.ChatCompletionRequestDto
import com.drivenote.app.data.remote.dto.ChatCompletionStreamChunkDto
import com.drivenote.app.domain.repository.SettingsRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.readUTF8Line
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

@Singleton
class HermesStreamClient @Inject constructor(
    private val httpClient: HttpClient,
    private val settingsRepository: SettingsRepository
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun streamChat(messages: List<ChatCompletionMessageDto>): Flow<String> = flow {
        val endpoint = settingsRepository.getHermesEndpoint().trimEnd('/')
        val request = ChatCompletionRequestDto(messages = messages)

        httpClient.preparePost("$endpoint/v1/chat/completions") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Accept, "text/event-stream")
            setBody(request)
        }.execute { response ->
            val channel = response.bodyAsChannel()
            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line() ?: break
                if (!line.startsWith("data:")) continue

                val payload = line.removePrefix("data:").trim()
                if (payload.isEmpty()) continue
                if (payload == "[DONE]") break

                val chunk = runCatching {
                    json.decodeFromString(ChatCompletionStreamChunkDto.serializer(), payload)
                }.getOrNull() ?: continue

                val delta = chunk.choices.firstOrNull()?.delta?.content
                if (!delta.isNullOrEmpty()) {
                    emit(delta)
                }
            }
        }
    }
}
