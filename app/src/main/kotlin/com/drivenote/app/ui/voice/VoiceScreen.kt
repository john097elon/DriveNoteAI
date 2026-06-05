package com.drivenote.app.ui.voice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun VoiceScreen(
    viewModel: VoiceConversationViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Hermes 실시간 대화") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "상태: ${uiState.status}",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = if (uiState.partialUserText.isBlank()) "사용자 발화: (대기 중)" else "사용자 발화: ${uiState.partialUserText}",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = if (uiState.assistantText.isBlank()) "Hermes 응답: (아직 없음)" else "Hermes 응답: ${uiState.assistantText}",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 14,
                overflow = TextOverflow.Ellipsis
            )

            Button(
                onClick = viewModel::onToggleConversation,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp),
                contentPadding = PaddingValues(vertical = 20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isActive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    contentColor = if (uiState.isActive) {
                        MaterialTheme.colorScheme.onError
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    }
                )
            ) {
                Text(
                    text = if (uiState.isActive) "대화 중지" else "대화 시작",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
