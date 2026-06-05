package com.drivenote.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drivenote.app.domain.model.Note
import com.drivenote.app.domain.model.NoteCategory
import com.drivenote.app.ui.home.component.NoteCard
import com.drivenote.app.ui.home.component.RecordingOverlay
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreen(
    uiState: HomeUiState,
    onRecordClick: () -> Unit,
    onOpenVoiceConversation: () -> Unit,
    onNoteClick: (String) -> Unit,
    onCategorySelected: (NoteCategory?) -> Unit,
    onOpenFilter: (NoteCategory?) -> Unit,
    onSyncClick: () -> Unit,
    onClearError: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val noteCount      = uiState.todayNotes.size
    val unsyncedCount  = uiState.todayNotes.count { !it.isSynced }
    val classifyingCount = uiState.todayNotes.count { !it.isClassified }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            onClearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                HomeHeader(
                    noteCount     = noteCount,
                    unsyncedCount = unsyncedCount,
                    onSyncClick   = onSyncClick,
                    onOpenVoiceConversation = onOpenVoiceConversation
                )
                CategoryFilterRow(
                    selected    = uiState.selectedCategory,
                    onSelected  = onCategorySelected,
                    onOpenFilter = onOpenFilter
                )
                NotesList(
                    notes            = uiState.todayNotes,
                    classifyingCount = classifyingCount,
                    onNoteClick      = onNoteClick
                )
            }

            RecordButton(
                isRecording = uiState.isRecording,
                onClick     = onRecordClick,
                modifier    = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 24.dp)
            )

            if (uiState.isRecording) {
                RecordingOverlay(
                    recordingSeconds = uiState.recordingSeconds,
                    onStop           = onRecordClick
                )
            }
        }
    }
}

@Composable
private fun RecordButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick  = onClick,
        modifier = modifier.height(72.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isRecording) MaterialTheme.colorScheme.error
                             else MaterialTheme.colorScheme.primary,
            contentColor   = if (isRecording) MaterialTheme.colorScheme.onError
                             else MaterialTheme.colorScheme.onPrimary
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Text(
            text  = if (isRecording) "녹음 중지" else "녹음 시작",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun HomeHeader(
    noteCount: Int,
    unsyncedCount: Int,
    onSyncClick: () -> Unit,
    onOpenVoiceConversation: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text  = "DriveNote",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text  = todayText(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (noteCount > 0) {
                val syncLabel = if (unsyncedCount > 0) "미동기화 ${unsyncedCount}개" else "모두 동기화됨"
                Text(
                    text  = "오늘 ${noteCount}개  $syncLabel",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (unsyncedCount > 0) MaterialTheme.colorScheme.tertiary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            FilledTonalButton(onClick = onSyncClick) {
                Text("전송", style = MaterialTheme.typography.labelLarge)
            }
            TextButton(onClick = onOpenVoiceConversation) {
                Text("Hermes 대화", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun CategoryFilterRow(
    selected: NoteCategory?,
    onSelected: (NoteCategory?) -> Unit,
    onOpenFilter: (NoteCategory?) -> Unit
) {
    val categories = listOf<NoteCategory?>(null) + NoteCategory.entries
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selected == category,
                    onClick  = { onSelected(category) },
                    label    = { Text(category?.label ?: "전체") },
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor     = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 12.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { onOpenFilter(selected) }) {
                Text(
                    text  = "카테고리별 전체 보기",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun NotesList(
    notes: List<Note>,
    classifyingCount: Int,
    onNoteClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 4.dp, end = 20.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (classifyingCount > 0) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.weight(1f),
                        color    = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                    Text(
                        text  = "${classifyingCount}개 분류 중",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        if (notes.isEmpty()) {
            item {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceContainerLow
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text  = "아직 메모가 없습니다",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text  = "아래 버튼으로 첫 번째 음성 메모를 시작하세요",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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
    return now.format(DateTimeFormatter.ofPattern("M월 d일 EEEE", Locale.KOREAN))
}
