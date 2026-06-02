# DriveNote AI (MVP)

DriveNote는 운전 중 음성 메모를 빠르게 기록하고, 온디바이스 분류 후 Hermes로 수동 동기화하는 Android 앱입니다.

## 포함 기능

- Jetpack Compose + Material 3 UI
- Clean Architecture 스타일 (`data` / `domain` / `ui`)
- Room 기반 메모/설정 저장
- 음성 녹음(`MediaRecorder`) + STT(`SpeechRecognizer`)
- 분류 인터페이스 + 키워드 기반 fallback 분류기
- 수동 동기화 버튼 + Hermes 엔드포인트 설정
- WorkManager 기반 분류/동기화 워커

## 프로젝트 경로

- `/home/ubuntu/projects/drivenote`

## 참고

- 현재 환경에는 `gradle` 명령이 설치되어 있지 않아 CLI 빌드 검증은 수행하지 못했습니다.
- Android Studio에서 프로젝트를 열어 Sync/Build를 진행하면 됩니다.
