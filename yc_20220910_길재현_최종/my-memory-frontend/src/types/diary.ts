// 다이어리 타입

// 일기 타입
export interface Diary {
  diaryId?: string;
  authorEmail: string;
  diaryTitle: string;
  diaryContent: string;
  attachedPhotoUrl?: string;
  writtenDate?: string; // 형식을 ISO 날짜 문자열(YYYY-MM-DD)로 제한합니다.
}

// 로그인 요청
export interface LoginRequest {
  userEmail: string;
  rawPassword?: string;
}

// 가입 요청
export interface RegisterRequest {
  userEmail: string;
  rawPassword?: string;
  userNickname: string;
}

// 인증 응답
export interface AuthResponse {
  accessToken: string;
  refreshToken?: string;
  userEmail: string;
  userNickname: string;
}
