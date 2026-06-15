// 현재 인증된 사용자 이메일 조회
export const getCurrentUserId = (): string => {
  const user = localStorage.getItem('currentUser');
  return user ? JSON.parse(user).email : 'anonymous';
};

// 이미지 URL 절대 경로 변환
export const getImageUrl = (url?: string): string => {
  if (!url) return '';
  if (url.startsWith('data:') || url.startsWith('blob:') || url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  const apiBase = import.meta.env.VITE_API_URL || '';
  const cleanBase = apiBase.endsWith('/') ? apiBase.slice(0, -1) : apiBase;
  const cleanUrl = url.startsWith('/') ? url : `/${url}`;
  return `${cleanBase}${cleanUrl}`;
};
