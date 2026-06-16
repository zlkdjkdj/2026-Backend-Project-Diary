import React, { useState, useEffect } from 'react';
import type { Diary } from '../types/diary';
import { diaryApi } from '../api/diaryApi';
import DiaryForm from '../components/DiaryForm';
import DiaryList from '../components/DiaryList';
import DiaryDetailModal from '../components/DiaryDetailModal';
import Header from '../components/Header';
import SearchFilter from '../components/SearchFilter';

// 메인 속성
interface MainPageProps {
  token: string | null;
  currentUser: { email: string; nickname: string } | null;
  onLogout: () => void;
}

// 메인 페이지
const MainPage: React.FC<MainPageProps> = ({
  token,
  currentUser,
  onLogout,
}) => {
  // 데이터 상태
  const [diaries, setDiaries] = useState<Diary[]>([]); // 일기 목록 상태
  const [fetchLoading, setFetchLoading] = useState(false); // 조회 로딩 상태
  const [actionLoading, setActionLoading] = useState(false); // 저장/삭제 진행 상태
  const [error, setError] = useState<string | null>(null); // 에러 메시지 상태

  // 검색 필터 상태
  const [searchKeyword, setSearchKeyword] = useState('');
  const [searchDate, setSearchDate] = useState('');
  const [onlyWithImages, setOnlyWithImages] = useState(false);

  // 일기 입력/수정 폼 상태
  const [isEditing, setIsEditing] = useState(false);
  const [editingDiary, setEditingDiary] = useState<Diary | undefined>(undefined);

  // 상세 보기 모달 상태
  const [viewingDiary, setViewingDiary] = useState<Diary | null>(null);

  // 상단 알림 메시지 상태
  const [alertMessage, setAlertMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  // 알림 토스트 출력
  const showAlert = (text: string, type: 'success' | 'error' = 'success') => {
    setAlertMessage({ type, text });
    setTimeout(() => setAlertMessage(null), 3000);
  };

  // 일기 목록 데이터 조회
  const fetchDiaries = async () => {
    if (!token) return;
    setFetchLoading(true);
    setError(null);
    try {
      const data = await diaryApi.getAll(); // 전체 조회 API 호출
      setDiaries(data);
    } catch (err: any) {
      setError(err.message || '네트워크 오류가 발생했습니다.');
      if (err.message.includes('유효하지 않')) {
        onLogout(); // 세션 유효하지 않은 경우 로그아웃 처리
      }
    } finally {
      setFetchLoading(false);
    }
  };

  // 일기 생성 및 수정 요청 처리
  const handleSaveDiary = async (diary: Diary, imageFile: File | null) => {
    if (!token) return;
    setActionLoading(true);
    try {
      if (isEditing && diary.diaryId) {
        await diaryApi.update(diary.diaryId, diary, imageFile); // 수정 API 호출
        showAlert('일기가 정상적으로 수정되었습니다!');
      } else {
        await diaryApi.create(diary, imageFile); // 생성 API 호출
        showAlert('일기가 정상적으로 저장되었습니다!');
      }
      resetForm(); // 입력 폼 리셋
      fetchDiaries(); // 목록 갱신
    } catch (err: any) {
      showAlert(err.message || '저장 중 오류가 발생했습니다.', 'error');
    } finally {
      setActionLoading(false);
    }
  };

  // 일기 삭제
  const handleDelete = async (id: string) => {
    if (!token) return; //로그인 토큰 존재 여부 확인
    if (!window.confirm('정말로 이 일기를 삭제하시겠습니까?')) { //삭제 확인 모달
      return;
    }
    setActionLoading(true);
    try {
      await diaryApi.delete(id); //삭제 api 호출, 백엔드에 http delete 요청
      showAlert('일기가 성공적으로 삭제되었습니다.');
      fetchDiaries();
      if (isEditing && editingDiary?.diaryId === id) {
        resetForm();
      }
    } catch (err: any) {
      showAlert(err.message || '삭제 오류가 발생했습니다.', 'error');
    } finally {
      setActionLoading(false);
    }
  };

  // 수정 시작
  const startEdit = (diary: Diary) => {
    if (!diary.diaryId) return;
    setIsEditing(true);
    setEditingDiary(diary);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  // 폼 초기화
  const resetForm = () => {
    setIsEditing(false);
    setEditingDiary(undefined);
  };


  // 필터 초기화
  const handleResetFilters = () => {
    setSearchKeyword('');
    setSearchDate('');
    setOnlyWithImages(false);
    fetchDiaries();
  };

  useEffect(() => {
    if (token) {
      fetchDiaries();
    }
  }, [token]);

  // 필터링 적용
  const filteredDiaries = diaries.filter((diary) => {
    if (searchKeyword.trim()) {
      const keyword = searchKeyword.toLowerCase();
      const titleMatch = diary.diaryTitle?.toLowerCase().includes(keyword);
      const contentMatch = diary.diaryContent?.toLowerCase().includes(keyword);
      if (!titleMatch && !contentMatch) return false;
    }

    if (searchDate) {
      if (!diary.writtenDate) return false;
      const diaryDate = diary.writtenDate.split('T')[0];
      if (diaryDate !== searchDate) return false;
    }

    if (onlyWithImages) {
      if (!diary.attachedPhotoUrl) return false;
    }

    return true;
  });

  return (
    <div className="min-h-screen bg-[#fbfbfd] dark:bg-[#0b0b0c] text-[#1d1d1f] dark:text-[#f5f5f7] flex flex-col antialiased">
      <Header
        currentUser={currentUser}
        onLogout={onLogout}
      />

      <main className="flex-grow max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {alertMessage && (
          <div
            className={`fixed top-20 right-8 z-50 px-5 py-3 rounded-xl border shadow-sm flex items-center gap-2.5 transition-all bg-white text-gray-900 dark:bg-neutral-900 dark:text-neutral-100 dark:border-neutral-800 ${alertMessage.type === 'success' ? 'border-gray-200' : 'border-red-200 dark:border-red-900/50'
              }`}
          >
            <span className="text-xs">{alertMessage.type === 'success' ? '✓' : '✗'}</span>
            <span className="font-medium text-xs">{alertMessage.text}</span>
          </div>
        )}

        <div className="max-w-7xl mx-auto w-full space-y-8">
          <div className="max-w-7xl mx-auto w-full">
            <DiaryForm
              initialData={editingDiary}
              isEditing={isEditing}
              onSubmit={(diary, imageFile) => {
                if (isEditing && editingDiary?.diaryId) {
                  diary.diaryId = editingDiary.diaryId;
                }
                handleSaveDiary(diary, imageFile);
              }}
              onCancel={resetForm}
              actionLoading={actionLoading}
            />
          </div>

          <div className="border-t border-gray-200/80 dark:border-neutral-800/80 pt-8 space-y-6">
            <SearchFilter
              searchKeyword={searchKeyword}
              setSearchKeyword={setSearchKeyword}
              searchDate={searchDate}
              setSearchDate={setSearchDate}
              onlyWithImages={onlyWithImages}
              setOnlyWithImages={setOnlyWithImages}
              onReset={handleResetFilters}
              onRefresh={fetchDiaries}
              fetchLoading={fetchLoading}
            />

            {error && (
              <div className="p-4 rounded-xl bg-red-50 dark:bg-red-950/20 border border-red-100 dark:border-red-900/30 text-red-700 dark:text-red-400 text-sm font-medium">
                ⚠️ {error}
              </div>
            )}

            <div className="flex items-center justify-between">
              <h3 className="text-gray-950 dark:text-white font-semibold text-base flex items-center gap-2 tracking-tight">
                <span>{currentUser?.nickname}님의 일기 목록</span>
                <span className="text-[10px] font-medium px-2 py-0.5 rounded-full bg-neutral-100 dark:bg-neutral-800 text-neutral-600 dark:text-neutral-300">
                  {filteredDiaries.length}개
                </span>
              </h3>
            </div>

            <DiaryList
              diaries={filteredDiaries}
              fetchLoading={fetchLoading}
              onEdit={startEdit}
              onDelete={handleDelete}
              onView={setViewingDiary}
            />
          </div>
        </div>
      </main>



      {viewingDiary && (
        <DiaryDetailModal diary={viewingDiary} onClose={() => setViewingDiary(null)} />
      )}
    </div>
  );
};

export default MainPage;

