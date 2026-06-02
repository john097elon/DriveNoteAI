package com.drivenote.app.domain.usecase

import com.drivenote.app.domain.repository.SettingsRepository
import javax.inject.Inject

class SetHermesEndpointUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(endpoint: String) {
        settingsRepository.setHermesEndpoint(endpoint)
    }
}
