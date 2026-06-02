package com.drivenote.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
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
    onSaveEndpoint: () -> Unit,
    onSyncNow: () -> Unit,
    onClearStatus: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("설정") })
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Hermes 연동")
            OutlinedTextField(
                value = uiState.endpoint,
                onValueChange = onEndpointChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("API 엔드포인트") }
            )
            Button(onClick = onSaveEndpoint) {
                Text("엔드포인트 저장")
            }
            Button(onClick = onSyncNow) {
                Text("지금 동기화")
            }
            Text(
                text = "마지막 동기화: ${
                    uiState.lastSyncAt?.let {
                        Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("M/d a h:mm"))
                    } ?: "기록 없음"
                }"
            )
            uiState.statusMessage?.let { message ->
                Text(message)
                Button(onClick = onClearStatus) {
                    Text("메시지 닫기")
                }
            }
            Text("동기화는 수동 버튼으로만 실행됩니다.")
        }
    }
}
