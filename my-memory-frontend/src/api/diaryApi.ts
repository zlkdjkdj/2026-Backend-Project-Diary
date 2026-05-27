import type { Diary } from '../types/diary';

const API_BASE = '/api/diary';

export const diaryApi = {
  // Fetch all diaries
  getAll: async (): Promise<Diary[]> => {
    const response = await fetch(API_BASE);
    if (!response.ok) {
      throw new Error('일기 목록을 불러오는 데 실패했습니다.');
    }
    return response.json();
  },

  // Search diaries
  search: async (userId: string, keyword: string): Promise<Diary[]> => {
    const response = await fetch(`${API_BASE}/search?userId=${encodeURIComponent(userId)}&keyword=${encodeURIComponent(keyword)}`);
    if (!response.ok) {
      throw new Error('일기 검색에 실패했습니다.');
    }
    return response.json();
  },

  // Create a new diary
  create: async (diary: Diary): Promise<Diary> => {
    const response = await fetch(API_BASE, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(diary),
    });
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || '일기 저장에 실패했습니다.');
    }
    return response.json();
  },

  // Update an existing diary
  update: async (id: string, diary: Diary): Promise<Diary> => {
    const response = await fetch(`${API_BASE}/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(diary),
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
    });
    if (!response.ok) {
      throw new Error('일기 삭제에 실패했습니다.');
    }
  }
};
