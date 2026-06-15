// API 통신 글로벌 환경 설정 및 공통 유틸리티 캡슐화 모듈
// 개발/운영 환경 분리 및 자원 URL 표준화

// 로컬 스토리지 캐싱 세션 데이터 기반 현재 인증 사용자 이메일 추출
// return: 현재 사용자 이메일 문자열 (비인증 시 'anonymous')
export const getCurrentUserId = (): string => {
  const user = localStorage.getItem('currentUser');
  return user ? JSON.parse(user).email : 'anonymous';
};

// 데이터베이스 이미지 상대 경로를 백엔드 서버 절대 URL 체계로 변환
// 외부 URL/Base64 데이터는 변환 없이 원본 반환
// param: 변환 대상 원본 URL/경로
// return: 클라이언트 접근 가능 완전한 형태 URL 문자열
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
