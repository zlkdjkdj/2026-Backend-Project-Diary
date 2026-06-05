// Mock 모드 확인 (기본값 true)
export const isMock = (): boolean => import.meta.env.VITE_USE_MOCK_API === 'true';

// 현재 로그인한 사용자 ID (email)
export const getCurrentUserId = (): string => {
  const user = localStorage.getItem('currentUser');
  return user ? JSON.parse(user).email : 'anonymous';
};
