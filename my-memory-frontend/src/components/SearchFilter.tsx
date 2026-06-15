import React from 'react';

// SearchFilter Props 정의
interface SearchFilterProps {
  searchKeyword: string;
  setSearchKeyword: (val: string) => void;
  searchDate: string;
  setSearchDate: (val: string) => void;
  onlyWithImages: boolean;
  setOnlyWithImages: (val: boolean) => void;
  onReset: () => void;
  onRefresh: () => void;
  fetchLoading: boolean;
}

// 검색 필터 컴포넌트
const SearchFilter: React.FC<SearchFilterProps> = ({
  searchKeyword,
  setSearchKeyword,
  searchDate,
  setSearchDate,
  onlyWithImages,
  setOnlyWithImages,
  onReset,
  onRefresh,
  fetchLoading,
}) => {
  return (
    <div className="bg-white dark:bg-neutral-900 border border-gray-200/80 dark:border-neutral-800 rounded-2xl p-6 shadow-sm space-y-4">
      <div className="flex items-center justify-between border-b border-gray-100 dark:border-neutral-800 pb-3">
        <h3 className="text-xs font-bold text-gray-900 dark:text-white uppercase tracking-wider">
          검색 및 필터
        </h3>
        <button
          onClick={onReset}
          className="text-xs text-gray-400 dark:text-neutral-500 hover:text-black dark:hover:text-white transition-colors font-medium cursor-pointer"
        >
          필터 초기화
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-12 gap-4">
        {/* Keyword Input */}
        <div className="md:col-span-5">
          <label className="block text-[10px] font-semibold text-gray-400 dark:text-neutral-500 mb-1.5 uppercase">키워드 검색</label>
          <input
            type="text"
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            placeholder="제목 또는 내용 검색"
            className="w-full bg-white dark:bg-neutral-800 border border-gray-200 dark:border-neutral-700 focus:border-black dark:focus:border-white rounded-xl px-3 py-2 text-sm text-gray-900 dark:text-white outline-none transition-colors"
          />
        </div>

        {/* Date Input */}
        <div className="md:col-span-5">
          <label className="block text-[10px] font-semibold text-gray-400 dark:text-neutral-500 mb-1.5 uppercase">날짜 검색</label>
          <input
            type="date"
            value={searchDate}
            onChange={(e) => setSearchDate(e.target.value)}
            className="w-full bg-white dark:bg-neutral-800 border border-gray-200 dark:border-neutral-700 focus:border-black dark:focus:border-white rounded-xl px-3 py-2 text-sm text-gray-900 dark:text-white outline-none transition-colors"
          />
        </div>

        {/* Action Buttons (Refresh) */}
        <div className="md:col-span-2 flex items-end justify-end md:justify-center md:border-l md:border-gray-100 dark:md:border-neutral-800 md:pl-2">
          <button
            onClick={onRefresh}
            disabled={fetchLoading}
            className="flex items-center justify-center p-1.5 rounded-xl hover:bg-gray-50 dark:hover:bg-neutral-800 transition-colors disabled:opacity-50 cursor-pointer w-full h-9 border border-gray-200 dark:border-neutral-700 text-sm font-semibold"
          >
            <span className="text-xs text-gray-700 dark:text-neutral-300">새로고침</span>
          </button>
        </div>
      </div>

      {/* Additional filters (Checkbox) */}
      <div className="flex items-center gap-2 pt-2">
        <label className="flex items-center gap-2 text-xs text-gray-700 dark:text-neutral-300 font-medium cursor-pointer select-none">
          <input
            type="checkbox"
            checked={onlyWithImages}
            onChange={(e) => setOnlyWithImages(e.target.checked)}
            className="w-4 h-4 rounded border-gray-300 dark:border-neutral-700 text-black dark:text-white focus:ring-black dark:focus:ring-white cursor-pointer accent-black dark:accent-white"
          />
          이미지 있는 일기만 모아보기
        </label>
      </div>
    </div>
  );
};

export default SearchFilter;
