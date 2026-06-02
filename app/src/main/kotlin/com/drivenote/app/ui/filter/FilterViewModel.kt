package com.drivenote.app.ui.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivenote.app.domain.model.Note
import com.drivenote.app.domain.model.NoteCategory
import com.drivenote.app.domain.usecase.GetNotesByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FilterUiState(
    val category: NoteCategory = NoteCategory.WORK,
    val notes: List<Note> = emptyList()
)

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val getNotesByCategoryUseCase: GetNotesByCategoryUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(FilterUiState())
    val uiState: StateFlow<FilterUiState> = _uiState.asStateFlow()
    private var observeJob: Job? = null

    fun load(categoryLabel: String?) {
        val category = categoryLabel?.takeIf { it.isNotBlank() }?.let(NoteCategory::fromLabel)
            ?: NoteCategory.WORK
        select(category)
    }

    fun select(category: NoteCategory) {
        _uiState.update { it.copy(category = category) }
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            getNotesByCategoryUseCase(category).collect { notes ->
                _uiState.update { state -> state.copy(notes = notes) }
            }
        }
    }
}
