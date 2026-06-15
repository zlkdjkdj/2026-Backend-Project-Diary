import React from 'react';
import { type Diary } from '../types/diary';
import { getImageUrl } from '../api/apiConfig';

// DiaryItem 컴포넌트 주입 프로퍼티 구조 정의
interface DiaryItemProps {
  diary: Diary;
  onEdit: (diary: Diary) => void;
  onDelete: (id: string) => void;
  onView: (diary: Diary) => void;
}

// 개별 일기 레코드 카드 아이템 컴포넌트
// 썸네일, 제목, 본문 등 메타데이터 시각화 및 상호작용 제공
// param: 단일 일기 객체 및 각 액션 매핑 콜백 함수들
// return: 개별 항목 카드 UI 구성요소
const DiaryItem: React.FC<DiaryItemProps> = ({ diary, onEdit, onDelete, onView }) => {
  return (
    <div 
      onClick={() => onView(diary)}
      className="group bg-white dark:bg-neutral-900 border border-gray-200/80 dark:border-neutral-800 rounded-2xl p-5 transition-all duration-300 hover:shadow-sm hover:translate-y-[-1px] flex flex-col md:flex-row gap-5 cursor-pointer"
    >
      {diary.attachedPhotoUrl && (
        <div className="w-full md:w-36 h-48 md:h-28 rounded-xl overflow-hidden border border-gray-100 dark:border-neutral-800 bg-gray-50 dark:bg-neutral-800 flex-shrink-0">
          <img
            src={getImageUrl(diary.attachedPhotoUrl)}
            alt={diary.diaryTitle}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
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
              <span className="text-xs text-gray-500 dark:text-neutral-400 font-medium">{diary.authorEmail}</span>
              <span className="text-xs text-gray-300 dark:text-neutral-700 font-medium">|</span>
              <span className="text-xs text-gray-400 dark:text-neutral-500 font-medium">
                {diary.writtenDate ? diary.writtenDate.split('T')[0] : '날짜 없음'}
              </span>
            </div>
            <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  onEdit(diary);
                }}
                className="p-1.5 rounded-lg text-gray-400 dark:text-neutral-500 hover:text-black dark:hover:text-white hover:bg-gray-100 dark:hover:bg-neutral-800 transition-all cursor-pointer"
                title="수정"
              >
                ✏️
              </button>
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  diary.diaryId && onDelete(diary.diaryId);
                }}
                className="p-1.5 rounded-lg text-gray-400 dark:text-neutral-500 hover:text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-950/20 transition-all cursor-pointer"
                title="삭제"
              >
                🗑️
              </button>
            </div>
          </div>
          {/* Title */}
          <h4 className="text-gray-900 dark:text-white font-semibold text-base mb-1.5 transition-colors">
            {diary.diaryTitle}
          </h4>
          {/* Content */}
          <p className="text-gray-600 dark:text-neutral-300 text-sm leading-relaxed line-clamp-2 overflow-hidden text-ellipsis whitespace-pre-wrap">
            {diary.diaryContent}
          </p>
        </div>
      </div>
    </div>
  );
};

export default DiaryItem;
