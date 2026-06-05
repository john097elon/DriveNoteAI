package com.drivenote.app.audio

class SentenceBuffer(
    private val flushThreshold: Int = 60
) {
    private val sentenceEnders = setOf('.', '!', '?', '\n', '。', '！', '？')
    private val buffer = StringBuilder()

    fun append(token: String): String? {
        if (token.isEmpty()) return null
        buffer.append(token)

        val content = buffer.toString()
        val lastSentenceEnd = content.indexOfLast { it in sentenceEnders }
        if (lastSentenceEnd >= 0) {
            val sentence = content.substring(0, lastSentenceEnd + 1).trim()
            buffer.delete(0, lastSentenceEnd + 1)
            return sentence.ifBlank { null }
        }

        if (buffer.length >= flushThreshold) {
            val chunk = buffer.toString().trim()
            buffer.clear()
            return chunk.ifBlank { null }
        }

        return null
    }

    fun flush(): String? {
        val remaining = buffer.toString().trim()
        buffer.clear()
        return remaining.ifBlank { null }
    }

    fun clear() {
        buffer.clear()
    }
}
