# 📔 My Memory - 일기 및 메모 보관 서비스

이 프로젝트는 **React(프론트엔드)**와 **Spring Boot + MongoDB(백엔드)**를 활용하여 구축된 개인 일기장 애플리케이션입니다. 사용자 인증(JWT), 이미지 업로드, 그리고 안전성 및 데이터 정합성을 완벽하게 보장하는 백업 및 복원 기능을 제공합니다.

이 프로젝트는 백엔드가 오프라인 상태일 때도 프론트엔드에서 100% 브라우저 로컬 저장소 기반의 Mocking 모드로 구동할 수 있으며, 백엔드 연결 설정을 통해 실제 MongoDB Atlas 데이터베이스 환경과 매끄럽게 연결되는 유연한 전환 구조를 탑재하고 있습니다.

---

## 📂 프로젝트 전체 디렉토리 구조

```text
my-memory/
├── my-memory-frontend/            # 프론트엔드 (React + TypeScript + Vite)
│   ├── src/
│   │   ├── api/                   # API 통신 계층 (백엔드 통신 & Mocking 스위칭 제공)
│   │   │   ├── apiConfig.ts       # Mocking 사용 여부 감지 및 Axios 공통 인스턴스 설정
│   │   │   ├── authApi.ts         # 로그인/회원가입 API 정의 (Mock / Real)
│   │   │   └── diaryApi.ts        # 일기 생성/수정/삭제/검색 API 정의 (Mock / Real)
│   │   │
│   │   ├── components/            # UI 컴포넌트 레이어
│   │   │   ├── DiaryCard.tsx      # 일기 카드 아이템 레이아웃 및 감정별 테마 적용
│   │   │   ├── DiaryDetailModal.tsx # 일기 세부 본문 및 이미지 팝업 뷰어
│   │   │   ├── DiaryFormModal.tsx # 일기 작성 및 수정 폼 (이미지 미리보기 지원)
│   │   │   └── GoogleDriveWidget.tsx # (선택사항) 백업 파일 저장용 클라우드 위젯 UI
│   │   │
│   │   ├── pages/                 # 라우터 페이지 컴포넌트
│   │   │   ├── LoginPage.tsx      # 로그인 화면
│   │   │   ├── RegisterPage.tsx   # 회원가입 화면
│   │   │   └── MainPage.tsx       # 일기 목록, 필터링, 검색 및 백업 관리 메인 대시보드
│   │   │
│   │   ├── types/                 # 공통 TypeScript 타입 선언
│   │   │   └── diary.ts           # Diary 데이터 규격 인터페이스, Auth DTO 포맷, 감정 상수군
│   │   │
│   │   ├── App.tsx                # 라우터 처리, 상태 보존, 헤더 글로벌 내비게이션 바
│   │   ├── index.css              # 전역 스타일 및 HSL 기반 감정 테마 변수 바인딩
│   │   └── main.tsx               # 애플리케이션 엔트리 포인트
│   └── vite.config.ts             # 프론트엔드 설정 및 /uploads, /api 프록시 설정
│
└── my-memory-backend/             # 백엔드 (Spring Boot + MongoDB Atlas)
    ├── src/main/java/com/jaehyun/diary/
    │   ├── MyMemoryBackendApplication.java # Spring Boot 메인 진입점
    │   ├── api/                   # REST API 컨트롤러 레이어
    │   │   ├── AuthApiController.java     # 회원가입 및 로그인 처리
    │   │   ├── DiaryApiController.java    # 일기 등록/수정/삭제/검색 처리
    │   │   └── BackupApiController.java   # 데이터 백업 및 복원 파일 업/다운로드
    │   │
    │   ├── dto/                   # 데이터 전송 객체 (DTO) 레이어 (참고폴더 Form 매핑)
    │   │   ├── DiaryForm.java             # 일기 생성/수정 데이터 교환용 DTO
    │   │   └── AuthForm.java              # 로그인/회원가입 요청 및 응답 규격 래퍼
    │   │
    │   ├── entity/                # MongoDB 도메인 엔티티 레이어
    │   │   ├── DiaryEntity.java           # 일기 데이터베이스 매핑 모델
    │   │   ├── UserEntity.java            # 사용자 데이터베이스 매핑 모델
    │   │   └── UserRole.java              # 권한 분류 열거형 (USER, ADMIN)
    │   │
    │   ├── repository/            # 데이터 액세스 인터페이스 레이어
    │   │   ├── DiaryRepository.java       # 일기 쿼리 정의 (findByUserId 등)
    │   │   └── UserRepository.java        # 사용자 존재 및 이메일 탐색 정의
    │   │
    │   ├── service/               # 핵심 비즈니스 로직 레이어
    │   │   ├── DiaryService.java          # 일기 작성/조회 권한 체크 및 DB 매핑
    │   │   ├── AuthService.java           # BCrypt 비밀번호 암호화 및 JWT 토큰 빌드
    │   │   ├── FileService.java           # 업로드된 MultipartFile 로컬 저장 및 UUID 고유명 처리
    │   │   └── BackupService.java         # 백업 파일 생성 및 롤백 보장형 데이터 복원
    │   │
    │   └── config/                # 설정 및 보안 설정
    │       ├── MongoConfig.java           # MongoDB Atlas 연결 설정
    │       ├── WebConfig.java             # 로컬 이미지(/uploads) 정적 자원 매핑
    │       ├── SecurityConfig.java        # 스프링 시큐리티 인가 설정 및 JWT 필터 등록
    │       ├── JwtFilter.java             # 요청 헤더 Bearer 토큰 파싱 및 인가 필터
    │       └── JwtUtil.java               # JWT 빌드, 검증 및 클레임(Email, Role) 추출
    │
    └── uploads/                   # 업로드된 이미지 파일 저장 경로 (프로젝트 루트 생성)
```

---

## 🎨 프론트엔드 구조 및 비즈니스 로직 설명

프론트엔드는 단독(Mocking) 혹은 백엔드 연동(Real-API) 모드로 유연하게 스위칭되는 구조와 반응형 모던 UI 레이아웃으로 이루어져 있습니다.

### 1. Mocking vs Real-API 전환 원리
*   **감정 분석 설정 (`apiConfig.ts`)**:
    *   브라우저의 `localStorage.getItem('use_mock')` 값에 따라 Mocking 여부를 판단합니다. 기본값(값이 존재하지 않거나 `true`인 경우)은 Mocking 활성 모드입니다.
    *   백엔드 연결 모드로 동작시키려면 개발자 콘솔 등에서 아래 스크립트를 입력한 뒤 새로고침합니다.
        ```javascript
        localStorage.setItem('use_mock', 'false');
        ```
*   **[authApi.ts](file:///Users/kil07201/Desktop/my-memory/my-memory-frontend/src/api/authApi.ts) 분기 흐름**:
    *   `isMock()`이 참일 때: `localStorage`의 `'mock_users'` 배열에 가입 데이터를 저장하고, 로그인 시 해당 데이터를 기반으로 가짜 JWT를 리턴합니다.
    *   `isMock()`이 거짓일 때: 백엔드 `/api/auth/login`, `/api/auth/register` 서버 주소로 실제 HTTP POST 요청을 보냅니다.
*   **[diaryApi.ts](file:///Users/kil07201/Desktop/my-memory/my-memory-frontend/src/api/diaryApi.ts) 분기 흐름**:
    *   `isMock()`이 참일 때: 일기 등록, 수정, 삭제 시 `localStorage`의 `'mock_diaries'` 데이터를 직접 배열 연산으로 가공합니다. 이미지 파일 등록 시, 브라우저의 `FileReader` API를 이용해 파일을 **Base64** 데이터 주소(`data:image/...;base64,...`)로 인코딩하여 로컬 브라우저 내에 영구 보존합니다.
    *   `isMock()`이 거짓일 때: HTTP 헤더에 Bearer 토큰을 실어 백엔드의 `/api/diary` REST 엔드포인트와 실제 통신을 수행합니다.

### 2. UI 컴포넌트 및 상태 흐름
*   **메인 상태 관리 (`App.tsx` & `MainPage.tsx`)**:
    *   현재 로그인된 유저의 토큰 및 프로필 상태(`user`), 작성 모달 열림 여부 등을 React State로 유지합니다.
    *   가입/로그인 검증이 완료되면 `MainPage`로 라우팅되어 개인 일기 데이터 목록을 자동으로 로드합니다.
*   **상세 페이지 및 일기 카드 (`DiaryCard.tsx` & `DiaryDetailModal.tsx`)**:
    *   `diary.ts`에 기재된 감정(Emotion) 상수군에 따라 HSL 테마 디자인 컬러(Rose, Sky, Emerald 등)가 카드 테두리 및 배경에 동적으로 배정됩니다.
    *   카드 클릭 시 `DiaryDetailModal`이 팝업되며 본문 내용 및 등록된 첨부 이미지를 고해상도로 로드합니다.

---

## ⚙️ 백엔드 구조 및 비즈니스 로직 설명

백엔드는 RESTful API 명세에 따른 계층형(Layered) 아키텍처로 설계되어 있습니다.

### 1. 사용자 인증 및 가입 로직
*   **회원가입 (`POST /api/auth/register`)**
    *   **흐름**: `AuthApiController` ➔ `AuthService` ➔ `UserRepository`
    *   **로직**:
        1. `userRepository.existsByEmail()` 호출로 이메일 중복 검사를 수행합니다.
        2. 중복이 아닌 것이 검증되면 `BCryptPasswordEncoder.encode()`를 활용하여 평문 비밀번호를 해시 암호화합니다.
        3. 암호화된 비밀번호와 기본 권한(`UserRole.USER`)을 매핑하여 `UserEntity`를 생성하고 DB에 영구 보존합니다.
*   **로그인 (`POST /api/auth/login`)**
    *   **흐름**: `AuthApiController` ➔ `AuthService` ➔ `UserRepository`
    *   **로직**:
        1. `userRepository.findByEmail()`을 호출해 계정 유무를 조회합니다.
        2. 유저가 존재하면 `passwordEncoder.matches()`로 입력된 패스워드 해시 일치 여부를 파악합니다.
        3. 검증 통과 시 `JwtUtil.createToken()`을 기동해 JWT Access Token을 발급하여 DTO 응답 객체에 담아 응답합니다.
*   **보안 필터 및 인가 흐름**
    *   **토큰 가로채기 필터 (`JwtFilter.java`)**: `OncePerRequestFilter`를 상속하여 요청마다 `Authorization` 헤더에서 Bearer 토큰을 가로챈 후 검증합니다. 통과 시 세션 인가 객체를 등록합니다.
    *   **시큐리티 규칙 (`SecurityConfig.java`)**: `/api/auth/**`, `/uploads/**`를 제외한 모든 도메인 API 경로에 대해 인가된 사용자만 접근할 수 있도록 차단합니다.

### 2. 일기(Diary) CRUD 비즈니스 로직
*   **일기 등록 및 파일 저장 (`POST /api/diary`)**
    *   **흐름**: `DiaryApiController` ➔ `DiaryService` ➔ `FileService` ➔ `DiaryRepository`
    *   **로직**:
        1. 이미지 파일이 함께 전달된 경우 `FileService.saveFile(MultipartFile)`을 구동해 고유한 UUID 기반 파일명을 생성한 후 `uploads/` 디렉토리에 물리 파일을 적재하고, 그 주소를 이미지 URL 경로로 설정합니다.
        2. 인가 정보(`email`)를 소유주의 식별값인 `userId`로 활용하여 `DiaryEntity`에 저장합니다.
*   **수정 및 소유권 검증 (`PUT /api/diary/{id}`)**
    *   **로직**:
        1. 수정 대상 일기의 기존 데이터와 요청 유저를 매핑하여 `userId`가 일치하는지 소유권 점검을 실시합니다. 권한이 없는 경우 예외를 즉각 발생시킵니다.
        2. 수정 파일로 새 이미지가 넘어온 경우 기존 파일을 교체하여 저장하고, 없을 경우 기존 URL 정보를 보존합니다.

### 3. 일기 백업 및 정합성 보장형 복원 로직
*   **내보내기 / 다운로드 (`GET /api/backup`)**
    *   **흐름**: `BackupApiController` ➔ `BackupService` ➔ `DiaryRepository`
    *   **로직**:
        1. 임시 작업 폴더(`Files.createTempDirectory`)를 디스크 내에 개설합니다.
        2. 전체 일기 데이터를 파싱하여 `backup.json` 임시 파일로 변환 저장합니다.
        3. 로컬 디스크 내의 `uploads/` 폴더 내에 실재하는 이미지 파일들을 `images.zip` 내부 아카이브 압축 파일로 패킹합니다.
        4. 이 두 임시 결과 파일(`backup.json` + `images.zip`)을 모아서 하나의 단일 ZIP 파일(`diary_backup.zip`)로 병합 압축한 뒤 사용자의 브라우저 다운로드 스트림으로 전송합니다.
        5. 전송이 완료된 후 임시 디렉토리는 흔적 없이 물리 삭제합니다.
*   **들여오기 / 복원 및 자동 롤백 (`POST /api/restore`)**
    *   **흐름**: `BackupApiController` ➔ `BackupService`
    *   **로직**:
        1. **안전 장치 마련**: 에러 발생을 감안하여 기존 DB의 데이터 목록과 로컬 `uploads/` 디렉토리 원본 파일들을 임시 백업 메모리 및 임시 디렉토리로 전부 전송 보관(대피)합니다.
        2. **적재 및 갱신**: 기존 컬렉션을 포맷(`diaryRepository.deleteAll()`)한 후 복원 ZIP에서 압축 해제된 데이터 및 이미지 파일들을 복사해 채워넣습니다.
        3. **무결성 롤백 구현**: 만약 데이터 삽입 또는 이미지 파일 쓰기 도중 에러가 확인되면 롤백 핸들러가 격발되어 새로 갱신된 데이터를 전부 포맷한 뒤 대피 폴더에 보존 중이던 백업 본원 정보로 복구(Rollback)를 완료해 시스템의 일관성을 항상 보장합니다.

---

## 🚀 기동 및 테스트 방법

### 1. 백엔드 실행
백엔드 루트 디렉토리(`my-memory-backend`)에서 실행:
```bash
./gradlew bootRun
```
*백엔드 서버는 `http://localhost:8089` 포트에서 실행됩니다.*

### 2. 프론트엔드 실행
프론트엔드 루트 디렉토리(`my-memory-frontend`)에서 패키지 다운로드 및 개발 모드 기동:
```bash
npm install
npm run dev
```
*프론트엔드 개발 서버는 `http://localhost:5173` 포트에서 실행되며, 백엔드(`/api`, `/uploads`) 요청은 Vite 프록시 설정을 통해 백엔드 포트(`8089`)로 자동 유도됩니다.*

