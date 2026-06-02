package com.drivenote.app.ui.detail

import com.drivenote.app.domain.model.Note

data class DetailUiState(
    val note: Note? = null,
    val isSaving: Boolean = false,
    val error: String? = null
)
