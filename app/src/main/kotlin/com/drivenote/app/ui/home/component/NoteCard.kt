package com.drivenote.app.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.drivenote.app.domain.model.Note
import com.drivenote.app.domain.model.NoteCategory
import com.drivenote.app.ui.common.CategoryChip
import com.drivenote.app.ui.theme.CategoryAmber
import com.drivenote.app.ui.theme.CategoryBlue
import com.drivenote.app.ui.theme.CategoryGray
import com.drivenote.app.ui.theme.CategoryGreen
import com.drivenote.app.ui.theme.CategoryOrange
import com.drivenote.app.ui.theme.CategoryViolet
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun NoteCard(
    note: Note,
    onTap: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTap),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(categoryColorFor(note.category))
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryChip(category = note.category)
                    StatusPill(
                        text = if (note.isClassified) "분류 완료" else "분류 중",
                        active = note.isClassified
                    )
                    StatusPill(
                        text = if (note.isSynced) "동기화 완료" else "동기화 대기",
                        active = note.isSynced
                    )
                }
                Text(
                    text = note.text.ifBlank { "내용 없음" },
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (note.tags.isNotEmpty()) {
                    Text(
                        text = note.tags.take(3).joinToString("  ") { "#$it" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                Text(
                    text = note.createdAt.toKoreanTimeText(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatusPill(
    text: String,
    active: Boolean
) {
    Surface(
        color = if (active) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        contentColor = if (active) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private fun categoryColorFor(category: NoteCategory): Color {
    return when (category) {
        NoteCategory.WORK -> CategoryBlue
        NoteCategory.DEV -> CategoryGreen
        NoteCategory.INVEST -> CategoryAmber
        NoteCategory.TODO -> CategoryOrange
        NoteCategory.RESEARCH -> CategoryViolet
        NoteCategory.UNCLASSIFIED -> CategoryGray
    }
}

private fun Long.toKoreanTimeText(): String {
    val time = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalTime()
    return time.format(DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN))
}
