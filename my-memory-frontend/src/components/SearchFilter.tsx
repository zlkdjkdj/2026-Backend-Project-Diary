import React from 'react';

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
    <div className="bg-white border border-gray-200/80 rounded-2xl p-6 shadow-sm space-y-4">
      <div className="flex items-center justify-between border-b border-gray-100 pb-3">
        <h3 className="text-xs font-bold text-gray-900 uppercase tracking-wider">
          검색 및 필터
        </h3>
        <button
          onClick={onReset}
          className="text-xs text-gray-400 hover:text-black transition-colors font-medium cursor-pointer"
        >
          필터 초기화
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-12 gap-4">
        {/* Keyword Input */}
        <div className="md:col-span-5">
          <label className="block text-[10px] font-semibold text-gray-400 mb-1.5 uppercase">키워드 검색</label>
          <input
            type="text"
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            placeholder="제목 또는 내용 검색"
            className="w-full bg-white border border-gray-200 focus:border-black rounded-xl px-3 py-2 text-sm text-gray-900 outline-none transition-colors"
          />
        </div>

        {/* Date Input */}
        <div className="md:col-span-5">
          <label className="block text-[10px] font-semibold text-gray-400 mb-1.5 uppercase">날짜 검색</label>
          <input
            type="date"
            value={searchDate}
            onChange={(e) => setSearchDate(e.target.value)}
            className="w-full bg-white border border-gray-200 focus:border-black rounded-xl px-3 py-2 text-sm text-gray-900 outline-none transition-colors"
          />
        </div>

        {/* Action Buttons (Refresh) */}
        <div className="md:col-span-2 flex items-end justify-end md:justify-center md:border-l md:border-gray-100 md:pl-2">
          <button
            onClick={onRefresh}
            disabled={fetchLoading}
            className="flex flex-col items-center justify-center p-1.5 rounded-xl hover:bg-gray-50 transition-colors disabled:opacity-50 cursor-pointer w-full"
          >
            <span className="text-lg">🔄</span>
            <span className="text-[10px] text-gray-400 font-semibold mt-0.5">새로고침</span>
          </button>
        </div>
      </div>

      {/* Additional filters (Checkbox) */}
      <div className="flex items-center gap-2 pt-2">
        <label className="flex items-center gap-2 text-xs text-gray-755 font-medium cursor-pointer select-none">
          <input
            type="checkbox"
            checked={onlyWithImages}
            onChange={(e) => setOnlyWithImages(e.target.checked)}
            className="w-4 h-4 rounded border-gray-300 text-black focus:ring-black cursor-pointer accent-black"
          />
          이미지 있는 일기만 모아보기
        </label>
      </div>
    </div>
  );
};

export default SearchFilter;
