# My Memory - 일기 및 메모 보관 서비스

이 프로젝트는 React(프론트엔드)와 Spring Boot 및 MongoDB(백엔드)를 활용하여 구현한 개인 일기장 애플리케이션입니다. 사용자 인증(JWT), AWS S3 기반 이미지 업로드, 날짜 및 키워드 기반 일기 검색/필터링 기능을 제공합니다.

---

## 프로젝트 전체 디렉토리 구조

```text
my-memory/
├── database_schema.md             # MongoDB 데이터베이스 정의 문서 (스키마 및 ERD 명세)
├── DB구조 제출본.pdf                 # MongoDB 데이터베이스 관계 정의 PDF 보고서
├── my-memory-frontend/            # 프론트엔드 (React + TypeScript + Vite)
│   ├── src/
│   │   ├── api/                   # API 통신 계층
│   │   │   ├── apiConfig.ts       # Axios 공통 인스턴스 및 Base URL 설정
│   │   │   ├── authApi.ts         # 로그인/회원가입 API 통신 모듈
│   │   │   └── diaryApi.ts        # 일기 생성/수정/삭제/검색 API 통신 모듈
│   │   │
│   │   ├── components/            # UI 컴포넌트 레이어
│   │   │   ├── DiaryDetailModal.tsx # 일기 세부 본문 및 이미지 뷰어 모달
│   │   │   ├── DiaryForm.tsx      # 일기 작성 및 수정 입력 폼 컴포넌트
│   │   │   ├── DiaryItem.tsx      # 일기 목록 카드 아이템 컴포넌트
│   │   │   ├── DiaryList.tsx      # 일기 목록 컨테이너 컴포넌트
│   │   │   ├── Header.tsx         # 글로벌 상단 내비게이션 헤더 컴포넌트
│   │   │   └── SearchFilter.tsx   # 키워드, 날짜, 이미지 필터링 검색 바 컴포넌트
│   │   │
│   │   ├── pages/                 # 라우터 페이지 컴포넌트
│   │   │   ├── LoginPage.tsx      # 로그인 화면
│   │   │   ├── RegisterPage.tsx   # 회원가입 화면
│   │   │   └── MainPage.tsx       # 일기 관리 메인 대시보드 화면 (목록, 필터, CRUD 이벤트 연동)
│   │   │
│   │   ├── types/                 # 공통 TypeScript 타입 정의
│   │   │   └── diary.ts           # Diary 도메인 데이터 모델 인터페이스 명세
│   │   │
│   │   ├── App.tsx                # 라우터 및 글로벌 상태 관리 컴포넌트
│   │   ├── index.css              # 전역 CSS 및 디자인 시스템 설정 (CSS 변수)
│   │   └── main.tsx               # React 애플리케이션 엔트리 포인트
│   └── vite.config.ts             # Vite 설정 파일
│
└── my-memory-backend/             # 백엔드 (Spring Boot + MongoDB Atlas)
    ├── Dockerfile                 # 도커 컨테이너 배포 설정 파일
    ├── build.gradle               # Gradle 의존성 및 프로젝트 빌드 구성
    ├── settings.gradle            # Gradle 멀티프로젝트 관리 설정
    ├── gradlew / gradlew.bat      # Gradle 래퍼 실행 스크립트
    ├── src/
    │   ├── main/
    │   │   ├── java/com/jaehyun/diary/
    │   │   │   ├── MyMemoryBackendApplication.java # Spring Boot 메인 어플리케이션 진입점 (.env 동적 로더 포함)
    │   │   │   ├── api/                   # REST API 컨트롤러 계층
    │   │   │   │   ├── AuthApiController.java     # 회원가입 및 로그인 처리 API
    │   │   │   │   └── DiaryApiController.java    # 일기 생성/조회/수정/삭제/검색 처리 API
    │   │   │   │
    │   │   │   ├── dto/                   # 데이터 전송 객체 (DTO) 계층
    │   │   │   │   ├── DiaryForm.java             # 일기 입출력 데이터 전달 DTO
    │   │   │   │   └── AuthForm.java              # 로그인/회원가입 요청 및 응답 DTO
    │   │   │   │
    │   │   │   ├── entity/                # MongoDB 영속성 엔티티 계층
    │   │   │   │   ├── DiaryEntity.java           # 일기 도메인 DB 매핑 객체
    │   │   │   │   ├── UserEntity.java            # 사용자 정보 DB 매핑 객체
    │   │   │   │   └── UserRole.java              # 인가 역할 정의 (USER, ADMIN)
    │   │   │   │
    │   │   │   ├── repository/            # 데이터 액세스 인터페이스 계층
    │   │   │   │   ├── DiaryRepository.java       # 일기 컬렉션 DB 쿼리 인터페이스
    │   │   │   │   └── UserRepository.java        # 회원 정보 컬렉션 DB 쿼리 인터페이스
    │   │   │   │
    │   │   │   ├── service/               # 핵심 비즈니스 로직 계층
    │   │   │   │   ├── DiaryService.java          # 일기 등록/수정/삭제 비즈니스 로직
    │   │   │   │   ├── AuthService.java           # 사용자 인증, 비밀번호 암호화 및 토큰 처리
    │   │   │   │   └── FileService.java           # AWS S3 파일 업로드 및 링크 생성 비즈니스 로직
    │   │   │   │
    │   │   │   └── config/                # 글로벌 인프라 및 보안 설정
    │   │   │       ├── MongoConfig.java           # MongoDB 연결 설정
    │   │   │       ├── S3Config.java              # AWS S3 클라이언트 빈(Bean) 등록 설정
    │   │   │       ├── SecurityConfig.java        # Spring Security 및 CORS 인가/예외 처리 설정
    │   │   │       ├── JwtFilter.java             # JWT 유효성 검증 필터
    │   │   │       ├── JwtUtil.java               # JWT 빌더 및 복호화 유틸리티
    │   │   │       ├── GlobalExceptionHandler.java # REST API 공통 예외 처리 컨트롤러 어드바이스
    │   │   │       ├── CheckOwnership.java        # 소유권 검증용 AOP 어노테이션 정의
    │   │   │       └── OwnershipAspect.java       # 소유권 검증 Aspect 구현체 (AOP)
    │   │   │
    │   │   └── resources/             # 정적 리소스 및 어플리케이션 환경 정보
    │   │       └── application.yml                 # DB 정보 및 포트 설정
    │   │
    │   ├── test/                      # 통합/단위 테스트 디렉토리
    │   │   └── java/com/jaehyun/diary/
    │   │       ├── S3ConnectionTest.java           # S3 업로드, Mongo 쿼리 및 JWT 인증 API 통합 검증 테스트
    │   │       └── service/
    │   │           └── DiaryServiceTest.java       # 일기 관리 서비스 유닛 테스트
    │   │
    │   └── test.http                  # HTTP API 수동 검증용 테스트 스크립트
```

---

## 프론트엔드 구조 및 비즈니스 로직 설명

프론트엔드는 모던한 UI와 반응형 다크 모드, 그리고 편리한 일기 필터링 시스템을 갖추고 있습니다.

### 1. API 통신 및 토큰 저장
- **인증 토큰 보존**: 사용자가 로그인에 성공하면 발급된 JWT 토큰은 `localStorage`의 `token` 키에 저장됩니다.
- **인증 인터셉트**: [diaryApi.ts](file:///Users/kil07201/Desktop/my-memory/my-memory-frontend/src/api/diaryApi.ts) 모듈에서 서버에 API 요청을 보낼 때, 헤더에 자동으로 `Authorization: Bearer <Token>` 포맷으로 주입하여 요청합니다.

### 2. 주요 UI 컴포넌트
- **일기 리스트 & 상세 모달**: 
  - [DiaryItem.tsx](file:///Users/kil07201/Desktop/my-memory/my-memory-frontend/src/components/DiaryItem.tsx)를 통해 일기를 반응형 카드로 렌더링하며, 클릭하면 상세 뷰를 보여주는 [DiaryDetailModal.tsx](file:///Users/kil07201/Desktop/my-memory/my-memory-frontend/src/components/DiaryDetailModal.tsx)가 활성화됩니다.
- **일기 필터 및 날짜 검색**:
  - [SearchFilter.tsx](file:///Users/kil07201/Desktop/my-memory/my-memory-frontend/src/components/SearchFilter.tsx) 컴포넌트는 키워드 검색, 날짜 검색(현지 타임존 보정), 이미지 포함 여부 필터링 조건을 제공합니다.
  - [MainPage.tsx](file:///Users/kil07201/Desktop/my-memory/my-memory-frontend/src/pages/MainPage.tsx)에서 사용자가 필터링 조건을 변경하면 실시간으로 클라이언트 사이드에서 검색 결과를 절삭하여 보여줍니다.
- **날짜 타임존 안정성**:
  - 한국(KST) 오전 시간대에 일기를 작성하더라도 UTC 오차로 인해 어제 날짜로 저장되거나 검색되지 않던 현상을 해결하기 위해 브라우저의 현지 날짜를 YYYY-MM-DD로 정확히 파싱해 전송합니다.

---

## 백엔드 구조 및 비즈니스 로직 설명

백엔드는 대용량 파일 저장에 유리한 **AWS S3**와 확장성이 뛰어난 **MongoDB Atlas**를 연동하여 동작합니다.

### 1. 사용자 인증 및 보안
- **회원가입 (`POST /api/auth/register`)**: 이메일 중복 여부를 감지하고, `BCryptPasswordEncoder`로 단방향 비밀번호 해시 처리를 거쳐 저장합니다.
- **로그인 (`POST /api/auth/login`)**: 비밀번호 대조가 성공하면 만료 시간 30분의 JWT 토큰을 발행합니다.
- **인증 보장 (`JwtFilter.java` & `SecurityConfig.java`)**: `SecurityConfig`에 명시된 예외 허용 경로(`/api/auth/**`)를 제외한 모든 자원에 접근할 때 JWT 필터를 반드시 거쳐 사용자를 식별합니다.

### 2. 일기(Diary) CRUD 및 파일 저장 서비스
- **AWS S3 업로드 (`FileService.java`)**:
  - 일기 작성 또는 수정 시 이미지가 첨부되면, Spring Boot가 이를 바이트 어레이 스트림 형태로 주입받아 AWS S3 버킷에 난수화된 키로 직접 전송(Upload)합니다.
  - 업로드가 정상 종료되면 해당 S3 객체의 절대 URL을 생성하여 `attachedPhotoUrl`로 영속화합니다.
- **소유권 검증 (AOP - `@CheckOwnership`)**:
  - 일기 수정/삭제 시 로그인한 사용자가 실제 일기를 작성했는지 여부를 서비스 호출 시 AOP(`OwnershipAspect.java`) 프록시가 가로채어 검증함으로써 강력한 보안 무결성을 보장합니다.

---

## 실행 및 테스트 방법

본 프로젝트는 **Vercel**과 **Render** 서비스를 통해 배포 및 가동 중입니다. 아래의 배포 링크를 통해 테스트를 진행할 수 있습니다.

- **배포 서비스 접속 링크 (Vercel)**: https://2026-backend-project-diary.vercel.app
