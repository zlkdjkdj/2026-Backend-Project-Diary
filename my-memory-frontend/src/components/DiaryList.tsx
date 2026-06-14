import React from 'react';
import type { Diary } from '../types/diary';
import DiaryItem from './DiaryItem';

// DiaryList 컴포넌트 주입 프로퍼티 구조 선언
interface DiaryListProps {
  diaries: Diary[];
  fetchLoading: boolean;
  onEdit: (diary: Diary) => void;
  onDelete: (id: string) => void;
  onView: (diary: Diary) => void;
}

// 다수 일기 레코드 목록(List) 구조 렌더링 컨테이너 컴포넌트
// DiaryItem 하위 엔티티 생성 위임 및 순회 DOM 구성
// param: 렌더링 대상 일기 배열, 로딩 상태 및 항목 액션 콜백
// return: DiaryItem 목록 또는 데이터 부재 빈 상태(Empty State) 레이아웃
const DiaryList: React.FC<DiaryListProps> = ({ diaries, fetchLoading, onEdit, onDelete, onView }) => {
  if (diaries.length === 0 && !fetchLoading) {
    return (
      <div className="text-center py-16 bg-white border border-dashed border-gray-250/80 rounded-2xl">
        <span className="text-3xl">📭</span>
        <p className="text-gray-500 font-medium text-sm mt-3">기록된 일기가 없습니다.</p>
        <p className="text-gray-400 text-xs mt-1">오늘의 기억을 기록해보세요.</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {diaries.map((diary) => (
        <DiaryItem key={diary.diaryId} diary={diary} onEdit={onEdit} onDelete={onDelete} onView={onView} />
      ))}
    </div>
  );
};

export default DiaryList;
