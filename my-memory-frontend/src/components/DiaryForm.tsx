import React, { useState, useEffect } from 'react';
import { EMOTIONS, type Diary } from '../types/diary';

interface DiaryFormProps {
  initialData?: Diary;
  isEditing: boolean;
  onSubmit: (diary: Diary) => void;
  onCancel: () => void;
  actionLoading: boolean;
}

const DiaryForm: React.FC<DiaryFormProps> = ({
  initialData,
  isEditing,
  onSubmit,
  onCancel,
  actionLoading,
}) => {
  const [userId, setUserId] = useState(initialData?.userId || 'kil07201');
  const [title, setTitle] = useState(initialData?.title || '');
  const [content, setContent] = useState(initialData?.content || '');
  const [emotion, setEmotion] = useState(initialData?.emotion || 'Happy');
  const [imageUrl, setImageUrl] = useState(initialData?.imageUrl || '');
  const [createdAt, setCreatedAt] = useState(
    initialData?.createdAt || new Date().toISOString().split('T')[0]
  );

  useEffect(() => {
    if (initialData) {
      setUserId(initialData.userId);
      setTitle(initialData.title);
      setContent(initialData.content);
      setEmotion(initialData.emotion);
      setImageUrl(initialData.imageUrl || '');
      setCreatedAt(initialData.createdAt || new Date().toISOString().split('T')[0]);
    } else {
      setUserId('kil07201');
      setTitle('');
      setContent('');
      setEmotion('Happy');
      setImageUrl('');
      setCreatedAt(new Date().toISOString().split('T')[0]);
    }
  }, [initialData]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !content.trim()) {
      alert('제목과 내용을 모두 입력해 주세요.');
      return;
    }
    onSubmit({
      userId,
      title,
      content,
      emotion,
      imageUrl: imageUrl.trim() ? imageUrl : undefined,
      createdAt,
    });
  };

  return (
    <div className="bg-slate-900/40 backdrop-blur-xl border border-slate-800/85 rounded-2xl p-6 shadow-xl sticky top-28">
      <h2 className="text-lg font-bold text-slate-100 mb-6 flex items-center gap-2">
        <span>{isEditing ? '✏️ 일기 수정하기' : '✍️ 새로운 하루 기록'}</span>
        {isEditing && (
          <span className="text-xs px-2.5 py-0.5 rounded-full bg-indigo-500/20 text-indigo-300 font-normal">
            수정 모드
          </span>
        )}
      </h2>

      <form onSubmit={handleSubmit} className="space-y-5">
        <div>
          <label className="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">
            작성자 ID
          </label>
          <input
            type="text"
            value={userId}
            onChange={(e) => setUserId(e.target.value)}
            placeholder="작성자 ID"
            required
            className="w-full bg-slate-950 border border-slate-800 hover:border-slate-700 focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 rounded-xl px-4 py-3 text-slate-100 placeholder-slate-600 transition-all outline-none"
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">
            날짜
          </label>
          <input
            type="date"
            value={createdAt}
            onChange={(e) => setCreatedAt(e.target.value)}
            required
            className="w-full bg-slate-950 border border-slate-800 hover:border-slate-700 focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 rounded-xl px-4 py-3 text-slate-100 placeholder-slate-600 transition-all outline-none"
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">
            오늘의 기분 (감정)
          </label>
          <div className="grid grid-cols-3 gap-2">
            {EMOTIONS.map((emo) => {
              const isSelected = emotion === emo.name;
              return (
                <button
                  key={emo.name}
                  type="button"
                  onClick={() => setEmotion(emo.name)}
                  className={`flex flex-col items-center gap-1.5 p-3 rounded-xl border transition-all text-xs font-medium cursor-pointer ${
                    isSelected
                      ? 'bg-indigo-600 border-indigo-500 text-white shadow-lg shadow-indigo-600/20 scale-105'
                      : 'bg-slate-950 border-slate-850 hover:border-slate-700 text-slate-300'
                  }`}
                >
                  <span className="text-xl">{emo.emoji}</span>
                  <span>{emo.label}</span>
                </button>
              );
            })}
          </div>
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">
            일기 제목
          </label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="오늘 하루를 한 줄로 요약한다면?"
            required
            className="w-full bg-slate-950 border border-slate-800 hover:border-slate-700 focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 rounded-xl px-4 py-3 text-slate-100 placeholder-slate-600 transition-all outline-none"
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">
            일기 내용
          </label>
          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="오늘 어떤 특별한 일이 있었나요? 자세하게 나누어 주세요..."
            required
            rows={5}
            className="w-full bg-slate-950 border border-slate-800 hover:border-slate-700 focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 rounded-xl px-4 py-3 text-slate-100 placeholder-slate-600 transition-all outline-none resize-none"
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">
            이미지 URL (선택)
          </label>
          <input
            type="url"
            value={imageUrl}
            onChange={(e) => setImageUrl(e.target.value)}
            placeholder="https://example.com/image.jpg"
            className="w-full bg-slate-950 border border-slate-800 hover:border-slate-700 focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 rounded-xl px-4 py-3 text-slate-100 placeholder-slate-600 transition-all outline-none"
          />
          {imageUrl.trim() && (
            <div className="mt-3 relative rounded-xl overflow-hidden border border-slate-800 bg-slate-950 h-32 flex items-center justify-center">
              <img
                src={imageUrl}
                alt="미리보기"
                className="h-full w-full object-cover"
                onError={(e) => {
                  (e.target as HTMLImageElement).src =
                    'https://images.unsplash.com/photo-1506784983877-45594efa4cbe?q=80&w=300';
                }}
              />
            </div>
          )}
        </div>

        <div className="flex items-center gap-3 pt-2">
          {isEditing && (
            <button
              type="button"
              onClick={onCancel}
              className="flex-1 bg-slate-850 hover:bg-slate-800 border border-slate-850 text-slate-300 font-medium py-3 px-4 rounded-xl transition-all"
            >
              취소
            </button>
          )}
          <button
            type="submit"
            disabled={actionLoading}
            className="flex-2 bg-gradient-to-r from-indigo-500 to-purple-500 hover:from-indigo-600 hover:to-purple-600 text-white font-medium py-3 px-4 rounded-xl transition-all shadow-lg shadow-indigo-500/10 hover:shadow-indigo-500/20 active:scale-[0.98] disabled:opacity-50"
          >
            {isEditing ? '수정 완료' : '저장하기 🚀'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default DiaryForm;
