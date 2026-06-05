package com.drivenote.app.auto

import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator

class DriveNoteCarAppService : CarAppService() {

    override fun createHostValidator(): HostValidator {
        // 개발용 스켈레톤: 차량/에뮬레이터 호스트 검증은 배포 단계에서 제한해야 합니다.
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(): Session = DriveNoteCarSession()
}
