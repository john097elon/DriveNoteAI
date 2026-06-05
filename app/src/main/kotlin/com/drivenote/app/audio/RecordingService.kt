package com.drivenote.app.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import java.util.concurrent.atomic.AtomicReference

@AndroidEntryPoint
class RecordingService : Service() {

    @Inject
    lateinit var audioRecorderManager: AudioRecorderManager

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when (intent?.action) {
            ACTION_START -> {
                runCatching { startRecordingForeground() }
                    .onFailure {
                        lastStartError.set(it.message ?: "녹음을 시작할 수 없습니다.")
                        clearForegroundAndStop()
                    }
                START_NOT_STICKY
            }

            ACTION_STOP -> {
                stopRecordingInternal()
                START_NOT_STICKY
            }

            else -> START_NOT_STICKY
        }
    }

    private fun startRecordingForeground() {
        createNotificationChannelIfNeeded()
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
        val startedPath = audioRecorderManager.startRecording(this)
        lastKnownRecordingPath.set(startedPath)
    }

    private fun stopRecordingInternal() {
        val stoppedPath = audioRecorderManager.stopRecording() ?: lastKnownRecordingPath.get()
        lastStoppedAudioPath.set(stoppedPath)
        lastKnownRecordingPath.set(null)
        clearForegroundAndStop()
    }

    private fun clearForegroundAndStop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentTitle("음성 녹음 중")
            .setContentText("운전 메모를 녹음하고 있습니다.")
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "운전 메모 녹음",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "운전 메모 녹음 상태를 표시합니다."
        }
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val ACTION_START = "com.drivenote.app.action.RECORDING_START"
        const val ACTION_STOP = "com.drivenote.app.action.RECORDING_STOP"

        private const val CHANNEL_ID = "drivenote_recording"
        private const val NOTIFICATION_ID = 1201

        private val lastStoppedAudioPath = AtomicReference<String?>(null)

        private val lastKnownRecordingPath = AtomicReference<String?>(null)
        private val lastStartError = AtomicReference<String?>(null)

        fun start(context: Context) {
            lastStoppedAudioPath.set(null)
            lastKnownRecordingPath.set(null)
            lastStartError.set(null)
            val intent = Intent(context, RecordingService::class.java).apply {
                action = ACTION_START
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, RecordingService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

        fun consumeLastStoppedAudioPath(): String? {
            return lastStoppedAudioPath.getAndSet(null)
        }

        fun consumeLastStartError(): String? {
            return lastStartError.getAndSet(null)
        }
    }
}
