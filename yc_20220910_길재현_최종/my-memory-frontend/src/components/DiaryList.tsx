import React from 'react';
import type { Diary } from '../types/diary';
import DiaryItem from './DiaryItem';

// 목록 속성
interface DiaryListProps {
  diaries: Diary[];
  fetchLoading: boolean;
  onEdit: (diary: Diary) => void;
  onDelete: (id: string) => void;
  onView: (diary: Diary) => void;
}

// 목록 컴포넌트
const DiaryList: React.FC<DiaryListProps> = ({ diaries, fetchLoading, onEdit, onDelete, onView }) => {
  // 데이터가 없고 로딩 중이 아닐 때의 화면 처리
  if (diaries.length === 0 && !fetchLoading) {
    return (
      <div className="text-center py-16 bg-white border border-dashed border-gray-250/80 rounded-2xl">
        <span className="text-3xl">📭</span>
        <p className="text-gray-500 font-medium text-sm mt-3">기록된 일기가 없습니다.</p>
        <p className="text-gray-400 text-xs mt-1">오늘의 기억을 기록해보세요.</p>
      </div>
    );
  }

  // 일기 목록 렌더링
  return (
    <div className="space-y-4">
      {diaries.map((diary) => (
        <DiaryItem key={diary.diaryId} diary={diary} onEdit={onEdit} onDelete={onDelete} onView={onView} />
      ))}
    </div>
  );
};

export default DiaryList;
