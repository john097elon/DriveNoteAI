package com.drivenote.app.ui.detail.component

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun AudioPlayer(audioPath: String?) {
    if (audioPath.isNullOrBlank()) {
        Text("오디오 파일이 없습니다.")
        return
    }

    val mediaPlayerState = remember { mutableStateOf<MediaPlayer?>(null) }
    val isPlayingState = remember { mutableStateOf(false) }

    DisposableEffect(audioPath) {
        val player = MediaPlayer().apply {
            setDataSource(audioPath)
            prepare()
            setOnCompletionListener { isPlayingState.value = false }
        }
        mediaPlayerState.value = player
        onDispose {
            runCatching { player.stop() }
            player.release()
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = {
            val player = mediaPlayerState.value ?: return@Button
            if (isPlayingState.value) {
                player.pause()
                isPlayingState.value = false
            } else {
                player.start()
                isPlayingState.value = true
            }
        }) {
            Text(if (isPlayingState.value) "일시정지" else "재생")
        }
        Text(audioPath.substringAfterLast('/'))
    }
}
