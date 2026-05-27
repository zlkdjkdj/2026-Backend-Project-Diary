# 📔 My Memory - 일기 및 메모 보관 서비스

이 프로젝트는 **React(프론트엔드)**와 **Spring Boot + MongoDB(백엔드)**를 활용하여 구축된 개인 일기장 애플리케이션입니다. 사용자 인증(JWT), 이미지 업로드, 그리고 안전성 및 데이터 정합성을 완벽하게 보장하는 백업 및 복원 기능을 제공합니다.

전체 백엔드 설계는 가독성과 일관성을 보장하기 위해 필드 기반 의존성 주입(`@Autowired`), 분리된 레이어드 패키지 모델, 그리고 `Entity`/`Form` 명명 규칙을 적용한 **참고폴더 예제 형식**에 맞춰 리팩토링되었습니다.

---

## 📂 프로젝트 전체 디렉토리 구조

```text
my-memory/
├── my-memory-frontend/            # 프론트엔드 (React + TypeScript + Vite)
│   ├── src/
│   │   ├── api/                   # API 호출 인터페이스 (diaryApi.ts, authApi.ts)
│   │   ├── components/            # UI 컴포넌트 (일기 카드, 에디터 폼, 모달 상세창 등)
│   │   ├── types/                 # TypeScript 공통 인터페이스 및 타입 정의
│   │   └── App.tsx                # 전체 상태 관리 및 헤더 UI 바인딩
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

## ⚙️ 백엔드 모든 비즈니스 로직 및 파일 실행 경로 설명

### 1. 사용자 인증 및 가입 로직

* **회원가입 (`POST /api/auth/register`)**
  * **API 컨트롤러**: `api/AuthApiController.java`의 `register(AuthForm.RegisterForm request)` 메서드 실행
  * **비즈니스 로직**: `service/AuthService.java`의 `register(AuthForm.RegisterForm request)` 메서드 실행
    1. `userRepository.existsByEmail()` 호출로 이메일 중복 체크
    2. 중복이 아닐 시 `BCryptPasswordEncoder.encode()`를 사용해 패스워드 암호화
    3. `UserEntity.builder()`를 통해 `UserRole.USER` 권한과 함께 DB 엔티티 생성 후 `userRepository.save()`로 기록

* **로그인 (`POST /api/auth/login`)**
  * **API 컨트롤러**: `api/AuthApiController.java`의 `login(AuthForm.LoginForm request)` 메서드 실행
  * **비즈니스 로직**: `service/AuthService.java`의 `login(AuthForm.LoginForm request)` 메서드 실행
    1. `userRepository.findByEmail()`을 호출해 등록 이메일 점검
    2. `passwordEncoder.matches()`를 사용해 평문 비밀번호와 해시 비밀번호 매칭 확인
    3. 비밀번호 확인 시 `config/JwtUtil.java`의 `createToken(email, role)`을 실행해 JWT 토큰 생성 및 `AuthForm.TokenResponse` 반환

* **인가 통제 및 검증 필터**
  * **토큰 검증 필터**: `config/JwtFilter.java`의 `doFilterInternal()` 실행
    - 들어오는 모든 요청 헤더에서 Bearer 토큰 식별
    - `JwtUtil.java`의 `validateToken(token)` 및 `getEmail(token)`, `getRole(token)`을 직접 활용하여 DB 조회 없이 세션 인가 컨텍스트 생성
  * **보안 라우팅**: `config/SecurityConfig.java`의 `filterChain(HttpSecurity http)`
    - 인증 제외 경로(`/api/auth/**`, `/uploads/**`) 등록 및 이외 모든 요청에 대해 시큐리티 인가 제한 처리

---

### 2. 일기(Diary) CRUD 비즈니스 로직

* **일기 작성 (`POST /api/diary`)**
  * **API 컨트롤러**: `api/DiaryApiController.java`의 `createDiary(...)` 메서드 실행
  * **비즈니스 로직**: `service/DiaryService.java`의 `createDiary(String email, DiaryForm form)` 메서드 실행
    1. **이미지 저장**: 컨트롤러 단에서 파일 업로드 감지 시 `service/FileService.java`의 `saveFile(MultipartFile file)`을 가동하여 중복 없는 UUID 파일명으로 `uploads/` 폴더에 이미지를 저장하고 `DiaryForm.setImageUrl`에 경로 바인딩
    2. 소유권 보존을 위해 `DiaryForm`의 `userId` 값을 로그인한 사용자의 메일로 저장
    3. `DiaryForm.toEntity()` 변환 메서드를 호출해 `DiaryEntity` 객체 빌드
    4. `diaryRepository.save()`를 호출해 최종 레코드 생성 및 DTO 규격 반환

* **일기 목록 전체 조회 (`GET /api/diary`)**
  * **API 컨트롤러**: `api/DiaryApiController.java`의 `getAllDiaries(Principal principal)` 메서드 실행
  * **비즈니스 로직**: `service/DiaryService.java`의 `getAllDiaries(String email)` 메서드 실행
    1. `diaryRepository.findByUserId(email)`을 호출해 본인 소유의 일기 레코드(`DiaryEntity`) 리스트만 필터링
    2. `DiaryForm.fromEntity()`를 통해 DTO 타입 리스트로 맵핑 및 가공 후 프론트엔드로 응답

* **일기 본문 내용 검색 (`GET /api/diary/search`)**
  * **API 컨트롤러**: `api/DiaryApiController.java`의 `searchDiaries(Principal principal, String keyword)` 메서드 실행
  * **비즈니스 로직**: `service/DiaryService.java`의 `searchDiaries(String email, String keyword)` 메서드 실행
    1. `diaryRepository.findByUserIdAndContentContaining(email, keyword)`를 호출하여 지정 작성자의 본문 검색을 실행
    2. 검색 조건에 일치하는 결과물 리스트를 DTO 배열 형식으로 제공

* **일기 본문 수정 (`PUT /api/diary/{id}`)**
  * **API 컨트롤러**: `api/DiaryApiController.java`의 `updateDiary(...)` 메서드 실행
  * **비즈니스 로직**: `service/DiaryService.java`의 `updateDiary(String email, String id, DiaryForm form)` 메서드 실행
    1. **소유권 검증**: `diaryRepository.findById(id)` 호출 및 기존 일기의 `userId`와 현재 로그인 이메일 대조 (비일치 시 권한 부족 예외 유발)
    2. **이미지 교체**: 수정에 업로드된 신규 이미지가 있는 경우 `FileService`를 재호출하여 덮어쓰고, 이미지가 없을 시 기존의 `imageUrl` 속성을 유지
    3. `DiaryForm.toEntity()`를 변환하여 `diaryRepository.save()`로 레코드 수정 저장

* **일기 영구 삭제 (`DELETE /api/diary/{id}`)**
  * **API 컨트롤러**: `api/DiaryApiController.java`의 `deleteDiary(Principal principal, String id)` 메서드 실행
  * **비즈니스 로직**: `service/DiaryService.java`의 `deleteDiary(String email, String id)` 메서드 실행
    1. 대상 일기의 존재 및 작성자 일치 여부를 파악
    2. 정상 권한이 확인된 사용자에 한해 `diaryRepository.deleteById(id)`를 호출하여 데이터 소거

---

### 3. 일기 백업 및 정합성 보장형 복원 로직

* **백업 파일 생성 및 내보내기 (`GET /api/backup`)**
  * **API 컨트롤러**: `api/BackupApiController.java`의 `downloadBackup(HttpServletResponse response)` 메서드 실행
  * **비즈니스 로직**: `service/BackupService.java`의 `createBackup(OutputStream outputStream)` 메서드 실행
    1. `Files.createTempDirectory()`로 임시 작업 폴더 생성
    2. `diaryRepository.findAll()`로 모든 컬렉션 데이터 수집 후 Jackson `objectMapper.writeValue()`를 써서 `backup.json` 임시 파일로 작성
    3. 로컬 디스크의 `uploads/` 폴더를 재귀 순회하여 `images.zip` 내부 스트림 압축 수행
    4. 생성된 `backup.json`과 `images.zip` 두 파일을 하나의 외부 ZIP 아카이브(`diary_backup.zip`)로 스트리밍 압축하여 사용자 Response로 파일 전송 실행
    5. 사용 완료된 임시 공간 폴더는 `deleteDirectoryRecursive()`로 디스크 공간 복원 처리

* **데이터 복원 및 예외 안전성 롤백 (`POST /api/restore`)**
  * **API 컨트롤러**: `api/BackupApiController.java`의 `restoreBackup(MultipartFile file)` 메서드 실행
  * **비즈니스 로직**: `service/BackupService.java`의 `restoreBackup(InputStream zipInputStream)` 메서드 실행
    1. **임시 검증**: 임시 디렉토리를 열어 업로드한 ZIP 아카이브의 압축을 풀고 `backup.json` 유무 및 JSON 포맷 무결성을 먼저 분석 (파싱 실패 시 예외 던짐)
    2. **대피소(Backup) 설정**: DB 및 물리 파일 쓰기 실패를 감안하여, 직전의 데이터베이스 데이터(`diaryRepository.findAll()`)를 메모리 리스트에 적재하고 `uploads/` 내 원본 이미지 파일들을 임시 대피 폴더(`tempUploadsBackupDir`)로 통복사
    3. **트랜잭션 실행**:
       - `diaryRepository.deleteAll()` 호출로 기존 일기를 전면 포맷
       - 아카이브에서 파싱된 일기 리스트 적재(`diaryRepository.saveAll(newDiaries)`)
       - 기존 `uploads/` 디렉토리를 비운 후 압축 해제된 새 이미지 파일 복사 적재
    4. **롤백(Rollback) 작동**:
       - 데이터 쓰기 도중 에러가 `catch`에 감지되면, DB를 다시 전체 포맷하고 백업 메모리에 담아두었던 예전 일기 리스트를 재적재
       - 디렉토리의 파일을 초기화하고 `tempUploadsBackupDir` 대피 폴더에 저장되어 있었던 기존 원본 이미지 파일들을 다시 `uploads/`로 복사 원복
    5. 복원/롤백 여부와 무관하게 `finally` 구문을 통과해 사용되었던 모든 임시 디스크 경로들을 강제 소멸 삭제

---

## 🚀 기동 및 테스트 방법

### 1. 백엔드 실행
백엔드 루트 디렉토리(`my-memory-backend`)에서 터미널을 열고 다음 명령어를 입력합니다:
```bash
./gradlew bootRun
```
*백엔드 서버는 `http://localhost:8089` 포트에서 실행되며, 로컬 저장 이미지들을 서빙하기 시작합니다.*

### 2. 프론트엔드 실행
프론트엔드 루트 디렉토리(`my-memory-frontend`)에서 패키지를 내려받은 후 개발 모드로 실행합니다:
```bash
npm install
npm run dev
```
*프론트엔드 개발 서버는 `http://localhost:5173` 포트에서 가동되며, `/api`와 `/uploads`로 통하는 요청은 Vite 프록시 설정을 따라 백엔드(`8089` 포트)로 우회 공급됩니다.*
