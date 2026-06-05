import React, { useState, useEffect } from 'react';
import { EMOTIONS, type Diary } from '../types/diary';

interface DiaryFormProps {
  initialData?: Diary;
  isEditing: boolean;
  onSubmit: (diary: Diary, imageFile: File | null) => void;
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
  const [title, setTitle] = useState(initialData?.title || '');
  const [content, setContent] = useState(initialData?.content || '');
  const [emotion, setEmotion] = useState(initialData?.emotion || 'Happy');
  const [imageUrl, setImageUrl] = useState(initialData?.imageUrl || '');
  const [createdAt, setCreatedAt] = useState(
    initialData?.createdAt || new Date().toISOString().split('T')[0]
  );
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string>('');

  useEffect(() => {
    if (initialData) {
      setTitle(initialData.title);
      setContent(initialData.content);
      setEmotion(initialData.emotion);
      setImageUrl(initialData.imageUrl || '');
      setPreviewUrl(initialData.imageUrl || '');
      setCreatedAt(initialData.createdAt || new Date().toISOString().split('T')[0]);
      setImageFile(null);
    } else {
      setTitle('');
      setContent('');
      setEmotion('Happy');
      setImageUrl('');
      setPreviewUrl('');
      setCreatedAt(new Date().toISOString().split('T')[0]);
      setImageFile(null);
    }
  }, [initialData]);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      setImageFile(file);
      setPreviewUrl(URL.createObjectURL(file));
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !content.trim()) {
      alert('제목과 내용을 모두 입력해 주세요.');
      return;
    }
    onSubmit({
      userId: '', // 백엔드에서 인증 토큰으로 처리됨
      title,
      content,
      emotion,
      imageUrl: imageUrl.trim() ? imageUrl : undefined,
      createdAt,
    }, imageFile);
  };

  return (
    <div className="bg-white dark:bg-neutral-900 border border-gray-200/80 dark:border-neutral-800/80 rounded-2xl p-8 md:p-10 shadow-sm">
      <h2 className="text-lg md:text-xl font-bold text-gray-900 dark:text-white mb-6 flex items-center gap-2 tracking-tight">
        <span>{isEditing ? '일기 수정하기' : '새로운 기록'}</span>
        {isEditing && (
          <span className="text-[11px] px-2.5 py-0.5 rounded-full bg-neutral-100 dark:bg-neutral-855 text-neutral-600 dark:text-neutral-400 font-medium">
            수정 중
          </span>
        )}
      </h2>

      <form onSubmit={handleSubmit} className="space-y-6">
        <div>
          <label className="block text-[11px] font-semibold text-gray-500 dark:text-neutral-400 uppercase tracking-wider mb-1.5">
            날짜
          </label>
          <input
            type="date"
            value={createdAt}
            onChange={(e) => setCreatedAt(e.target.value)}
            required
            className="w-full bg-white dark:bg-neutral-800 border border-gray-200 dark:border-neutral-700 focus:border-black dark:focus:border-white rounded-xl px-3 py-2 text-gray-900 dark:text-white placeholder-gray-400 transition-colors outline-none text-sm"
          />
        </div>


        <div>
          <label className="block text-xs font-semibold text-gray-500 dark:text-neutral-400 uppercase tracking-wider mb-1.5">
            제목
          </label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="오늘 하루를 요약해보세요"
            required
            className="w-full bg-white dark:bg-neutral-800 border border-gray-200 dark:border-neutral-700 focus:border-black dark:focus:border-white rounded-xl px-4 py-3 text-gray-900 dark:text-white placeholder-gray-400 transition-colors outline-none text-base"
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-gray-500 dark:text-neutral-400 uppercase tracking-wider mb-1.5">
            내용
          </label>
          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="오늘 하루 어떤 기록을 남기고 싶으신가요?"
            required
            rows={10}
            className="w-full bg-white dark:bg-neutral-800 border border-gray-200 dark:border-neutral-700 focus:border-black dark:focus:border-white rounded-xl px-4 py-3 text-gray-900 dark:text-white placeholder-gray-400 transition-colors outline-none text-base resize-none"
          />
        </div>

        <div>
          <label className="block text-[11px] font-semibold text-gray-500 dark:text-neutral-400 uppercase tracking-wider mb-1.5">
            이미지 (선택)
          </label>
          <input
            type="file"
            accept="image/*"
            onChange={handleFileChange}
            className="w-full bg-white dark:bg-neutral-800 border border-gray-200 dark:border-neutral-700 focus:border-black dark:focus:border-white rounded-xl px-3 py-2 text-gray-900 dark:text-white placeholder-gray-400 transition-colors outline-none text-sm file:mr-3 file:py-1 file:px-2.5 file:rounded-md file:border-0 file:text-[11px] file:font-semibold file:bg-gray-100 dark:file:bg-neutral-700 file:text-gray-700 dark:file:text-neutral-300 hover:file:bg-gray-200 dark:hover:file:bg-neutral-600"
          />
          {previewUrl && (
            <div className="mt-3 relative rounded-xl overflow-hidden border border-gray-200 dark:border-neutral-700 bg-gray-50 dark:bg-neutral-800 h-32 flex items-center justify-center">
              <img
                src={previewUrl}
                alt="미리보기"
                className="h-full w-full object-cover"
                onError={(e) => {
                  (e.target as HTMLImageElement).src =
                    'https://images.unsplash.com/photo-1506784983877-45594efa4cbe?q=80&w=300';
                }}
              />
              <button
                type="button"
                onClick={() => {
                  setImageFile(null);
                  setPreviewUrl('');
                  setImageUrl('');
                }}
                className="absolute top-2 right-2 bg-black/70 hover:bg-black text-white rounded-full w-5 h-5 flex items-center justify-center text-xs transition-colors cursor-pointer"
                title="이미지 제거"
              >
                ✕
              </button>
            </div>
          )}
        </div>

        <div className="flex items-center gap-3 pt-2">
          {isEditing && (
            <button
              type="button"
              onClick={onCancel}
              className="flex-1 bg-gray-100 dark:bg-neutral-800 hover:bg-gray-200 dark:hover:bg-neutral-700 text-gray-700 dark:text-neutral-300 font-medium py-2.5 px-4 rounded-xl transition-colors text-sm cursor-pointer"
            >
              취소
            </button>
          )}
          <button
            type="submit"
            disabled={actionLoading}
            className="flex-2 bg-black dark:bg-white hover:bg-neutral-800 dark:hover:bg-neutral-100 text-white dark:text-black font-medium py-2.5 px-4 rounded-xl transition-colors active:scale-[0.98] disabled:opacity-50 text-sm cursor-pointer"
          >
            {isEditing ? '수정 완료' : '저장하기'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default DiaryForm;
