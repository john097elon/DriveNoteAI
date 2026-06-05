package com.drivenote.app.auto

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import com.drivenote.app.R
import com.drivenote.app.audio.RecordingService
import com.drivenote.app.audio.VoiceConversationService

class DriveNoteCarScreen(carContext: CarContext) : Screen(carContext) {

    private var isRecording = false

    override fun onGetTemplate(): Template {
        val isVoiceActive = VoiceConversationService.state.value.isActive
        val message = if (isVoiceActive) {
            "Hermes 실시간 대화 중 (휴대폰)"
        } else {
            "Hermes 대기 중"
        }
        val recordActionLabel = if (isRecording) {
            carContext.getString(R.string.auto_record_stop)
        } else {
            carContext.getString(R.string.auto_record_start)
        }
        val voiceActionLabel = if (isVoiceActive) {
            "대화 중지"
        } else {
            "Hermes 대화 시작"
        }

        val recordToggleAction = Action.Builder()
            .setTitle(recordActionLabel)
            .setOnClickListener {
                if (isRecording) {
                    RecordingService.stop(carContext)
                } else {
                    RecordingService.start(carContext)
                }
                isRecording = !isRecording
                invalidate()
            }
            .build()

        val voiceToggleAction = Action.Builder()
            .setTitle(voiceActionLabel)
            .setOnClickListener {
                if (isVoiceActive) {
                    VoiceConversationService.stop(carContext)
                } else {
                    VoiceConversationService.start(carContext)
                }
                invalidate()
            }
            .build()

        return MessageTemplate.Builder(message)
            .setTitle(carContext.getString(R.string.app_name))
            .setHeaderAction(Action.APP_ICON)
            .addAction(recordToggleAction)
            .addAction(voiceToggleAction)
            .build()
    }
}
