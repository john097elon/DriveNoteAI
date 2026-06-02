package com.drivenote.app.ui.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drivenote.app.domain.model.NoteCategory
import com.drivenote.app.ui.home.component.NoteCard

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FilterScreen(
    uiState: FilterUiState,
    onCategorySelect: (NoteCategory) -> Unit,
    onBack: () -> Unit,
    onNoteClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(uiState.category.label) },
            navigationIcon = {
                TextButton(onClick = onBack) {
                    Text("뒤로")
                }
            }
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            item {
                androidx.compose.foundation.layout.Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    NoteCategory.entries.forEach { category ->
                        TextButton(onClick = { onCategorySelect(category) }) {
                            Text(
                                text = category.label,
                                color = if (uiState.category == category) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.notes, key = { it.id }) { note ->
                NoteCard(note = note, onTap = { onNoteClick(note.id) })
            }
        }
    }
}
