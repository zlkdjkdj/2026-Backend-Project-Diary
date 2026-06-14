// 클라이언트-백엔드 간 교환 핵심 도메인 데이터 모델(Data Model) 구조 명세 TypeScript 인터페이스 집합

// 단일 일기 레코드 도메인 인터페이스
// 영속성 엔티티(DiaryEntity) 매핑 및 핵심 비즈니스 데이터 표현
export interface Diary {
  diaryId?: string;
  authorEmail: string;
  diaryTitle: string;
  diaryContent: string;
  attachedPhotoUrl?: string;
  writtenDate?: string; // 형식을 ISO 날짜 문자열(YYYY-MM-DD)로 제한합니다.
}

// 로그인 인증 요청 시 서버 전송 자격 증명 페이로드(Payload) 구조 정의
export interface LoginRequest {
  userEmail: string;
  rawPassword?: string;
}

// 신규 계정 프로비저닝(회원가입) 시 전송 사용자 프로필 명세서
export interface RegisterRequest {
  userEmail: string;
  rawPassword?: string;
  userNickname: string;
}

// 서버 측 인증 절차 통과 후 발급 토큰 및 메타데이터 응답 구조
export interface AuthResponse {
  accessToken: string;
  refreshToken?: string;
  userEmail: string;
  userNickname: string;
}
