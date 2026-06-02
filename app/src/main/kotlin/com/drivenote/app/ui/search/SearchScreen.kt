package com.drivenote.app.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drivenote.app.ui.home.component.NoteCard

@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onNoteClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = uiState.query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            label = { Text("검색어를 입력하세요") },
            singleLine = true
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (uiState.query.isBlank()) {
                item { Text("최근 검색이 없습니다.") }
            } else if (uiState.results.isEmpty()) {
                item { Text("검색 결과가 없습니다.") }
            } else {
                items(uiState.results, key = { it.id }) { note ->
                    NoteCard(note = note, onTap = { onNoteClick(note.id) })
                }
            }
        }
    }
}
