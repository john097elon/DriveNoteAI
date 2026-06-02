package com.drivenote.app.ui.home

import com.drivenote.app.domain.model.Note
import com.drivenote.app.domain.model.NoteCategory

data class HomeUiState(
    val todayNotes: List<Note> = emptyList(),
    val selectedCategory: NoteCategory? = null,
    val isRecording: Boolean = false,
    val recordingSeconds: Int = 0,
    val error: String? = null
)
