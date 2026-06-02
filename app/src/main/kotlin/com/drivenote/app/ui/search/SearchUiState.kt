package com.drivenote.app.ui.search

import com.drivenote.app.domain.model.Note

data class SearchUiState(
    val query: String = "",
    val results: List<Note> = emptyList(),
    val isSearching: Boolean = false
)
