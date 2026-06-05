package com.drivenote.app.auto

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import com.drivenote.app.R
import com.drivenote.app.audio.RecordingService

class DriveNoteCarScreen(carContext: CarContext) : Screen(carContext) {

    private var isRecording = false

    override fun onGetTemplate(): Template {
        val message = if (isRecording) {
            carContext.getString(R.string.auto_recording_now)
        } else {
            carContext.getString(R.string.auto_tap_to_record)
        }
        val actionLabel = if (isRecording) {
            carContext.getString(R.string.auto_record_stop)
        } else {
            carContext.getString(R.string.auto_record_start)
        }

        val toggleAction = Action.Builder()
            .setTitle(actionLabel)
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

        return MessageTemplate.Builder(message)
            .setTitle(carContext.getString(R.string.app_name))
            .setHeaderAction(Action.APP_ICON)
            .addAction(toggleAction)
            .build()
    }
}
