import type { Diary } from '../types/diary';

const API_BASE = '/api/diary';

const getHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
};

const getMultipartHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
};

export const diaryApi = {
  // Fetch all diaries
  getAll: async (): Promise<Diary[]> => {
    const response = await fetch(API_BASE, { headers: getHeaders() });
    if (!response.ok) {
      throw new Error('일기 목록을 불러오는 데 실패했습니다.');
    }
    return response.json();
  },

  // Search diaries
  search: async (keyword: string): Promise<Diary[]> => {
    const response = await fetch(`${API_BASE}/search?keyword=${encodeURIComponent(keyword)}`, { headers: getHeaders() });
    if (!response.ok) {
      throw new Error('일기 검색에 실패했습니다.');
    }
    return response.json();
  },

  // Create a new diary
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

  // Update an existing diary
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

  // Delete a diary
  delete: async (id: string): Promise<void> => {
    const response = await fetch(`${API_BASE}/${id}`, {
      method: 'DELETE',
      headers: getHeaders(),
    });
    if (!response.ok) {
      throw new Error('일기 삭제에 실패했습니다.');
    }
  },

  // Backup all data and images
  backup: async (): Promise<Blob> => {
    const response = await fetch('/api/backup', {
      headers: getMultipartHeaders(),
    });
    if (!response.ok) {
      throw new Error('백업 데이터 다운로드에 실패했습니다.');
    }
    return response.blob();
  },

  // Restore data and images
  restore: async (file: File): Promise<string> => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await fetch('/api/restore', {
      method: 'POST',
      headers: getMultipartHeaders(),
      body: formData,
    });
    const message = await response.text();
    if (!response.ok) {
      throw new Error(message || '백업 복원에 실패했습니다.');
    }
    return message;
  }
};
