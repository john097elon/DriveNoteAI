package com.drivenote.app.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RecordingOverlay(
    recordingSeconds: Int,
    onStop: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "녹음 중", color = Color.White, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "${recordingSeconds}s",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge
        )
        Button(
            onClick = onStop,
            modifier = Modifier.size(width = 180.dp, height = 64.dp)
        ) {
            Text("중단")
        }
    }
}
