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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
    onNoteClick: (String) -> Unit,
    onCategorySelected: (NoteCategory?) -> Unit,
    onOpenFilter: (NoteCategory?) -> Unit,
    onSyncClick: () -> Unit,
    onClearError: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val noteCount = uiState.todayNotes.size
    val unsyncedCount = uiState.todayNotes.count { !it.isSynced }
    val classifyingCount = uiState.todayNotes.count { !it.isClassified }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            onClearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onRecordClick,
                modifier = Modifier
                    .width(264.dp)
                    .height(72.dp),
                containerColor = if (uiState.isRecording) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                },
                contentColor = if (uiState.isRecording) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                }
            ) {
                Text(
                    text = if (uiState.isRecording) "녹음 중지" else "녹음 시작",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                HomeHeader(
                    noteCount = noteCount,
                    unsyncedCount = unsyncedCount,
                    onSyncClick = onSyncClick
                )
                CategoryFilterRow(
                    selected = uiState.selectedCategory,
                    onSelected = onCategorySelected,
                    onOpenFilter = onOpenFilter
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                NotesList(
                    notes = uiState.todayNotes,
                    classifyingCount = classifyingCount,
                    onNoteClick = onNoteClick
                )
            }

            if (uiState.isRecording) {
                RecordingOverlay(
                    recordingSeconds = uiState.recordingSeconds,
                    onStop = onRecordClick
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    noteCount: Int,
    unsyncedCount: Int,
    onSyncClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "DriveNote",
                style = MaterialTheme.typography.headlineMedium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = todayText(),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "오늘 메모 ${noteCount}개 · 동기화 대기 ${unsyncedCount}개",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                FilledTonalButton(onClick = onSyncClick) {
                    Text("지금 전송")
                }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "빠른 카테고리",
            style = MaterialTheme.typography.labelLarge
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { category ->
                FilterChip(
                    selected = selected == category,
                    onClick = { onSelected(category) },
                    label = { Text(category?.label ?: "전체") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
        FilledTonalButton(
            onClick = { onOpenFilter(selected) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("카테고리별 전체 보기")
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
        contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (classifyingCount > 0) {
            item {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainerLow
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "메모 ${classifyingCount}개를 분류 중입니다.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
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
                            .padding(horizontal = 20.dp, vertical = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "오늘 메모가 아직 없습니다.",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "하단의 녹음 시작 버튼을 눌러 바로 기록해 보세요.",
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
