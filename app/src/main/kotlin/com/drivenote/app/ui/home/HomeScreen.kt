package com.drivenote.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drivenote.app.domain.model.Note
import com.drivenote.app.domain.model.NoteCategory
import com.drivenote.app.ui.home.component.NoteCard
import com.drivenote.app.ui.home.component.RecordingOverlay
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreen(
    uiState: HomeUiState,
    onRecordClick: () -> Unit,
    onNoteClick: (String) -> Unit,
    onCategorySelected: (NoteCategory?) -> Unit,
    onOpenFilter: (NoteCategory?) -> Unit,
    onSyncClick: () -> Unit,
    onClearError: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            onClearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("DriveNote")
                },
                actions = {
                    Button(onClick = onSyncClick) {
                        Text("전송")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onRecordClick) {
                Text(if (uiState.isRecording) "중단" else "녹음")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "오늘 · ${todayText()}",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = { onOpenFilter(uiState.selectedCategory) }) {
                    Text("전체 보기")
                }
            }
            CategoryFilterRow(
                selected = uiState.selectedCategory,
                onSelected = onCategorySelected
            )
            NotesList(notes = uiState.todayNotes, onNoteClick = onNoteClick)
        }

        if (uiState.isRecording) {
            RecordingOverlay(
                recordingSeconds = uiState.recordingSeconds,
                onStop = onRecordClick
            )
        }
    }
}

@Composable
private fun CategoryFilterRow(
    selected: NoteCategory?,
    onSelected: (NoteCategory?) -> Unit
) {
    val categories = listOf<NoteCategory?>(null) + NoteCategory.entries
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            categories.take(3).forEach { category ->
                TextButton(onClick = { onSelected(category) }) {
                    Text(
                        text = category?.label ?: "전체",
                        color = if (selected == category) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.drop(3).forEach { category ->
                TextButton(onClick = { onSelected(category) }) {
                    Text(
                        text = category?.label ?: "전체",
                        color = if (selected == category) {
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

@Composable
private fun NotesList(
    notes: List<Note>,
    onNoteClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (notes.isEmpty()) {
            item {
                Text("오늘 메모가 없습니다.")
            }
        } else {
            items(items = notes, key = { it.id }) { note ->
                NoteCard(note = note, onTap = { onNoteClick(note.id) })
            }
        }
    }
}

private fun todayText(): String {
    val now = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate()
    return now.format(DateTimeFormatter.ofPattern("M월 d일"))
}
