package com.drivenote.app.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioRecorderManager @Inject constructor() {
    private var mediaRecorder: MediaRecorder? = null
    private var currentFilePath: String? = null

    @Throws(IOException::class, SecurityException::class)
    fun startRecording(context: Context): String {
        stopRecording()
        val outputDir = File(context.filesDir, "audio").apply { mkdirs() }
        val outputFile = File(outputDir, "${System.currentTimeMillis()}.m4a")
        currentFilePath = outputFile.absolutePath

        val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)
            prepare()
            start()
        }

        mediaRecorder = recorder
        return outputFile.absolutePath
    }

    fun stopRecording(): String? {
        val path = currentFilePath
        val recorder = mediaRecorder ?: return path
        runCatching { recorder.stop() }
        recorder.reset()
        recorder.release()
        mediaRecorder = null
        currentFilePath = null
        return path
    }

    fun getCurrentFilePath(): String? = currentFilePath
}
