import React from 'react';

interface GoogleDriveWidgetProps {
  show: boolean;
  onClose: () => void;
}

const GoogleDriveWidget: React.FC<GoogleDriveWidgetProps> = ({ show, onClose }) => {
  if (!show) return null;

  return (
    <div className="fixed bottom-6 right-6 z-40 w-64 bg-white/95 backdrop-blur-md border border-gray-200/80 rounded-3xl p-5 shadow-2xl hover:shadow-neutral-300/30 transition-all duration-300 flex flex-col justify-between min-h-[240px]">
      <div className="flex justify-between items-start">
        <div className="w-10 h-10 bg-blue-600 rounded-2xl flex items-center justify-center text-white text-lg shadow-md shadow-blue-500/20">
          💾
        </div>
        <button
          onClick={onClose}
          className="text-gray-400 hover:text-gray-650 p-1 cursor-pointer transition-colors text-xs font-semibold"
          title="닫기"
        >
          ✕
        </button>
      </div>
      <div className="space-y-2 mt-4 flex-grow">
        <h4 className="text-xs font-bold text-gray-900 tracking-tight">구글 드라이브 백업</h4>
        <p className="text-[11px] text-gray-650 leading-relaxed font-medium">
          로컬 백업 파일을 구글드라이브로 업로드 하면 장소에 상관없이 일기 조회가 가능합니다!
        </p>
      </div>
      <a
        href="https://drive.google.com"
        target="_blank"
        rel="noopener noreferrer"
        className="flex items-center justify-center gap-1.5 w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2.5 px-4 rounded-xl transition-all shadow-lg shadow-blue-600/10 active:scale-[0.98] cursor-pointer text-xs mt-4"
      >
        <span>구글 드라이브 이동</span>
        <span className="text-sm">↗</span>
      </a>
    </div>
  );
};

export default GoogleDriveWidget;
