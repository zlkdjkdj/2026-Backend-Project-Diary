import type { Diary } from '../types/diary';

const API_BASE = import.meta.env.VITE_API_URL ? `${import.meta.env.VITE_API_URL}/api/diary` : '/api/diary';

// 일반 JSON REST API 요청 표준 HTTP 헤더 생성 (인증 토큰 주입)
// return: 설정 완료 Headers 매핑 딕셔너리
const getHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
};

// 멀티파트 폼 데이터 HTTP 전송 헤더 생성 (인증 토큰만 첨부)
// return: 인증 토큰 포함 Headers 매핑 딕셔너리
const getMultipartHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
};

// 일기 도메인(Diary) CRUD 및 백업/복원 기능 수행 API 통신 모듈
// HTTP 요청 추상화 및 멀티파트 데이터 처리
export const diaryApi = {
  // 인증된 사용자 소속 전체 일기 레코드 서버 비동기 조회
  // return: 일기 객체 배열 반환 Promise
  getAll: async (): Promise<Diary[]> => {
    const response = await fetch(API_BASE, { headers: getHeaders() });
    if (!response.ok) {
      throw new Error('일기 목록을 불러오는 데 실패했습니다.');
    }
    return response.json();
  },

  // 키워드 기반 일기 검색
  search: async (keyword: string): Promise<Diary[]> => {
    const response = await fetch(`${API_BASE}/search?keyword=${encodeURIComponent(keyword)}`, { headers: getHeaders() });
    if (!response.ok) {
      throw new Error('일기 검색에 실패했습니다.');
    }
    return response.json();
  },

  // 신규 일기 생성
  create: async (diary: Diary, imageFile?: File | null): Promise<Diary> => {
    const formData = new FormData();
    formData.append('diary', new Blob([JSON.stringify(diary)], { type: 'application/json' }));
    if (imageFile) {
      formData.append('image', imageFile);
    }

    const response = await fetch(API_BASE, {
      method: 'POST',
      headers: getMultipartHeaders(),
      body: formData,
    });
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || '일기 저장에 실패했습니다.');
    }
    return response.json();
  },

  // 기존 일기 정보 수정
  update: async (id: string, diary: Diary, imageFile?: File | null): Promise<Diary> => {
    const formData = new FormData();
    formData.append('diary', new Blob([JSON.stringify(diary)], { type: 'application/json' }));
    if (imageFile) {
      formData.append('image', imageFile);
    }

    const response = await fetch(`${API_BASE}/${id}`, {
      method: 'PUT',
      headers: getMultipartHeaders(),
      body: formData,
    });
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || '일기 수정에 실패했습니다.');
    }
    return response.json();
  },

  // 지정 일기 삭제
  delete: async (id: string): Promise<void> => {
    const response = await fetch(`${API_BASE}/${id}`, {
      method: 'DELETE',
      headers: getHeaders(),
    });
    if (!response.ok) {
      throw new Error('일기 삭제에 실패했습니다.');
    }
  }
};
