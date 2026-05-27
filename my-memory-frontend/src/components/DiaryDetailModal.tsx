import React from 'react';
import { EMOTIONS, type Diary } from '../types/diary';

interface DiaryDetailModalProps {
  diary: Diary | null;
  onClose: () => void;
}

const DiaryDetailModal: React.FC<DiaryDetailModalProps> = ({ diary, onClose }) => {
  if (!diary) return null;

  const emo = EMOTIONS.find((e) => e.name === diary.emotion) || {
    emoji: '📝',
    label: diary.emotion,
  };

  return (
    <div className="fixed inset-0 bg-black/30 backdrop-blur-xs z-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto shadow-2xl border border-gray-100 flex flex-col animate-in fade-in zoom-in-95 duration-200">
        {/* Sticky Modal Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100 sticky top-0 bg-white/95 backdrop-blur-md z-10">
          <div className="flex items-center gap-2.5">
            <span className="text-2xl" title={emo.label}>
              {emo.emoji}
            </span>
            <div>
              <span className="text-[11px] font-semibold text-gray-400 uppercase tracking-wider block">
                {diary.createdAt || '날짜 없음'}
              </span>
            </div>
          </div>
          <button
            onClick={onClose}
            className="w-7 h-7 rounded-full bg-gray-100 hover:bg-gray-200 text-gray-600 flex items-center justify-center text-sm transition-colors cursor-pointer outline-none"
          >
            ✕
          </button>
        </div>

        {/* Modal Body */}
        <div className="p-6 space-y-6">
          {/* Large Image */}
          {diary.imageUrl && (
            <div className="w-full max-h-[550px] rounded-xl overflow-hidden border border-gray-150 bg-gray-50 flex items-center justify-center">
              <img
                src={diary.imageUrl}
                alt={diary.title}
                className="w-full h-full object-contain max-h-[550px]"
                onError={(e) => {
                  (e.target as HTMLImageElement).style.display = 'none';
                }}
              />
            </div>
          )}

          {/* Title and Content */}
          <div className="space-y-4">
            <h2 className="text-xl font-bold text-gray-950 tracking-tight leading-snug">
              {diary.title}
            </h2>
            <p className="text-gray-700 text-sm leading-relaxed whitespace-pre-line border-t border-gray-50 pt-4">
              {diary.content}
            </p>
          </div>
        </div>

        {/* Footer */}
        <div className="px-6 py-4 border-t border-gray-100 bg-gray-50 flex justify-end">
          <button
            onClick={onClose}
            className="px-4 py-2 bg-black hover:bg-neutral-800 text-white rounded-xl text-xs font-medium transition-colors cursor-pointer"
          >
            닫기
          </button>
        </div>
      </div>
    </div>
  );
};

export default DiaryDetailModal;
