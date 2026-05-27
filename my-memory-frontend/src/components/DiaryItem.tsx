import React from 'react';
import { EMOTIONS, type Diary } from '../types/diary';

interface DiaryItemProps {
  diary: Diary;
  onEdit: (diary: Diary) => void;
  onDelete: (id: string) => void;
}

const DiaryItem: React.FC<DiaryItemProps> = ({ diary, onEdit, onDelete }) => {
  const emo = EMOTIONS.find((e) => e.name === diary.emotion) || {
    emoji: '📝',
    color: 'slate',
    label: diary.emotion,
  };

  return (
    <div className="group bg-slate-900/30 backdrop-blur-xl border border-slate-850 hover:border-slate-800 rounded-2xl p-5 transition-all duration-300 hover:shadow-lg hover:shadow-indigo-950/10 hover:translate-y-[-2px] flex flex-col md:flex-row gap-5">
      {diary.imageUrl && (
        <div className="md:w-32 h-24 md:h-auto rounded-xl overflow-hidden border border-slate-800 bg-slate-950 flex-shrink-0">
          <img
            src={diary.imageUrl}
            alt={diary.title}
            className="w-full h-full object-cover group-hover:scale-105 transition-all duration-500"
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
              <span className="text-xs text-indigo-400 font-semibold">{diary.userId}</span>
              <span className="text-xs text-slate-500 font-medium">|</span>
              <span className="text-xs text-slate-400 font-medium">
                {diary.createdAt || '날짜 없음'}
              </span>
            </div>
            <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
              <button
                onClick={() => onEdit(diary)}
                className="p-1.5 rounded-lg text-slate-400 hover:text-indigo-400 hover:bg-indigo-500/10 transition-all cursor-pointer"
                title="수정"
              >
                ✏️
              </button>
              <button
                onClick={() => diary.id && onDelete(diary.id)}
                className="p-1.5 rounded-lg text-slate-400 hover:text-rose-400 hover:bg-rose-500/10 transition-all cursor-pointer"
                title="삭제"
              >
                🗑️
              </button>
            </div>
          </div>
          {/* Title */}
          <h4 className="text-slate-100 font-bold text-base mb-1.5 group-hover:text-indigo-300 transition-colors">
            {diary.title}
          </h4>
          {/* Content */}
          <p className="text-slate-350 text-sm leading-relaxed whitespace-pre-line">
            {diary.content}
          </p>
        </div>
      </div>
    </div>
  );
};

export default DiaryItem;
