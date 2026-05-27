import React from 'react';
import { EMOTIONS, type Diary } from '../types/diary';

interface DiaryItemProps {
  diary: Diary;
  onEdit: (diary: Diary) => void;
  onDelete: (id: string) => void;
  onView: (diary: Diary) => void;
}

const DiaryItem: React.FC<DiaryItemProps> = ({ diary, onEdit, onDelete, onView }) => {
  const emo = EMOTIONS.find((e) => e.name === diary.emotion) || {
    emoji: '📝',
    color: 'slate',
    label: diary.emotion,
  };

  return (
    <div 
      onClick={() => onView(diary)}
      className="group bg-white border border-gray-200/80 rounded-2xl p-5 transition-all duration-300 hover:shadow-sm hover:translate-y-[-1px] flex flex-col md:flex-row gap-5 cursor-pointer"
    >
      {diary.imageUrl && (
        <div className="md:w-32 h-24 md:h-auto rounded-xl overflow-hidden border border-gray-150 bg-gray-50 flex-shrink-0">
          <img
            src={diary.imageUrl}
            alt={diary.title}
            className="w-full h-full object-cover group-hover:scale-[1.02] transition-transform duration-500"
            onError={(e) => {
              (e.target as HTMLImageElement).src =
                'https://images.unsplash.com/photo-1506784983877-45594efa4cbe?q=80&w=300';
            }}
          />
        </div>
      )}
      <div className="flex-grow flex flex-col justify-between space-y-3">
        <div>
          {/* Card Header info */}
          <div className="flex flex-wrap items-center justify-between gap-2 mb-1.5">
            <div className="flex items-center gap-2">
              <span className="text-xl" title={emo.label}>
                {emo.emoji}
              </span>
              <span className="text-xs text-gray-500 font-medium">{diary.userId}</span>
              <span className="text-xs text-gray-300 font-medium">|</span>
              <span className="text-xs text-gray-400 font-medium">
                {diary.createdAt || '날짜 없음'}
              </span>
            </div>
            <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  onEdit(diary);
                }}
                className="p-1.5 rounded-lg text-gray-400 hover:text-black hover:bg-gray-100 transition-all cursor-pointer"
                title="수정"
              >
                ✏️
              </button>
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  diary.id && onDelete(diary.id);
                }}
                className="p-1.5 rounded-lg text-gray-400 hover:text-rose-600 hover:bg-rose-50 transition-all cursor-pointer"
                title="삭제"
              >
                🗑️
              </button>
            </div>
          </div>
          {/* Title */}
          <h4 className="text-gray-900 font-semibold text-base mb-1.5 transition-colors">
            {diary.title}
          </h4>
          {/* Content */}
          <p className="text-gray-600 text-sm leading-relaxed whitespace-pre-line">
            {diary.content}
          </p>
        </div>
      </div>
    </div>
  );
};

export default DiaryItem;
