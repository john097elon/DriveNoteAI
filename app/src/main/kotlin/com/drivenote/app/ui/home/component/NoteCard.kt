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
import androidx.compose.ui.Alignment
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
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(categoryColorFor(note.category))
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryChip(category = note.category)
                    if (!note.isClassified) StatusPill("분류 중")
                    if (!note.isSynced) StatusPill("미동기화")
                }
                Text(
                    text     = note.text.ifBlank { "내용 없음" },
                    style    = MaterialTheme.typography.bodyLarge,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color    = MaterialTheme.colorScheme.onSurface
                )
                if (note.tags.isNotEmpty()) {
                    Text(
                        text  = note.tags.take(3).joinToString("  ") { "#$it" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                Text(
                    text  = note.createdAt.toKoreanTimeText(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatusPill(text: String) {
    Surface(
        color        = MaterialTheme.colorScheme.surfaceContainerHighest,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape        = MaterialTheme.shapes.extraSmall
    ) {
        Text(
            text     = text,
            style    = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private fun categoryColorFor(category: NoteCategory): Color = when (category) {
    NoteCategory.WORK         -> CategoryBlue
    NoteCategory.DEV          -> CategoryGreen
    NoteCategory.INVEST       -> CategoryAmber
    NoteCategory.TODO         -> CategoryOrange
    NoteCategory.RESEARCH     -> CategoryViolet
    NoteCategory.UNCLASSIFIED -> CategoryGray
}

private fun Long.toKoreanTimeText(): String {
    val time = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalTime()
    return time.format(DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN))
}
