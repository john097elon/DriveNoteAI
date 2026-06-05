package com.drivenote.app.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.drivenote.app.MainActivity
import com.drivenote.app.domain.model.VoiceConversationState
import com.drivenote.app.domain.usecase.VoiceConversationOrchestrator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VoiceConversationService : Service() {

    @Inject
    lateinit var orchestrator: VoiceConversationOrchestrator

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            orchestrator.state.collectLatest { state ->
                _state.value = state
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when (intent?.action) {
            ACTION_START -> {
                startForegroundWithNotification()
                orchestrator.start(this, serviceScope)
                START_STICKY
            }

            ACTION_STOP -> {
                orchestrator.stop()
                stopSelf()
                START_NOT_STICKY
            }

            else -> START_NOT_STICKY
        }
    }

    override fun onDestroy() {
        orchestrator.stop()
        _state.value = VoiceConversationState()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun startForegroundWithNotification() {
        createNotificationChannelIfNeeded()

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentTitle("Hermes 음성 대화 중")
            .setContentText("실시간 음성 대화가 실행 중입니다.")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java) ?: return
        val existing = manager.getNotificationChannel(CHANNEL_ID)
        if (existing != null) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Hermes 음성 대화",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Hermes 실시간 음성 대화 상태를 표시합니다."
        }
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val ACTION_START = "com.drivenote.app.action.VOICE_CONVERSATION_START"
        const val ACTION_STOP = "com.drivenote.app.action.VOICE_CONVERSATION_STOP"

        private const val CHANNEL_ID = "drivenote_voice_conversation"
        private const val NOTIFICATION_ID = 1203

        private val _state = MutableStateFlow(VoiceConversationState())
        val state: StateFlow<VoiceConversationState> = _state.asStateFlow()

        fun start(context: Context) {
            val intent = Intent(context, VoiceConversationService::class.java).apply {
                action = ACTION_START
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, VoiceConversationService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
