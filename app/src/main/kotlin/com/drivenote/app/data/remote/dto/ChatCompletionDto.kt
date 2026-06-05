package com.drivenote.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionRequestDto(
    val model: String = "hermes",
    val messages: List<ChatCompletionMessageDto>,
    val stream: Boolean = true
)

@Serializable
data class ChatCompletionMessageDto(
    val role: String,
    val content: String
)

@Serializable
data class ChatCompletionStreamChunkDto(
    val choices: List<ChatCompletionStreamChoiceDto> = emptyList()
)

@Serializable
data class ChatCompletionStreamChoiceDto(
    val delta: ChatCompletionStreamDeltaDto = ChatCompletionStreamDeltaDto()
)

@Serializable
data class ChatCompletionStreamDeltaDto(
    val content: String? = null
)
