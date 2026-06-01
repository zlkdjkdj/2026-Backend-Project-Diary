import type { Diary } from '../types/diary';
import { isMock, getCurrentUserId } from './apiConfig';

const API_BASE = '/api/diary';

// 초기 다이어리 데이터 설정
const getMockDiaries = (): Diary[] => {
  const diaries = localStorage.getItem('mock_diaries');
  if (!diaries) {
    const defaultDiaries: Diary[] = [
      {
        id: 'mock-diary-1',
        userId: 'test@test.com',
        title: '오늘의 일기: 평온한 카페 투어',
        content: '동네에 새로 생긴 예쁜 북카페에 다녀왔다. 조용한 음악이 흐르고 커피 맛도 훌륭해서 앞으로 자주 방문하게 될 것 같다. 밀린 독서도 조금 하고 머리를 식힐 수 있었던 소중한 시간이었다.',
        emotion: 'Calm',
        createdAt: new Date().toISOString().split('T')[0],
      },
      {
        id: 'mock-diary-2',
        userId: 'test@test.com',
        title: '신나는 코딩 공부!',
        content: '백엔드 없이 프론트엔드를 완벽하게 모사하는 Mock API를 구현했다. 생각보다 자연스럽게 잘 작동해서 신기하고 재미있었다. 앞으로 시연할 때 정말 큰 도움이 될 것 같다!',
        emotion: 'Excited',
        createdAt: new Date().toISOString().split('T')[0],
      }
    ];
    localStorage.setItem('mock_diaries', JSON.stringify(defaultDiaries));
    return defaultDiaries;
  }
  return JSON.parse(diaries);
};

// 파일 데이터를 Base64 스트링으로 변환하는 헬퍼 함수
const fileToBase64 = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = (error) => reject(error);
  });
};

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
    if (isMock()) {
      await new Promise(resolve => setTimeout(resolve, 300));
      const userId = getCurrentUserId();
      const diaries = getMockDiaries();
      return diaries.filter(d => d.userId === userId);
    }

    const response = await fetch(API_BASE, { headers: getHeaders() });
    if (!response.ok) {
      throw new Error('일기 목록을 불러오는 데 실패했습니다.');
    }
    return response.json();
  },

  // Search diaries
  search: async (keyword: string): Promise<Diary[]> => {
    if (isMock()) {
      await new Promise(resolve => setTimeout(resolve, 300));
      const userId = getCurrentUserId();
      const diaries = getMockDiaries();
      return diaries.filter(d => 
        d.userId === userId && 
        (d.title.includes(keyword) || d.content.includes(keyword))
      );
    }

    const response = await fetch(`${API_BASE}/search?keyword=${encodeURIComponent(keyword)}`, { headers: getHeaders() });
    if (!response.ok) {
      throw new Error('일기 검색에 실패했습니다.');
    }
    return response.json();
  },

  // Create a new diary
  create: async (diary: Diary, imageFile?: File | null): Promise<Diary> => {
    if (isMock()) {
      await new Promise(resolve => setTimeout(resolve, 600));
      const userId = getCurrentUserId();
      const diaries = getMockDiaries();
      
      let imageUrl = '';
      if (imageFile) {
        try {
          imageUrl = await fileToBase64(imageFile);
        } catch (e) {
          console.error('Image encoding failed:', e);
        }
      }
      
      const newDiary: Diary = {
        ...diary,
        id: `mock-diary-${Date.now()}`,
        userId,
        imageUrl,
        createdAt: diary.createdAt || new Date().toISOString().split('T')[0]
      };
      
      diaries.unshift(newDiary);
      localStorage.setItem('mock_diaries', JSON.stringify(diaries));
      return newDiary;
    }

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
    if (isMock()) {
      await new Promise(resolve => setTimeout(resolve, 600));
      const diaries = getMockDiaries();
      const index = diaries.findIndex(d => d.id === id);
      
      if (index === -1) {
        throw new Error('해당 일기를 찾을 수 없습니다.');
      }
      
      let imageUrl = diaries[index].imageUrl;
      if (imageFile) {
        try {
          imageUrl = await fileToBase64(imageFile);
        } catch (e) {
          console.error('Image encoding failed:', e);
        }
      } else if (imageFile === null) {
        imageUrl = '';
      }
      
      const updatedDiary: Diary = {
        ...diaries[index],
        ...diary,
        imageUrl,
      };
      
      diaries[index] = updatedDiary;
      localStorage.setItem('mock_diaries', JSON.stringify(diaries));
      return updatedDiary;
    }

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
    if (isMock()) {
      await new Promise(resolve => setTimeout(resolve, 400));
      const diaries = getMockDiaries();
      const filtered = diaries.filter(d => d.id !== id);
      localStorage.setItem('mock_diaries', JSON.stringify(filtered));
      return;
    }

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
    if (isMock()) {
      await new Promise(resolve => setTimeout(resolve, 500));
      const diaries = getMockDiaries();
      const userId = getCurrentUserId();
      const userDiaries = diaries.filter(d => d.userId === userId);
      const dataStr = JSON.stringify(userDiaries, null, 2);
      return new Blob([dataStr], { type: 'application/json' });
    }

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
    if (isMock()) {
      await new Promise(resolve => setTimeout(resolve, 800));
      return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsText(file);
        reader.onload = () => {
          try {
            const restoredDiaries = JSON.parse(reader.result as string);
            if (!Array.isArray(restoredDiaries)) {
              throw new Error('유효하지 않은 백업 형식입니다.');
            }
            
            const diaries = getMockDiaries();
            const userId = getCurrentUserId();
            const otherUsersDiaries = diaries.filter(d => d.userId !== userId);
            
            const processedRestored = restoredDiaries.map((d: any) => ({
              ...d,
              userId: userId,
              id: d.id || `mock-diary-${Math.random().toString(36).substr(2, 9)}`,
            }));
            
            const merged = [...processedRestored, ...otherUsersDiaries];
            localStorage.setItem('mock_diaries', JSON.stringify(merged));
            resolve('백업 파일이 성공적으로 복원되었습니다.');
          } catch (e) {
            reject(new Error('백업 파일 파싱에 실패했습니다. 올바른 백업 파일인지 확인해주세요.'));
          }
        };
        reader.onerror = () => reject(new Error('파일을 읽는 도중 오류가 발생했습니다.'));
      });
    }

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


