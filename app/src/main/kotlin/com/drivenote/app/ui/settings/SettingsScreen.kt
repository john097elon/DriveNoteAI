package com.drivenote.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SettingsScreen(
    uiState: SettingsUiState,
    onEndpointChanged: (String) -> Unit,
    onAuthTokenChanged: (String) -> Unit,
    onSaveEndpoint: () -> Unit,
    onSyncNow: () -> Unit,
    onClearStatus: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("설정") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Hermes 연동", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = uiState.endpoint,
                        onValueChange = onEndpointChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("API 엔드포인트") }
                    )
                    OutlinedTextField(
                        value = uiState.authToken,
                        onValueChange = onAuthTokenChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("API 키 (Bearer, 선택)") }
                    )
                    Button(onClick = onSaveEndpoint, modifier = Modifier.fillMaxWidth()) {
                        Text("Hermes 설정 저장")
                    }
                    Button(onClick = onSyncNow, modifier = Modifier.fillMaxWidth()) {
                        Text("지금 동기화")
                    }
                    Text(
                        text = "마지막 동기화: ${
                            uiState.lastSyncAt?.let {
                                Instant.ofEpochMilli(it)
                                    .atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("M/d a h:mm"))
                            } ?: "기록 없음"
                        }",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            uiState.statusMessage?.let { message ->
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Button(onClick = onClearStatus, modifier = Modifier.fillMaxWidth()) {
                            Text("메시지 닫기")
                        }
                    }
                }
            }

            Text(
                text = "동기화는 수동 버튼으로만 실행됩니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
