import type { LoginRequest, RegisterRequest, AuthResponse } from '../types/diary';

const API_BASE = import.meta.env.VITE_API_URL ? `${import.meta.env.VITE_API_URL}/api/auth` : '/api/auth';

// 서버/Mock 환경 통신 인증 및 계정 관리 데이터 접근 계층(Data Access Layer)
// RESTful 엔드포인트 회원가입 및 로그인 처리 캡슐화
export const authApi = {
  // 클라이언트 회원가입 요청 서버 전송
  // param: 신규 사용자 자격 증명/프로필 정보
  // return: 서버 반환 성공 메시지 문자열
  register: async (request: RegisterRequest): Promise<string> => {
    const response = await fetch(`${API_BASE}/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    });
    
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || '회원가입에 실패했습니다.');
    }
    
    return response.text();
  },

  // 클라이언트 로그인 요청 검증 및 인증 토큰 발급
  // param: 사용자가 입력한 로그인 자격 증명(이메일, 비밀번호)
  // return: JWT 엑세스 토큰 및 기본 사용자 정보 포함 응답 객체
  login: async (request: LoginRequest): Promise<AuthResponse> => {
    const response = await fetch(`${API_BASE}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    });
    
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || '로그인에 실패했습니다.');
    }
    
    return response.json();
  }
};
