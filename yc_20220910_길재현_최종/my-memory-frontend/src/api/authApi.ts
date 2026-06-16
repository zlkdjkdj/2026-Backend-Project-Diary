import type { LoginRequest, RegisterRequest, AuthResponse } from '../types/diary';

const API_BASE = import.meta.env.VITE_API_URL ? `${import.meta.env.VITE_API_URL}/api/auth` : '/api/auth';

// 인증 API
export const authApi = {
  // 회원가입 요청
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

  // 로그인 요청
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
