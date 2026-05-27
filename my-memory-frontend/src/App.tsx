import { useState, useEffect } from 'react';
import './App.css';
import type { Diary, LoginRequest, RegisterRequest } from './types/diary';
import { diaryApi } from './api/diaryApi';
import { authApi } from './api/authApi';
import DiaryForm from './components/DiaryForm';
import DiaryList from './components/DiaryList';
import AuthForm from './components/AuthForm';
import DiaryDetailModal from './components/DiaryDetailModal';

function App() {
  // Auth State
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
  const [currentUser, setCurrentUser] = useState<{ email: string; nickname: string } | null>(() => {
    const saved = localStorage.getItem('currentUser');
    return saved ? JSON.parse(saved) : null;
  });

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

  // Auth Handlers
  const handleLogin = async (request: LoginRequest) => {
    setActionLoading(true);
    try {
      const response = await authApi.login(request);
      setToken(response.token);
      setCurrentUser({ email: response.email, nickname: response.nickname });
      localStorage.setItem('token', response.token);
      localStorage.setItem('currentUser', JSON.stringify({ email: response.email, nickname: response.nickname }));
      showAlert(`환영합니다, ${response.nickname}님!`);
    } catch (err: any) {
      showAlert(err.message || '로그인 오류가 발생했습니다.', 'error');
    } finally {
      setActionLoading(false);
    }
  };

  const handleRegister = async (request: RegisterRequest) => {
    setActionLoading(true);
    try {
      const msg = await authApi.register(request);
      showAlert(msg);
    } catch (err: any) {
      showAlert(err.message || '회원가입 오류가 발생했습니다.', 'error');
    } finally {
      setActionLoading(false);
    }
  };

  const handleLogout = () => {
    setToken(null);
    setCurrentUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    setDiaries([]);
    resetForm();
    showAlert('로그아웃 되었습니다.');
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
        handleLogout();
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

  useEffect(() => {
    if (token) {
      fetchDiaries();
    }
  }, [token]);

  // Filter diaries locally based on search filters
  const filteredDiaries = diaries.filter((diary) => {
    // 1. Keyword search
    if (searchKeyword.trim()) {
      const keyword = searchKeyword.toLowerCase();
      const titleMatch = diary.title?.toLowerCase().includes(keyword);
      const contentMatch = diary.content?.toLowerCase().includes(keyword);
      if (!titleMatch && !contentMatch) return false;
    }

    // 2. Date search
    if (searchDate) {
      if (diary.createdAt !== searchDate) return false;
    }

    // 3. Image search
    if (onlyWithImages) {
      if (!diary.imageUrl) return false;
    }

    return true;
  });

  return (
    <div className="min-h-screen bg-[#fbfbfd] text-[#1d1d1f] flex flex-col antialiased">
      {/* Header */}
      <header className="border-b border-gray-200/80 bg-white/80 backdrop-blur-md sticky top-0 z-50 transition-all duration-300">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div>
              <h1 className="text-lg font-semibold tracking-tight text-gray-950">
                My Memory
              </h1>
              <p className="text-[10px] text-gray-400 font-medium">MongoDB Atlas & Spring Boot Diary</p>
            </div>
          </div>
          {token && currentUser && (
            <div className="flex items-center gap-2">
              <span className="text-xs font-medium text-gray-600 mr-2">
                <span className="text-black font-semibold">{currentUser.nickname}</span>님
              </span>
              <button
                onClick={handleBackup}
                disabled={actionLoading}
                className="px-3 py-1.5 text-xs rounded-lg bg-black hover:bg-neutral-800 text-white transition-colors font-medium cursor-pointer disabled:opacity-50"
              >
                백업
              </button>
              <label
                className={`px-3 py-1.5 text-xs rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-800 border border-transparent font-medium cursor-pointer transition-colors ${
                  actionLoading ? 'opacity-50 pointer-events-none' : ''
                }`}
              >
                복원
                <input
                  type="file"
                  accept=".zip"
                  onChange={handleRestore}
                  disabled={actionLoading}
                  className="hidden"
                />
              </label>
              <a
                href="https://drive.google.com"
                target="_blank"
                rel="noopener noreferrer"
                className="px-3 py-1.5 text-xs rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-800 border border-transparent font-medium cursor-pointer transition-colors inline-flex items-center gap-1"
              >
                구글드라이브 이동 ↗
              </a>
              <button
                onClick={handleLogout}
                className="px-3 py-1.5 text-xs rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-800 transition-colors border border-transparent font-medium cursor-pointer"
              >
                로그아웃
              </button>
            </div>
          )}
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-grow max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Alerts */}
        {alertMessage && (
          <div
            className={`fixed top-20 right-8 z-50 px-5 py-3 rounded-xl border shadow-sm flex items-center gap-2.5 transition-all bg-white text-gray-900 ${
              alertMessage.type === 'success'
                ? 'border-gray-200'
                : 'border-red-200'
            }`}
          >
            <span className="text-xs">{alertMessage.type === 'success' ? '✓' : '✗'}</span>
            <span className="font-medium text-xs">{alertMessage.text}</span>
          </div>
        )}

        {!token ? (
          <AuthForm onLogin={handleLogin} onRegister={handleRegister} isLoading={actionLoading} />
        ) : (
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
              <div className="bg-white border border-gray-200/80 rounded-2xl p-6 shadow-sm space-y-4">
                <div className="flex items-center justify-between border-b border-gray-100 pb-3">
                  <h3 className="text-xs font-bold text-gray-900 uppercase tracking-wider">
                    검색 및 필터
                  </h3>
                  <button
                    onClick={() => {
                      setSearchKeyword('');
                      setSearchDate('');
                      setOnlyWithImages(false);
                      fetchDiaries();
                    }}
                    className="text-xs text-gray-400 hover:text-black transition-colors font-medium cursor-pointer"
                  >
                    필터 초기화
                  </button>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-12 gap-4">
                  {/* Keyword Input */}
                  <div className="md:col-span-5">
                    <label className="block text-[10px] font-semibold text-gray-400 mb-1.5 uppercase">키워드 검색</label>
                    <input
                      type="text"
                      value={searchKeyword}
                      onChange={(e) => setSearchKeyword(e.target.value)}
                      placeholder="제목 또는 내용 검색"
                      className="w-full bg-white border border-gray-200 focus:border-black rounded-xl px-3 py-2 text-sm text-gray-900 outline-none transition-colors"
                    />
                  </div>

                  {/* Date Input */}
                  <div className="md:col-span-5">
                    <label className="block text-[10px] font-semibold text-gray-400 mb-1.5 uppercase">날짜 검색</label>
                    <input
                      type="date"
                      value={searchDate}
                      onChange={(e) => setSearchDate(e.target.value)}
                      className="w-full bg-white border border-gray-200 focus:border-black rounded-xl px-3 py-2 text-sm text-gray-900 outline-none transition-colors"
                    />
                  </div>

                  {/* Action Buttons (Refresh) */}
                  <div className="md:col-span-2 flex items-end justify-end md:justify-center md:border-l md:border-gray-100 md:pl-2">
                    <button
                      onClick={fetchDiaries}
                      disabled={fetchLoading}
                      className="flex flex-col items-center justify-center p-1.5 rounded-xl hover:bg-gray-50 transition-colors disabled:opacity-50 cursor-pointer w-full"
                    >
                      <span className="text-lg">🔄</span>
                      <span className="text-[10px] text-gray-400 font-semibold mt-0.5">새로고침</span>
                    </button>
                  </div>
                </div>

                {/* Additional filters (Checkbox) */}
                <div className="flex items-center gap-2 pt-2">
                  <label className="flex items-center gap-2 text-xs text-gray-755 font-medium cursor-pointer select-none">
                    <input
                      type="checkbox"
                      checked={onlyWithImages}
                      onChange={(e) => setOnlyWithImages(e.target.checked)}
                      className="w-4 h-4 rounded border-gray-300 text-black focus:ring-black cursor-pointer accent-black"
                    />
                    이미지 있는 일기만 모아보기
                  </label>
                </div>
              </div>

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
        )}
      </main>

      {/* Floating Google Drive Widget at bottom right */}
      {token && currentUser && showDriveWidget && (
        <div className="fixed bottom-6 right-6 z-40 w-64 bg-white/95 backdrop-blur-md border border-gray-200/80 rounded-3xl p-5 shadow-2xl hover:shadow-neutral-300/30 transition-all duration-300 flex flex-col justify-between min-h-[240px]">
          <div className="flex justify-between items-start">
            <div className="w-10 h-10 bg-blue-600 rounded-2xl flex items-center justify-center text-white text-lg shadow-md shadow-blue-500/20">
              💾
            </div>
            <button
              onClick={() => setShowDriveWidget(false)}
              className="text-gray-400 hover:text-gray-650 p-1 cursor-pointer transition-colors text-xs font-semibold"
              title="닫기"
            >
              ✕
            </button>
          </div>
          <div className="space-y-2 mt-4 flex-grow">
            <h4 className="text-xs font-bold text-gray-900 tracking-tight">구글 드라이브 백업</h4>
            <p className="text-[11px] text-gray-650 leading-relaxed font-medium">
              로컬 백업 파일을 구글드라이브로 업로드 하면 장소에 상관없이 일기 조회가 가능합니다!
            </p>
          </div>
          <a
            href="https://drive.google.com"
            target="_blank"
            rel="noopener noreferrer"
            className="flex items-center justify-center gap-1.5 w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2.5 px-4 rounded-xl transition-all shadow-lg shadow-blue-600/10 active:scale-[0.98] cursor-pointer text-xs mt-4"
          >
            <span>구글 드라이브 이동</span>
            <span className="text-sm">↗</span>
          </a>
        </div>
      )}

      {viewingDiary && (
        <DiaryDetailModal diary={viewingDiary} onClose={() => setViewingDiary(null)} />
      )}
    </div>
  );
}

export default App;
