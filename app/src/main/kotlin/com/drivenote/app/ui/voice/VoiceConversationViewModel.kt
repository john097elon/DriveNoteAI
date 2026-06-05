package com.drivenote.app.ui.voice

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivenote.app.audio.VoiceConversationService
import com.drivenote.app.domain.model.VoiceConversationPhase
import com.drivenote.app.domain.usecase.VoiceConversationOrchestrator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class VoiceConversationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    orchestrator: VoiceConversationOrchestrator
) : ViewModel() {

    val uiState: StateFlow<VoiceUiState> = orchestrator.state
        .map { state ->
            val status = when (state.phase) {
                VoiceConversationPhase.IDLE -> "대기 중"
                VoiceConversationPhase.LISTENING -> "듣는 중..."
                VoiceConversationPhase.PROCESSING -> "응답 생성 중..."
                VoiceConversationPhase.SPEAKING -> "Hermes가 말하는 중..."
                VoiceConversationPhase.ERROR -> state.errorMessage ?: "오류가 발생했습니다."
            }

            VoiceUiState(
                isActive = state.isActive,
                status = status,
                partialUserText = state.partialUserText.ifBlank { state.finalUserText },
                assistantText = state.assistantText
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VoiceUiState()
        )

    fun onToggleConversation() {
        if (uiState.value.isActive) {
            VoiceConversationService.stop(context)
        } else {
            VoiceConversationService.start(context)
        }
    }
}
