package com.drivenote.app.audio

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.drivenote.app.R

class DriveNoteMediaService : MediaBrowserServiceCompat() {
    // 차량 마이크/PTT 버튼은 앱에서 직접 가로채지 못해 미디어 트랜스포트 제어로만 연동합니다.

    private lateinit var mediaSession: MediaSessionCompat

    override fun onCreate() {
        super.onCreate()

        mediaSession = MediaSessionCompat(this, TAG).apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            setCallback(
                object : MediaSessionCompat.Callback() {
                    override fun onPlay() {
                        RecordingService.start(this@DriveNoteMediaService)
                        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
                    }

                    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
                        onPlay()
                    }

                    override fun onPause() {
                        RecordingService.stop(this@DriveNoteMediaService)
                        updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
                    }

                    override fun onStop() {
                        RecordingService.stop(this@DriveNoteMediaService)
                        updatePlaybackState(PlaybackStateCompat.STATE_STOPPED)
                    }
                }
            )
            isActive = true
        }

        updatePlaybackState(PlaybackStateCompat.STATE_STOPPED)
        sessionToken = mediaSession.sessionToken
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot = BrowserRoot(ROOT_ID, null)

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        if (parentId != ROOT_ID) {
            result.sendResult(mutableListOf())
            return
        }

        val rootItem = MediaBrowserCompat.MediaItem(
            MediaDescriptionCompat.Builder()
                .setMediaId(ITEM_ID)
                .setTitle(getString(R.string.app_name))
                .setSubtitle(getString(R.string.auto_tap_to_record))
                .build(),
            MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        )
        result.sendResult(mutableListOf(rootItem))
    }

    override fun onDestroy() {
        mediaSession.release()
        super.onDestroy()
    }

    private fun updatePlaybackState(state: Int) {
        val actions = PlaybackStateCompat.ACTION_PLAY or
            PlaybackStateCompat.ACTION_PLAY_PAUSE or
            PlaybackStateCompat.ACTION_PAUSE or
            PlaybackStateCompat.ACTION_STOP

        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(actions)
                .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f)
                .build()
        )
    }

    companion object {
        private const val TAG = "DriveNoteMediaService"
        private const val ROOT_ID = "drivenote_root"
        private const val ITEM_ID = "drivenote_record_toggle"
    }
}
