import React from 'react';
import { type Diary } from '../types/diary';
import { getImageUrl } from '../api/apiConfig';

// DiaryDetailModal 컴포넌트 프로퍼티 타입 정의
interface DiaryDetailModalProps {
  diary: Diary | null;
  onClose: () => void;
}

// 일기 상세 내용 모달(Modal) 렌더링 UI 컴포넌트
// 고해상도 이미지 및 전체 텍스트 본문 표시
// param: 대상 일기 객체 및 모달 닫기 콜백
// return: 모달 오버레이 JSX 컨테이너
const DiaryDetailModal: React.FC<DiaryDetailModalProps> = ({ diary, onClose }) => {
  if (!diary) return null;

  return (
    <div className="fixed inset-0 bg-black/30 backdrop-blur-xs z-50 flex items-center justify-center p-4">
      <div className="bg-white dark:bg-neutral-900 rounded-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto shadow-2xl border border-gray-100 dark:border-neutral-800 flex flex-col animate-in fade-in zoom-in-95 duration-200">
        {/* Sticky Modal Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100 dark:border-neutral-800 sticky top-0 bg-white/95 dark:bg-neutral-900/95 backdrop-blur-md z-10">
          <div className="flex items-center gap-2.5">
            <div>
              <span className="text-[11px] font-semibold text-gray-400 dark:text-neutral-500 uppercase tracking-wider block">
                {diary.writtenDate || '날짜 없음'}
              </span>
            </div>
          </div>
          <button
            onClick={onClose}
            className="w-7 h-7 rounded-full bg-gray-100 dark:bg-neutral-800 hover:bg-gray-200 dark:hover:bg-neutral-700 text-gray-600 dark:text-neutral-400 flex items-center justify-center text-sm transition-colors cursor-pointer outline-none"
          >
            ✕
          </button>
        </div>

        {/* Modal Body */}
        <div className="p-6 space-y-6">
          {/* Large Image */}
          {diary.attachedPhotoUrl && (
            <div className="w-full max-h-[550px] rounded-xl overflow-hidden border border-gray-150 dark:border-neutral-800 bg-gray-50 dark:bg-neutral-850 flex items-center justify-center">
              <img
                src={getImageUrl(diary.attachedPhotoUrl)}
                alt={diary.diaryTitle}
                className="w-full h-full object-contain max-h-[550px]"
                onError={(e) => {
                  (e.target as HTMLImageElement).style.display = 'none';
                }}
              />
            </div>
          )}

          {/* Title and Content */}
          <div className="space-y-4">
            <h2 className="text-xl font-bold text-gray-950 dark:text-white tracking-tight leading-snug">
              {diary.diaryTitle}
            </h2>
            <p className="text-gray-700 dark:text-neutral-300 text-sm leading-relaxed whitespace-pre-line border-t border-gray-50 dark:border-neutral-800 pt-4">
              {diary.diaryContent}
            </p>
          </div>
        </div>

        {/* Footer */}
        <div className="px-6 py-4 border-t border-gray-100 dark:border-neutral-800 bg-gray-50 dark:bg-neutral-950 flex justify-end">
          <button
            onClick={onClose}
            className="px-4 py-2 bg-black dark:bg-white hover:bg-neutral-800 dark:hover:bg-neutral-100 text-white dark:text-black rounded-xl text-xs font-medium transition-colors cursor-pointer"
          >
            닫기
          </button>
        </div>
      </div>
    </div>
  );
};

export default DiaryDetailModal;
