package com.drivenote.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.drivenote.app.domain.model.NoteCategory
import com.drivenote.app.ui.theme.CategoryAmber
import com.drivenote.app.ui.theme.CategoryBlue
import com.drivenote.app.ui.theme.CategoryGray
import com.drivenote.app.ui.theme.CategoryGreen
import com.drivenote.app.ui.theme.CategoryOrange
import com.drivenote.app.ui.theme.CategoryViolet

@Composable
fun CategoryChip(category: NoteCategory) {
    Text(
        text = category.label,
        color = Color.White,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier
            .background(color = colorFor(category), shape = RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

private fun colorFor(category: NoteCategory): Color {
    return when (category) {
        NoteCategory.WORK -> CategoryBlue
        NoteCategory.DEV -> CategoryGreen
        NoteCategory.INVEST -> CategoryAmber
        NoteCategory.TODO -> CategoryOrange
        NoteCategory.RESEARCH -> CategoryViolet
        NoteCategory.UNCLASSIFIED -> CategoryGray
    }
}
