package com.drivenote.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.drivenote.app.domain.model.NoteCategory
import com.drivenote.app.ui.theme.Amber500
import com.drivenote.app.ui.theme.Blue500
import com.drivenote.app.ui.theme.Gray400
import com.drivenote.app.ui.theme.Green500
import com.drivenote.app.ui.theme.Orange500
import com.drivenote.app.ui.theme.Purple500

@Composable
fun CategoryChip(category: NoteCategory) {
    Text(
        text = category.label,
        color = Color.White,
        modifier = Modifier
            .background(color = colorFor(category), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

private fun colorFor(category: NoteCategory): Color {
    return when (category) {
        NoteCategory.WORK -> Blue500
        NoteCategory.DEV -> Green500
        NoteCategory.INVEST -> Amber500
        NoteCategory.TODO -> Orange500
        NoteCategory.RESEARCH -> Purple500
        NoteCategory.UNCLASSIFIED -> Gray400
    }
}
