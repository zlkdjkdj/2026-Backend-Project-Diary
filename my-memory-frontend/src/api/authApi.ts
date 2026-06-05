import type { LoginRequest, RegisterRequest, AuthResponse } from '../types/diary';
import { isMock } from './apiConfig';

const API_BASE = import.meta.env.VITE_API_URL ? `${import.meta.env.VITE_API_URL}/api/auth` : '/api/auth';

// 초기 Mock 유저 설정
const getMockUsers = (): Array<RegisterRequest & { nickname: string }> => {
  const users = localStorage.getItem('mock_users');
  if (!users) {
    const defaultUsers = [
      {
        email: 'test@test.com',
        password: 'password123',
        nickname: '테스터',
      }
    ];
    localStorage.setItem('mock_users', JSON.stringify(defaultUsers));
    return defaultUsers;
  }
  return JSON.parse(users);
};

export const authApi = {
  register: async (request: RegisterRequest): Promise<string> => {
    if (isMock()) {
      await new Promise(resolve => setTimeout(resolve, 500));
      const users = getMockUsers();
      if (users.some(u => u.email === request.email)) {
        throw new Error('이미 가입된 이메일입니다.');
      }
      users.push({
        email: request.email,
        password: request.password,
        nickname: request.nickname,
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
    
    return response.text(); // 성공 메시지 반환
  },

  login: async (request: LoginRequest): Promise<AuthResponse> => {
    if (isMock()) {
      await new Promise(resolve => setTimeout(resolve, 500));
      const users = getMockUsers();
      const user = users.find(u => u.email === request.email && u.password === request.password);
      if (!user) {
        throw new Error('이메일 혹은 비밀번호를 확인해주세요.');
      }
      return {
        token: `mock-jwt-token-${Date.now()}`,
        email: user.email,
        nickname: user.nickname,
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


