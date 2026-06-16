import type { Diary } from '../types/diary';

const API_BASE = import.meta.env.VITE_API_URL ? `${import.meta.env.VITE_API_URL}/api/diary` : '/api/diary';

// 기본 헤더 생성
const getHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
};

// 멀티파트 헤더
const getMultipartHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
};

// 일기 API
export const diaryApi = {
  // 전체 조회
  getAll: async (): Promise<Diary[]> => {
    const response = await fetch(API_BASE, { headers: getHeaders() });
    if (!response.ok) {
      throw new Error('일기 목록을 불러오는 데 실패했습니다.');
    }
    return response.json();
  },

  // 검색 요청
  search: async (keyword: string): Promise<Diary[]> => {
    const response = await fetch(`${API_BASE}/search?keyword=${encodeURIComponent(keyword)}`, { headers: getHeaders() });
    if (!response.ok) {
      throw new Error('일기 검색에 실패했습니다.');
    }
    return response.json();
  },

  // 생성 요청
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

  // 수정 요청
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

  // 삭제 요청
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
