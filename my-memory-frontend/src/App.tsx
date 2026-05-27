import { useState, useEffect } from 'react';
import './App.css';
import type { Diary } from './types/diary';
import { diaryApi } from './api/diaryApi';
import DiaryForm from './components/DiaryForm';
import DiaryList from './components/DiaryList';

function App() {
  const [diaries, setDiaries] = useState<Diary[]>([]);
  const [fetchLoading, setFetchLoading] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Search & Filter State
  const [searchUserId, setSearchUserId] = useState('kil07201');
  const [searchKeyword, setSearchKeyword] = useState('');

  // Form State
  const [isEditing, setIsEditing] = useState(false);
  const [editingDiary, setEditingDiary] = useState<Diary | undefined>(undefined);

  // Alert State
  const [alertMessage, setAlertMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  const showAlert = (text: string, type: 'success' | 'error' = 'success') => {
    setAlertMessage({ type, text });
    setTimeout(() => setAlertMessage(null), 3000);
  };

  // Fetch all diaries
  const fetchDiaries = async () => {
    setFetchLoading(true);
    setError(null);
    try {
      const data = await diaryApi.getAll();
      setDiaries(data);
    } catch (err: any) {
      setError(err.message || '네트워크 오류가 발생했습니다.');
    } finally {
      setFetchLoading(false);
    }
  };

  // Search diaries
  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!searchKeyword.trim()) {
      fetchDiaries();
      return;
    }
    setFetchLoading(true);
    setError(null);
    try {
      const data = await diaryApi.search(searchUserId, searchKeyword);
      setDiaries(data);
      showAlert(`"${searchKeyword}" 검색 완료!`);
    } catch (err: any) {
      setError(err.message || '검색 오류가 발생했습니다.');
    } finally {
      setFetchLoading(false);
    }
  };

  // Save diary (Create or Update)
  const handleSaveDiary = async (diary: Diary) => {
    setActionLoading(true);
    try {
      if (isEditing && diary.id) {
        await diaryApi.update(diary.id, diary);
        showAlert('일기가 정상적으로 수정되었습니다!');
      } else {
        await diaryApi.create(diary);
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
    // Scroll form into view on mobile
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  // Reset Form
  const resetForm = () => {
    setIsEditing(false);
    setEditingDiary(undefined);
  };

  useEffect(() => {
    fetchDiaries();
  }, []);

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col antialiased">
      {/* Header */}
      <header className="border-b border-slate-800/80 bg-slate-900/50 backdrop-blur-md sticky top-0 z-50 transition-all duration-300">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-20 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-indigo-500 to-purple-500 flex items-center justify-center text-xl shadow-lg shadow-indigo-500/20">
              🔮
            </div>
            <div>
              <h1 className="text-xl font-bold bg-gradient-to-r from-indigo-200 via-purple-200 to-pink-200 bg-clip-text text-transparent">
                My Memory
              </h1>
              <p className="text-xs text-slate-400 font-medium">MongoDB Atlas & Spring Boot Diary</p>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <button
              onClick={fetchDiaries}
              disabled={fetchLoading}
              className="px-4 py-2 text-sm rounded-lg bg-slate-800 border border-slate-700/80 text-slate-200 hover:bg-slate-700 hover:text-white transition-all disabled:opacity-50"
            >
              새로고침 🔄
            </button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-grow max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Alerts */}
        {alertMessage && (
          <div
            className={`fixed top-24 right-8 z-50 px-6 py-4 rounded-xl border shadow-xl flex items-center gap-3 transition-all transform translate-y-0 scale-100 animate-bounce ${
              alertMessage.type === 'success'
                ? 'bg-emerald-950/80 border-emerald-800 text-emerald-200 shadow-emerald-950/20'
                : 'bg-rose-950/80 border-rose-800 text-rose-200 shadow-rose-950/20'
            }`}
          >
            <span>{alertMessage.type === 'success' ? '✅' : '❌'}</span>
            <span className="font-medium text-sm">{alertMessage.text}</span>
          </div>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
          {/* Left Column: Form Card */}
          <div className="lg:col-span-5">
            <DiaryForm
              initialData={editingDiary}
              isEditing={isEditing}
              onSubmit={(diary) => {
                if (isEditing && editingDiary?.id) {
                  diary.id = editingDiary.id;
                }
                handleSaveDiary(diary);
              }}
              onCancel={resetForm}
              actionLoading={actionLoading}
            />
          </div>

          {/* Right Column: List & Filter */}
          <div className="lg:col-span-7 space-y-6">
            {/* Search Filter Box */}
            <div className="bg-slate-900/40 backdrop-blur-xl border border-slate-800/85 rounded-2xl p-6 shadow-xl">
              <h3 className="text-sm font-semibold text-slate-400 uppercase tracking-wider mb-4">
                필터 및 검색
              </h3>
              <form onSubmit={handleSearch} className="grid grid-cols-1 sm:grid-cols-12 gap-3">
                <div className="sm:col-span-4">
                  <input
                    type="text"
                    value={searchUserId}
                    onChange={(e) => setSearchUserId(e.target.value)}
                    placeholder="작성자 ID"
                    required
                    className="w-full bg-slate-950 border border-slate-800 hover:border-slate-700 focus:border-indigo-500 rounded-xl px-4 py-2.5 text-sm text-slate-100 outline-none"
                  />
                </div>
                <div className="sm:col-span-5">
                  <input
                    type="text"
                    value={searchKeyword}
                    onChange={(e) => setSearchKeyword(e.target.value)}
                    placeholder="키워드 검색..."
                    className="w-full bg-slate-950 border border-slate-800 hover:border-slate-700 focus:border-indigo-500 rounded-xl px-4 py-2.5 text-sm text-slate-100 outline-none"
                  />
                </div>
                <div className="sm:col-span-3 flex gap-2">
                  <button
                    type="submit"
                    className="w-full bg-slate-800 hover:bg-slate-750 text-slate-200 hover:text-white text-sm font-medium py-2.5 px-4 rounded-xl transition-all border border-slate-700/80 cursor-pointer"
                  >
                    검색 🔍
                  </button>
                  {searchKeyword && (
                    <button
                      type="button"
                      onClick={() => {
                        setSearchKeyword('');
                        fetchDiaries();
                      }}
                      className="bg-slate-800 hover:bg-slate-750 text-slate-200 border border-slate-700/80 p-2.5 rounded-xl text-sm"
                    >
                      ❌
                    </button>
                  )}
                </div>
              </form>
            </div>

            {/* Error Message */}
            {error && (
              <div className="p-4 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-300 text-sm font-medium">
                ⚠️ {error}
              </div>
            )}

            {/* List Header */}
            <div className="flex items-center justify-between">
              <h3 className="text-slate-100 font-bold text-lg flex items-center gap-2">
                <span>📝 나의 일기 목록</span>
                <span className="text-xs font-semibold px-2 py-0.5 rounded bg-slate-800 text-slate-400">
                  {diaries.length}개
                </span>
              </h3>
            </div>

            {/* List Grid */}
            <DiaryList
              diaries={diaries}
              fetchLoading={fetchLoading}
              onEdit={startEdit}
              onDelete={handleDelete}
            />
          </div>
        </div>
      </main>
    </div>
  );
}

export default App;
