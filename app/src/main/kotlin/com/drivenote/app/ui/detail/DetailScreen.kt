package com.drivenote.app.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drivenote.app.domain.model.NoteCategory
import com.drivenote.app.ui.detail.component.AudioPlayer

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DetailScreen(
    uiState: DetailUiState,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onCategoryChange: (NoteCategory) -> Unit,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    onSave: () -> Unit,
    onClearError: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var newTag by remember { mutableStateOf("") }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            onClearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("메모 상세") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("뒤로") }
                },
                actions = {
                    TextButton(onClick = onDelete) { Text("삭제") }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val note = uiState.note ?: return@Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = note.text, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = if (note.isSynced) "Hermes 동기화: 완료" else "Hermes 동기화: 미완료",
                style = MaterialTheme.typography.bodySmall
            )

            Text("카테고리")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(NoteCategory.entries) { category ->
                    Button(onClick = { onCategoryChange(category) }) {
                        Text(category.label)
                    }
                }
            }

            Text("태그")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = newTag,
                    onValueChange = { newTag = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("태그 추가") }
                )
                Button(onClick = {
                    onAddTag(newTag)
                    newTag = ""
                }) {
                    Text("추가")
                }
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(note.tags) { tag ->
                    TextButton(onClick = { onRemoveTag(tag) }) { Text("#$tag ✕") }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("오디오")
            AudioPlayer(audioPath = note.audioPath)
            TextButton(onClick = { }) {
                Text("재녹음")
            }

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                Text(if (uiState.isSaving) "저장 중..." else "저장")
            }
        }
    }
}
