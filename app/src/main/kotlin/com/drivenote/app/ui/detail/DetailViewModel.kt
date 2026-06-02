package com.drivenote.app.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivenote.app.domain.model.NoteCategory
import com.drivenote.app.domain.usecase.DeleteNoteUseCase
import com.drivenote.app.domain.usecase.GetNoteByIdUseCase
import com.drivenote.app.domain.usecase.UpdateNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun load(noteId: String) {
        viewModelScope.launch {
            val note = getNoteByIdUseCase(noteId)
            _uiState.update { it.copy(note = note) }
        }
    }

    fun updateCategory(category: NoteCategory) {
        val note = _uiState.value.note ?: return
        _uiState.update { it.copy(note = note.copy(category = category, isSynced = false)) }
    }

    fun addTag(tag: String) {
        val note = _uiState.value.note ?: return
        if (tag.isBlank()) return
        _uiState.update {
            it.copy(
                note = note.copy(
                    tags = (note.tags + tag.trim()).distinct(),
                    isSynced = false
                )
            )
        }
    }

    fun removeTag(tag: String) {
        val note = _uiState.value.note ?: return
        _uiState.update {
            it.copy(
                note = note.copy(
                    tags = note.tags.filterNot { current -> current == tag },
                    isSynced = false
                )
            )
        }
    }

    fun save() {
        val note = _uiState.value.note ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            runCatching {
                updateNoteUseCase(
                    note.copy(
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(error = throwable.message ?: "저장 중 오류가 발생했습니다.")
                }
            }
            _uiState.update { it.copy(isSaving = false) }
        }
    }

    fun delete(onDone: () -> Unit) {
        val note = _uiState.value.note ?: return
        viewModelScope.launch {
            deleteNoteUseCase(note.id)
            onDone()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
