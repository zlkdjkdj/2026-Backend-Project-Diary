import React, { useState, useEffect } from 'react';
import type { Diary } from '../types/diary';
import { diaryApi } from '../api/diaryApi';
import DiaryForm from '../components/DiaryForm';
import DiaryList from '../components/DiaryList';
import DiaryDetailModal from '../components/DiaryDetailModal';
import Header from '../components/Header';
import SearchFilter from '../components/SearchFilter';
import GoogleDriveWidget from '../components/GoogleDriveWidget';

interface MainPageProps {
  token: string | null;
  currentUser: { email: string; nickname: string } | null;
  onLogout: () => void;
}

const MainPage: React.FC<MainPageProps> = ({ token, currentUser, onLogout }) => {
  const [diaries, setDiaries] = useState<Diary[]>([]);
  const [fetchLoading, setFetchLoading] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Search & Filter State
  const [searchKeyword, setSearchKeyword] = useState('');
  const [searchDate, setSearchDate] = useState('');
  const [onlyWithImages, setOnlyWithImages] = useState(false);

  // Form State
  const [isEditing, setIsEditing] = useState(false);
  const [editingDiary, setEditingDiary] = useState<Diary | undefined>(undefined);

  // Modal State
  const [viewingDiary, setViewingDiary] = useState<Diary | null>(null);

  // Alert State
  const [alertMessage, setAlertMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  // Google Drive Widget State
  const [showDriveWidget, setShowDriveWidget] = useState(true);

  const showAlert = (text: string, type: 'success' | 'error' = 'success') => {
    setAlertMessage({ type, text });
    setTimeout(() => setAlertMessage(null), 3000);
  };

  // Fetch all diaries
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

  // Save diary (Create or Update)
  const handleSaveDiary = async (diary: Diary, imageFile: File | null) => {
    if (!token) return;
    setActionLoading(true);
    try {
      if (isEditing && diary.id) {
        await diaryApi.update(diary.id, diary, imageFile);
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

  // Delete diary
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
      if (isEditing && editingDiary?.id === id) {
        resetForm();
      }
    } catch (err: any) {
      showAlert(err.message || '삭제 오류가 발생했습니다.', 'error');
    } finally {
      setActionLoading(false);
    }
  };

  // Prepare edit mode
  const startEdit = (diary: Diary) => {
    if (!diary.id) return;
    setIsEditing(true);
    setEditingDiary(diary);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  // Reset Form
  const resetForm = () => {
    setIsEditing(false);
    setEditingDiary(undefined);
  };

  // Backup Handler
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

  // Restore Handler
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

  // Filter Reset Handler
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

  // Filter diaries locally based on search filters
  const filteredDiaries = diaries.filter((diary) => {
    if (searchKeyword.trim()) {
      const keyword = searchKeyword.toLowerCase();
      const titleMatch = diary.title?.toLowerCase().includes(keyword);
      const contentMatch = diary.content?.toLowerCase().includes(keyword);
      if (!titleMatch && !contentMatch) return false;
    }

    if (searchDate) {
      if (diary.createdAt !== searchDate) return false;
    }

    if (onlyWithImages) {
      if (!diary.imageUrl) return false;
    }

    return true;
  });

  return (
    <div className="min-h-screen bg-[#fbfbfd] text-[#1d1d1f] flex flex-col antialiased">
      {/* Header */}
      <Header
        currentUser={currentUser}
        actionLoading={actionLoading}
        onBackup={handleBackup}
        onRestore={handleRestore}
        onLogout={onLogout}
      />

      {/* Main Content */}
      <main className="flex-grow max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Alerts */}
        {alertMessage && (
          <div
            className={`fixed top-20 right-8 z-50 px-5 py-3 rounded-xl border shadow-sm flex items-center gap-2.5 transition-all bg-white text-gray-900 ${
              alertMessage.type === 'success' ? 'border-gray-200' : 'border-red-200'
            }`}
          >
            <span className="text-xs">{alertMessage.type === 'success' ? '✓' : '✗'}</span>
            <span className="font-medium text-xs">{alertMessage.text}</span>
          </div>
        )}

        <div className="max-w-7xl mx-auto w-full space-y-8">
          {/* Centered Diary Form Card */}
          <div className="max-w-5xl mx-auto w-full">
            <DiaryForm
              initialData={editingDiary}
              isEditing={isEditing}
              onSubmit={(diary, imageFile) => {
                if (isEditing && editingDiary?.id) {
                  diary.id = editingDiary.id;
                }
                handleSaveDiary(diary, imageFile);
              }}
              onCancel={resetForm}
              actionLoading={actionLoading}
            />
          </div>

          {/* Divider and List Section */}
          <div className="border-t border-gray-200/80 pt-8 space-y-6">
            {/* Search & Filter Box */}
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

            {/* Error Message */}
            {error && (
              <div className="p-4 rounded-xl bg-red-50 border border-red-100 text-red-700 text-sm font-medium">
                ⚠️ {error}
              </div>
            )}

            {/* List Header */}
            <div className="flex items-center justify-between">
              <h3 className="text-gray-950 font-semibold text-base flex items-center gap-2 tracking-tight">
                <span>{currentUser?.nickname}님의 일기 목록</span>
                <span className="text-[10px] font-medium px-2 py-0.5 rounded-full bg-neutral-100 text-neutral-600">
                  {filteredDiaries.length}개
                </span>
              </h3>
            </div>

            {/* List Grid */}
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

      {/* Floating Google Drive Widget at bottom right */}
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

