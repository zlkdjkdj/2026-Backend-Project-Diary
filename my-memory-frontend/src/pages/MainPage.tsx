import React, { useState, useEffect } from 'react';
import type { Diary } from '../types/diary';
import { diaryApi } from '../api/diaryApi';
import DiaryForm from '../components/DiaryForm';
import DiaryList from '../components/DiaryList';
import DiaryDetailModal from '../components/DiaryDetailModal';
import Header from '../components/Header';
import SearchFilter from '../components/SearchFilter';
import GoogleDriveWidget from '../components/GoogleDriveWidget';

// MainPage 컴포넌트 프로퍼티 타입 정의
interface MainPageProps {
  token: string | null;
  currentUser: { email: string; nickname: string } | null;
  onLogout: () => void;
}

// 인증 사용자 접근 메인 뷰포트(Viewport) 컴포넌트
// 일기 데이터 생명주기(CRUD) 및 상태 중앙 관리
// param: 인증 토큰, 사용자 정보, 로그아웃 콜백
// return: 메인 페이지 UI 렌더링 결과
const MainPage: React.FC<MainPageProps> = ({
  token,
  currentUser,
  onLogout,
}) => {
  // 도메인 데이터 및 네트워크 통신 상태 관리
  const [diaries, setDiaries] = useState<Diary[]>([]);
  const [fetchLoading, setFetchLoading] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 검색 및 필터링 관련 상태 관리
  const [searchKeyword, setSearchKeyword] = useState('');
  const [searchDate, setSearchDate] = useState('');
  const [onlyWithImages, setOnlyWithImages] = useState(false);

  // 폼(Form) UI 관련 상태 관리 (신규 작성 및 수정 모드 전환)
  const [isEditing, setIsEditing] = useState(false);
  const [editingDiary, setEditingDiary] = useState<Diary | undefined>(undefined);

  // 상세 보기 모달 창 상태 관리
  const [viewingDiary, setViewingDiary] = useState<Diary | null>(null);

  // 시스템 알림(Toast/Alert) 메시지 상태 관리
  const [alertMessage, setAlertMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  // 백업/복원을 위한 서드파티 통합 위젯 노출 상태
  const [showDriveWidget, setShowDriveWidget] = useState(true);

  // 화면 상단 일시 노출 알림 메시지 설정
  // param: 출력 메시지 본문
  // param: 알림 성격 ('success' | 'error')
  const showAlert = (text: string, type: 'success' | 'error' = 'success') => {
    setAlertMessage({ type, text });
    setTimeout(() => setAlertMessage(null), 3000);
  };

  // 서버 API 연동 인증 사용자 전체 일기 데이터 비동기 조회
  const fetchDiaries = async () => {
    if (!token) return;
    setFetchLoading(true);
    setError(null);
    try {
      const data = await diaryApi.getAll();
      setDiaries(data);
    } catch (err: any) {
      setError(err.message || '네트워크 오류가 발생했습니다.');
      if (err.message.includes('유효하지 않')) {
        onLogout();
      }
    } finally {
      setFetchLoading(false);
    }
  };

  // 신규 생성 및 기존 수정 API 요청 처리
  // param: 전송 일기 데이터
  // param: 첨부 멀티파트 이미지 파일 (선택)
  const handleSaveDiary = async (diary: Diary, imageFile: File | null) => {
    if (!token) return;
    setActionLoading(true);
    try {
      if (isEditing && diary.diaryId) {
        await diaryApi.update(diary.diaryId, diary, imageFile);
        showAlert('일기가 정상적으로 수정되었습니다!');
      } else {
        await diaryApi.create(diary, imageFile);
        showAlert('일기가 정상적으로 저장되었습니다!');
      }
      resetForm();
      fetchDiaries();
    } catch (err: any) {
      showAlert(err.message || '저장 중 오류가 발생했습니다.', 'error');
    } finally {
      setActionLoading(false);
    }
  };

  // 지정 식별자 일기 데이터 삭제 요청
  // param: 삭제 대상 일기 고유 ID
  const handleDelete = async (id: string) => {
    if (!token) return;
    if (!window.confirm('정말로 이 일기를 삭제하시겠습니까?')) {
      return;
    }
    setActionLoading(true);
    try {
      await diaryApi.delete(id);
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

  // 선택 일기 데이터 폼 주입 및 수정 모드 전환
  // param: 수정 대상 일기 객체
  const startEdit = (diary: Diary) => {
    if (!diary.diaryId) return;
    setIsEditing(true);
    setEditingDiary(diary);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  // 입력 폼 상태 초기화 및 신규 작성 모드 전환
  const resetForm = () => {
    setIsEditing(false);
    setEditingDiary(undefined);
  };

  // 사용자 전체 일기 및 이미지 데이터 ZIP 비동기 다운로드
  const handleBackup = async () => {
    if (!token) return;
    setActionLoading(true);
    try {
      const blob = await diaryApi.backup();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `diary_backup_${new Date().toISOString().split('T')[0]}.zip`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
      showAlert('백업 파일 다운로드가 완료되었습니다.');
    } catch (err: any) {
      showAlert(err.message || '백업 생성에 실패했습니다.', 'error');
    } finally {
      setActionLoading(false);
    }
  };

  // 업로드 ZIP 아카이브 서버 전송 및 시스템 상태 동기화(복원)
  // param: 파일 입력 이벤트 객체
  const handleRestore = async (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!token) return;
    const file = e.target.files?.[0];
    if (!file) return;

    if (!window.confirm('정말 복원하시겠습니까? 기존의 모든 일기 데이터는 지워지고 백업 파일의 내용으로 대체됩니다.')) {
      e.target.value = '';
      return;
    }

    setActionLoading(true);
    try {
      const msg = await diaryApi.restore(file);
      showAlert(msg);
      fetchDiaries();
    } catch (err: any) {
      showAlert(err.message || '복원 중 오류가 발생했습니다.', 'error');
    } finally {
      setActionLoading(false);
      e.target.value = '';
    }
  };

  // 검색어 및 날짜 필터링 조건 초기화 및 목록 재렌더링
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

  // 메모리에 적재된 전체 일기 데이터를 기반으로 검색/필터링 로직을 수행합니다 (Client-side Filtering).
  const filteredDiaries = diaries.filter((diary) => {
    if (searchKeyword.trim()) {
      const keyword = searchKeyword.toLowerCase();
      const titleMatch = diary.diaryTitle?.toLowerCase().includes(keyword);
      const contentMatch = diary.diaryContent?.toLowerCase().includes(keyword);
      if (!titleMatch && !contentMatch) return false;
    }

    if (searchDate) {
      if (diary.writtenDate !== searchDate) return false;
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
        actionLoading={actionLoading}
        onBackup={handleBackup}
        onRestore={handleRestore}
        onLogout={onLogout}
      />

      <main className="flex-grow max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {alertMessage && (
          <div
            className={`fixed top-20 right-8 z-50 px-5 py-3 rounded-xl border shadow-sm flex items-center gap-2.5 transition-all bg-white text-gray-900 dark:bg-neutral-900 dark:text-neutral-100 dark:border-neutral-800 ${
              alertMessage.type === 'success' ? 'border-gray-200' : 'border-red-200 dark:border-red-900/50'
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

      <GoogleDriveWidget
        show={token !== null && currentUser !== null && showDriveWidget}
        onClose={() => setShowDriveWidget(false)}
      />

      {viewingDiary && (
        <DiaryDetailModal diary={viewingDiary} onClose={() => setViewingDiary(null)} />
      )}
    </div>
  );
};

export default MainPage;

