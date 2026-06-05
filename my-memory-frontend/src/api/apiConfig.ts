// Mock 모드 확인 (기본값 true)
export const isMock = (): boolean => import.meta.env.VITE_USE_MOCK_API === 'true';

// 현재 로그인한 사용자 ID (email)
export const getCurrentUserId = (): string => {
  const user = localStorage.getItem('currentUser');
  return user ? JSON.parse(user).email : 'anonymous';
};

// 이미지 상대경로를 백엔드 서버 절대경로로 변환하는 헬퍼 함수
export const getImageUrl = (url?: string): string => {
  if (!url) return '';
  if (url.startsWith('data:') || url.startsWith('blob:') || url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  const apiBase = import.meta.env.VITE_API_URL || '';
  // VITE_API_URL이 존재하고 슬래시가 없는 경우 슬래시 처리
  const cleanBase = apiBase.endsWith('/') ? apiBase.slice(0, -1) : apiBase;
  const cleanUrl = url.startsWith('/') ? url : `/${url}`;
  return `${cleanBase}${cleanUrl}`;
};

