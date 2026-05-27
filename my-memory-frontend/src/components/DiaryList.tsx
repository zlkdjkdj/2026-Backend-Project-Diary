import React from 'react';
import type { Diary } from '../types/diary';
import DiaryItem from './DiaryItem';

interface DiaryListProps {
  diaries: Diary[];
  fetchLoading: boolean;
  onEdit: (diary: Diary) => void;
  onDelete: (id: string) => void;
}

const DiaryList: React.FC<DiaryListProps> = ({ diaries, fetchLoading, onEdit, onDelete }) => {
  if (diaries.length === 0 && !fetchLoading) {
    return (
      <div className="text-center py-16 bg-slate-900/20 border border-dashed border-slate-800/80 rounded-2xl">
        <span className="text-4xl">📭</span>
        <p className="text-slate-400 font-medium text-sm mt-3">기록된 일기가 없습니다.</p>
        <p className="text-slate-600 text-xs mt-1">오늘 있었던 하루를 첫 번째 일기로 남겨보세요!</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {diaries.map((diary) => (
        <DiaryItem key={diary.id} diary={diary} onEdit={onEdit} onDelete={onDelete} />
      ))}
    </div>
  );
};

export default DiaryList;
