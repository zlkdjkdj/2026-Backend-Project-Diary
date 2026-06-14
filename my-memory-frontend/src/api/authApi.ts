import type { LoginRequest, RegisterRequest, AuthResponse } from '../types/diary';
import { isMock } from './apiConfig';

const API_BASE = import.meta.env.VITE_API_URL ? `${import.meta.env.VITE_API_URL}/api/auth` : '/api/auth';

// 로컬 스토리지 Mock 사용자 데이터 배열 파싱 (초기 구동 시 기본 계정 프로비저닝)
// return: 프로비저닝된 사용자 자격 증명 배열
const getMockUsers = (): Array<RegisterRequest> => {
  const users = localStorage.getItem('mock_users');
  if (!users) {
    const defaultUsers = [
      {
        userEmail: 'test@test.com',
        rawPassword: 'password123',
        userNickname: '테스터',
      }
    ];
    localStorage.setItem('mock_users', JSON.stringify(defaultUsers));
    return defaultUsers;
  }
  return JSON.parse(users);
};

// 서버/Mock 환경 통신 인증 및 계정 관리 데이터 접근 계층(Data Access Layer)
// RESTful 엔드포인트 회원가입 및 로그인 처리 캡슐화
export const authApi = {
  // 클라이언트 회원가입 요청 서버 전송
  // param: 신규 사용자 자격 증명/프로필 정보
  // return: 서버 반환 성공 메시지 문자열
  register: async (request: RegisterRequest): Promise<string> => {
    if (isMock()) {
      await new Promise(resolve => setTimeout(resolve, 500));
      const users = getMockUsers();
      if (users.some(u => u.userEmail === request.userEmail)) {
        throw new Error('이미 가입된 이메일입니다.');
      }
      users.push({
        userEmail: request.userEmail,
        rawPassword: request.rawPassword,
        userNickname: request.userNickname,
      });
      localStorage.setItem('mock_users', JSON.stringify(users));
      return '회원가입이 완료되었습니다!';
    }

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
    if (isMock()) {
      await new Promise(resolve => setTimeout(resolve, 500));
      const users = getMockUsers();
      const user = users.find(u => u.userEmail === request.userEmail && u.rawPassword === request.rawPassword);
      if (!user) {
        throw new Error('이메일 혹은 비밀번호를 확인해주세요.');
      }
      return {
        accessToken: `mock-jwt-token-${Date.now()}`,
        userEmail: user.userEmail,
        userNickname: user.userNickname,
      };
    }

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
