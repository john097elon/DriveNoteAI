package com.drivenote.app.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivenote.app.domain.usecase.SearchNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchNotesUseCase: SearchNotesUseCase
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            queryFlow
                .debounce(300L)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) flowOf(emptyList()) else searchNotesUseCase(query)
                }
                .collect { notes ->
                    _uiState.update { it.copy(results = notes, isSearching = false) }
                }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query, isSearching = query.isNotBlank()) }
        queryFlow.value = query
    }
}
