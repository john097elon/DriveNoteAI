package com.drivenote.app.domain.model

enum class VoiceConversationPhase {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING,
    ERROR
}

data class VoiceConversationState(
    val phase: VoiceConversationPhase = VoiceConversationPhase.IDLE,
    val isActive: Boolean = false,
    val partialUserText: String = "",
    val finalUserText: String = "",
    val assistantText: String = "",
    val errorMessage: String? = null
)

data class ConversationTurn(
    val role: String,
    val content: String
)
