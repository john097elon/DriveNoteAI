package com.drivenote.app.auto

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session
import com.drivenote.app.domain.usecase.VoiceConversationOrchestrator
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DriveNoteCarSession : Session() {
    private val sessionScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var stateJob: Job? = null

    override fun onCreateScreen(intent: Intent): Screen {
        val screen = DriveNoteCarScreen(carContext)
        val entryPoint = EntryPointAccessors.fromApplication(
            carContext.applicationContext,
            VoiceOrchestratorEntryPoint::class.java
        )

        stateJob?.cancel()
        stateJob = sessionScope.launch {
            entryPoint.voiceConversationOrchestrator().state.collectLatest {
                screen.invalidate()
            }
        }
        return screen
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface VoiceOrchestratorEntryPoint {
    fun voiceConversationOrchestrator(): VoiceConversationOrchestrator
}
