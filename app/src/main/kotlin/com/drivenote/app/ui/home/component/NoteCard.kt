package com.drivenote.app.ui.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.drivenote.app.domain.model.Note
import com.drivenote.app.ui.common.CategoryChip
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun NoteCard(
    note: Note,
    onTap: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTap)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CategoryChip(category = note.category)
                Text(
                    text = if (note.isClassified) "분류됨" else "분류 중...",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.text,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = buildString {
                    if (note.tags.isNotEmpty()) {
                        append(note.tags.joinToString(separator = " ") { "#$it" })
                        append("  ")
                    }
                    append(note.createdAt.toKoreanTimeText())
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun Long.toKoreanTimeText(): String {
    val time = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalTime()
    return time.format(DateTimeFormatter.ofPattern("a h:mm"))
}
