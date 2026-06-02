package com.drivenote.app.domain.usecase

import com.drivenote.app.domain.repository.SettingsRepository
import javax.inject.Inject

class GetHermesEndpointUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): String = settingsRepository.getHermesEndpoint()
}
