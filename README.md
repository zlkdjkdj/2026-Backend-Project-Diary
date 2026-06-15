# My Memory - 일기 및 메모 보관 서비스

이 프로젝트는 React(프론트엔드)와 Spring Boot 및 MongoDB(백엔드)를 활용하여 구현한 개인 일기장 애플리케이션입니다. 사용자 인증(JWT), 이미지 업로드, 그리고 백업 및 복원 기능을 제공합니다.

이 프로젝트는 백엔드와 연결되지 않은 상태에서도 프론트엔드에서 로컬 저장소 기반의 Mocking 모드로 구동 가능하며, 설정 변경을 통해 실제 MongoDB Atlas 데이터베이스 환경과 연결할 수 있습니다.

---

## 프로젝트 전체 디렉토리 구조

```text
my-memory/
├── my-memory-frontend/            # 프론트엔드 (React + TypeScript + Vite)
│   ├── src/
│   │   ├── api/                   # API 통신 계층 (백엔드 통신 및 Mocking 스위칭 제공)
│   │   │   ├── apiConfig.ts       # Mocking 사용 여부 감지 및 Axios 공통 인스턴스 설정
│   │   │   ├── authApi.ts         # 로그인/회원가입 API (Mock / Real)
│   │   │   └── diaryApi.ts        # 일기 생성/수정/삭제/검색 API (Mock / Real)
│   │   │
│   │   ├── components/            # UI 컴포넌트 레이어
│   │   │   ├── DiaryCard.tsx      # 일기 카드 아이템 레이아웃 및 테마 적용
│   │   │   ├── DiaryDetailModal.tsx # 일기 세부 본문 및 이미지 뷰어
│   │   │   ├── DiaryFormModal.tsx # 일기 작성 및 수정 폼
│   │   │   └── GoogleDriveWidget.tsx # 백업 파일 저장용 클라우드 위젯 UI
│   │   │
│   │   ├── pages/                 # 라우터 페이지 컴포넌트
│   │   │   ├── LoginPage.tsx      # 로그인 화면
│   │   │   ├── RegisterPage.tsx   # 회원가입 화면
│   │   │   └── MainPage.tsx       # 일기 목록, 필터링, 검색 및 백업 관리 화면
│   │   │
│   │   ├── types/                 # 공통 TypeScript 타입 선언
│   │   │   └── diary.ts           # Diary 데이터 규격 인터페이스 및 Auth DTO 포맷
│   │   │
│   │   ├── App.tsx                # 라우터 처리, 상태 보존 및 글로벌 내비게이션 바
│   │   ├── index.css              # 전역 스타일 및 테마 변수 바인딩
│   │   └── main.tsx               # 애플리케이션 엔트리 포인트
│   └── vite.config.ts             # 프론트엔드 설정 및 프록시 설정
│
└── my-memory-backend/             # 백엔드 (Spring Boot + MongoDB Atlas)
    ├── Dockerfile                 # 도커 이미지 배포용 설정 파일
    ├── build.gradle               # Gradle 의존성 및 프로젝트 빌드 설정
    ├── settings.gradle            # Gradle 프로젝트 세팅 설정
    ├── gradlew / gradlew.bat      # Gradle 래퍼 실행 스크립트
    ├── src/
    │   ├── main/
    │   │   ├── java/com/jaehyun/diary/
    │   │   │   ├── MyMemoryBackendApplication.java # Spring Boot 메인 진입점
    │   │   │   ├── api/                   # REST API 컨트롤러 레이어
    │   │   │   │   ├── AuthApiController.java     # 회원가입 및 로그인 처리
    │   │   │   │   ├── DiaryApiController.java    # 일기 등록/수정/삭제/검색 처리
    │   │   │   │   └── BackupApiController.java   # 데이터 백업 및 복원 기능
    │   │   │   │
    │   │   │   ├── dto/                   # 데이터 전송 객체 (DTO) 레이어
    │   │   │   │   ├── DiaryForm.java             # 일기 생성/수정 데이터 교환용 DTO
    │   │   │   │   └── AuthForm.java              # 로그인/회원가입 요청 및 응답 DTO
    │   │   │   │
    │   │   │   ├── entity/                # MongoDB 도메인 엔티티 레이어
    │   │   │   │   ├── DiaryEntity.java           # 일기 데이터베이스 매핑 모델
    │   │   │   │   ├── UserEntity.java            # 사용자 데이터베이스 매핑 모델
    │   │   │   │   └── UserRole.java              # 권한 분류 열거형 (USER, ADMIN)
    │   │   │   │
    │   │   │   ├── repository/            # 데이터 액세스 인터페이스 레이어
    │   │   │   │   ├── DiaryRepository.java       # 일기 쿼리 인터페이스
    │   │   │   │   └── UserRepository.java        # 사용자 조회 인터페이스
    │   │   │   │
    │   │   │   ├── service/               # 비즈니스 로직 레이어
    │   │   │   │   ├── DiaryService.java          # 일기 관련 비즈니스 로직
    │   │   │   │   ├── AuthService.java           # 사용자 인증 및 토큰 발급
    │   │   │   │   ├── FileService.java           # 업로드 파일 로컬 저장 및 처리
    │   │   │   │   └── BackupService.java         # 백업 파일 생성 및 복원 처리
    │   │   │   │
    │   │   │   └── config/                # 설정 및 보안 설정
    │   │   │       ├── MongoConfig.java           # MongoDB 연결 설정
    │   │   │       ├── WebConfig.java             # 정적 자원 매핑 설정
    │   │   │       ├── SecurityConfig.java        # 스프링 시큐리티 인가 설정
    │   │   │       ├── JwtFilter.java             # JWT 검증 및 인가 필터
    │   │   │       ├── JwtUtil.java               # JWT 생성 및 파싱
    │   │   │       ├── GlobalExceptionHandler.java # 전역 예외 처리 핸들러
    │   │   │       ├── CheckOwnership.java        # 작성자 검증용 커스텀 어노테이션
    │   │   │       └── OwnershipAspect.java       # 소유권 검증 AOP 구현체
    │   │   │
    │   │   └── resources/             # 설정 및 정적 자원 폴더
    │   │       ├── application.yml                 # DB 연결 및 JWT 설정 정보
    │   │       ├── static/                         # 정적 자원 폴더 (기본 빈 폴더)
    │   │       └── templates/                      # 템플릿 폴더 (기본 빈 폴더)
    │   │
    │   ├── test/                      # 백엔드 단위/통합 테스트 폴더
    │   │   └── java/com/jaehyun/diary/
    │   │       ├── MyMemoryBackendApplicationTests.java # 스프링 부트 로드 테스트
    │   │       └── service/
    │   │           └── DiaryServiceTest.java           # 일기 서비스 테스트
    │   │
    │   └── test.http                  # HTTP API 수동 요청 테스트 파일
    │
    └── uploads/                   # 업로드된 이미지 파일 저장 경로 (동적 생성)
```

---

## 프론트엔드 구조 및 비즈니스 로직 설명

프론트엔드는 Mocking 모드와 API 연동 모드로 전환 가능한 구조로 구현되었습니다.

### 1. Mocking 및 API 연동 전환 로직
*   **설정 (`apiConfig.ts`)**:
    *   브라우저의 `localStorage.getItem('use_mock')` 값에 따라 Mocking 여부를 결정합니다.
    *   API 연동 모드로 전환하려면 콘솔에서 다음 스크립트를 실행 후 새로고침합니다.
        ```javascript
        localStorage.setItem('use_mock', 'false');
        ```
*   **인증 흐름 (`authApi.ts`)**:
    *   Mock 모드: `localStorage`의 `mock_users` 배열을 사용하여 회원가입 및 로그인을 시뮬레이션하고 임의의 JWT를 반환합니다.
    *   API 연동 모드: 백엔드의 `/api/auth/login`, `/api/auth/register` 엔드포인트로 HTTP 통신을 수행합니다.
*   **일기 기능 흐름 (`diaryApi.ts`)**:
    *   Mock 모드: 일기 데이터는 `localStorage`의 `mock_diaries` 배열에서 관리하며, 이미지는 `FileReader` API를 통해 Base64 인코딩하여 저장합니다.
    *   API 연동 모드: HTTP 헤더에 JWT를 포함하여 백엔드의 `/api/diary` 엔드포인트와 통신합니다.

### 2. 주요 UI 컴포넌트
*   **상태 관리 (`App.tsx` & `MainPage.tsx`)**:
    *   사용자의 인증 상태와 모달 표시 여부를 React State로 관리합니다.
    *   로그인 완료 시 `MainPage`로 이동하여 데이터를 조회합니다.
*   **일기 상세 조회 (`DiaryCard.tsx` & `DiaryDetailModal.tsx`)**:
    *   일기의 감정 상태에 따라 카드 테마 색상이 동적으로 적용됩니다.
    *   카드 클릭 시 `DiaryDetailModal`을 통해 상세 내용과 첨부 이미지를 확인할 수 있습니다.

---

## 백엔드 구조 및 비즈니스 로직 설명

백엔드는 RESTful API 규격에 맞춘 계층형 아키텍처로 구현되었습니다.

### 1. 사용자 인증 로직
*   **회원가입 (`POST /api/auth/register`)**
    *   `UserRepository`를 통해 이메일 중복 여부를 확인합니다.
    *   `BCryptPasswordEncoder`를 사용하여 비밀번호를 해시 암호화한 후 저장합니다.
*   **로그인 (`POST /api/auth/login`)**
    *   사용자 존재 여부를 확인하고, 입력된 비밀번호와 해시된 비밀번호를 대조합니다.
    *   검증이 완료되면 `JwtUtil`을 통해 JWT(Access Token)를 발급하여 반환합니다.
*   **보안 필터링**
    *   `JwtFilter.java`: `OncePerRequestFilter`를 상속받아 HTTP 요청의 `Authorization` 헤더에서 JWT를 추출하고 유효성을 검증합니다.
    *   `SecurityConfig.java`: 인증 관련 경로(`/api/auth/**`) 및 정적 파일 경로(`/uploads/**`)를 제외한 모든 API 접근에 대해 인증을 요구합니다.

### 2. 일기(Diary) CRUD 로직
*   **일기 등록 (`POST /api/diary`)**
    *   이미지 파일이 포함된 경우 `FileService`를 통해 고유 파일명(UUID)으로 `uploads/` 폴더에 저장하고 URL 경로를 반환합니다.
    *   사용자의 인증 정보를 바탕으로 `DiaryEntity`를 생성하여 데이터베이스에 저장합니다.
*   **일기 수정 (`PUT /api/diary/{id}`)**
    *   요청한 사용자가 해당 일기의 작성자인지 검증합니다. 권한이 일치하지 않을 경우 예외를 반환합니다.
    *   새로운 이미지가 업로드된 경우 기존 파일을 대체하고 데이터를 업데이트합니다.

### 3. 데이터 백업 및 복원 로직
*   **데이터 백업 (`GET /api/backup`)**
    *   임시 디렉토리를 생성하여 일기 데이터를 `backup.json`으로 내보냅니다.
    *   `uploads/` 폴더에 존재하는 이미지 파일들을 `images.zip`으로 압축합니다.
    *   위 두 파일을 병합하여 하나의 ZIP 파일(`diary_backup.zip`)로 구성한 후 사용자에게 다운로드 스트림으로 전송합니다. 전송 완료 후 임시 파일은 삭제됩니다.
*   **데이터 복원 (`POST /api/restore`)**
    *   기존 데이터베이스와 업로드된 파일을 임시 백업 폴더로 이동시킵니다.
    *   기존 컬렉션을 초기화(`diaryRepository.deleteAll()`)하고, 복원용 ZIP 파일의 데이터와 이미지를 적용합니다.
    *   복원 중 에러가 발생할 경우, 작업 내역을 취소하고 임시 저장된 기존 데이터로 롤백(Rollback)하여 시스템 일관성을 유지합니다.

---

## 실행 및 테스트 방법

프론트는 vercel, 백엔드는 render로 배포 되었습니다.
https://2026-backend-project-diary.vercel.app

### 1. 백엔드 실행
백엔드 디렉토리(`my-memory-backend`)에서 아래 명령어를 실행합니다.
```bash
./gradlew bootRun
```
백엔드 서버는 기본적으로 `http://localhost:8089`에서 실행됩니다.

### 2. 프론트엔드 실행
프론트엔드 디렉토리(`my-memory-frontend`)에서 패키지를 설치하고 개발 서버를 실행합니다.
```bash
npm install
npm run dev
```
프론트엔드 서버는 `http://localhost:5173`에서 실행되며, API(`/api`) 및 업로드 파일(`/uploads`) 요청은 Vite 프록시 설정을 통해 백엔드 서버로 라우팅됩니다.
