package com.drivenote.app.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.drivenote.app.domain.repository.SettingsRepository
import com.drivenote.app.domain.usecase.GetHermesEndpointUseCase
import com.drivenote.app.domain.usecase.SetHermesEndpointUseCase
import com.drivenote.app.sync.SyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val endpoint: String = "",
    val authToken: String = "",
    val lastSyncAt: Long? = null,
    val statusMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getHermesEndpointUseCase: GetHermesEndpointUseCase,
    private val setHermesEndpointUseCase: SetHermesEndpointUseCase,
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    endpoint = getHermesEndpointUseCase(),
                    authToken = settingsRepository.getHermesAuthToken(),
                    lastSyncAt = settingsRepository.getLastSyncAt()
                )
            }
        }
    }

    fun setEndpoint(endpoint: String) {
        _uiState.update { it.copy(endpoint = endpoint) }
    }

    fun setAuthToken(authToken: String) {
        _uiState.update { it.copy(authToken = authToken) }
    }

    fun saveEndpoint() {
        viewModelScope.launch {
            setHermesEndpointUseCase(_uiState.value.endpoint)
            settingsRepository.setHermesAuthToken(_uiState.value.authToken)
            _uiState.update { it.copy(statusMessage = "Hermes 연결 설정을 저장했습니다.") }
        }
    }

    fun syncNow() {
        WorkManager.getInstance(context)
            .enqueue(OneTimeWorkRequestBuilder<SyncWorker>().build())
        _uiState.update { it.copy(statusMessage = "동기화를 요청했습니다.") }
    }

    fun clearStatus() {
        _uiState.update { it.copy(statusMessage = null) }
    }
}
