package com.drivenote.app.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.drivenote.app.audio.AudioRecorderManager
import com.drivenote.app.audio.RecordingService
import com.drivenote.app.audio.SttManager
import com.drivenote.app.domain.model.Note
import com.drivenote.app.domain.model.NoteCategory
import com.drivenote.app.domain.model.NoteSource
import com.drivenote.app.domain.usecase.GetNotesUseCase
import com.drivenote.app.domain.usecase.SaveNoteUseCase
import com.drivenote.app.sync.ClassificationWorker
import com.drivenote.app.sync.SyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    getNotesUseCase: GetNotesUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val audioRecorderManager: AudioRecorderManager,
    private val sttManager: SttManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var allTodayNotes: List<Note> = emptyList()

    init {
        viewModelScope.launch {
            getNotesUseCase().collect { notes ->
                allTodayNotes = notes.filter { it.createdAt.isToday() }
                applyCategoryFilter()
            }
        }
    }

    fun selectCategory(category: NoteCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
        applyCategoryFilter()
    }

    fun toggleRecording() {
        if (_uiState.value.isRecording) {
            stopRecordingAndSave()
        } else {
            startRecording()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun syncNow() {
        WorkManager.getInstance(context)
            .enqueue(OneTimeWorkRequestBuilder<SyncWorker>().build())
    }

    private fun startRecording() {
        runCatching { RecordingService.start(context) }
            .onSuccess {
                timerJob?.cancel()
                timerJob = viewModelScope.launch {
                    var startError: String? = null
                    for (attempt in 0..5) {
                        startError = RecordingService.consumeLastStartError()
                        if (startError != null) break
                        if (attempt < 5) {
                            delay(100L)
                        }
                    }

                    if (startError != null) {
                        _uiState.update {
                            it.copy(
                                isRecording = false,
                                recordingSeconds = 0,
                                error = startError
                            )
                        }
                        return@launch
                    }

                    _uiState.update { it.copy(isRecording = true, recordingSeconds = 0, error = null) }
                    while (true) {
                        delay(1000L)
                        _uiState.update { state ->
                            state.copy(recordingSeconds = state.recordingSeconds + 1)
                        }
                    }
                }
            }
            .onFailure { throwable ->
                _uiState.update {
                    it.copy(error = throwable.message ?: "녹음을 시작할 수 없습니다.")
                }
            }
    }

    private fun stopRecordingAndSave() {
        timerJob?.cancel()
        var fallbackAudioPath = audioRecorderManager.getCurrentFilePath()
        runCatching { RecordingService.stop(context) }
            .onFailure {
                fallbackAudioPath = audioRecorderManager.stopRecording() ?: fallbackAudioPath
            }
        _uiState.update { it.copy(isRecording = false) }

        viewModelScope.launch {
            var audioPath = RecordingService.consumeLastStoppedAudioPath()
            var pollCount = 0
            while (audioPath == null && pollCount < 5) {
                delay(100L)
                audioPath = RecordingService.consumeLastStoppedAudioPath()
                pollCount++
            }
            if (audioPath == null) {
                audioPath = audioRecorderManager.stopRecording() ?: fallbackAudioPath
            }

            val transcribed = sttManager.transcribe(context).getOrDefault("")
            val text = transcribed.ifBlank { "재녹음 필요: 음성 인식에 실패했습니다." }
            val note = Note(
                text = text,
                source = NoteSource.VOICE,
                audioPath = audioPath,
                isClassified = false,
                isSynced = false
            )
            saveNoteUseCase(note)
            WorkManager.getInstance(context)
                .enqueue(OneTimeWorkRequestBuilder<ClassificationWorker>().build())
        }
    }

    private fun applyCategoryFilter() {
        val selected = _uiState.value.selectedCategory
        val filtered = selected?.let { category ->
            allTodayNotes.filter { it.category == category }
        } ?: allTodayNotes
        _uiState.update { it.copy(todayNotes = filtered) }
    }

    private fun Long.isToday(): Boolean {
        val createdDate = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
        val currentDate = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate()
        return createdDate == currentDate
    }
}
