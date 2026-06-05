package com.drivenote.app.ui.voice

data class VoiceUiState(
    val isActive: Boolean = false,
    val status: String = "대화를 시작하세요.",
    val partialUserText: String = "",
    val assistantText: String = ""
)
