import React from 'react';

interface HeaderProps {
  currentUser: { email: string; nickname: string } | null;
  actionLoading: boolean;
  onBackup: () => void;
  onRestore: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onLogout: () => void;
}

const Header: React.FC<HeaderProps> = ({
  currentUser,
  actionLoading,
  onBackup,
  onRestore,
  onLogout,
}) => {
  return (
    <header className="border-b border-gray-200/80 bg-white/80 backdrop-blur-md sticky top-0 z-50 transition-all duration-300">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div>
            <h1 className="text-lg font-semibold tracking-tight text-gray-950">
              My Memory
            </h1>
            <p className="text-[10px] text-gray-400 font-medium">MongoDB Atlas & Spring Boot Diary</p>
          </div>
        </div>
        {currentUser && (
          <div className="flex items-center gap-2">
            <span className="text-xs font-medium text-gray-600 mr-2">
              <span className="text-black font-semibold">{currentUser.nickname}</span>님
            </span>
            <button
              onClick={onBackup}
              disabled={actionLoading}
              className="px-3 py-1.5 text-xs rounded-lg bg-black hover:bg-neutral-800 text-white transition-colors font-medium cursor-pointer disabled:opacity-50"
            >
              백업
            </button>
            <label
              className={`px-3 py-1.5 text-xs rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-800 border border-transparent font-medium cursor-pointer transition-colors ${
                actionLoading ? 'opacity-50 pointer-events-none' : ''
              }`}
            >
              복원
              <input
                type="file"
                accept=".zip"
                onChange={onRestore}
                disabled={actionLoading}
                className="hidden"
              />
            </label>
            <a
              href="https://drive.google.com"
              target="_blank"
              rel="noopener noreferrer"
              className="px-3 py-1.5 text-xs rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-800 border border-transparent font-medium cursor-pointer transition-colors inline-flex items-center gap-1"
            >
              구글드라이브 이동 ↗
            </a>
            <button
              onClick={onLogout}
              className="px-3 py-1.5 text-xs rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-800 transition-colors border border-transparent font-medium cursor-pointer"
            >
              로그아웃
            </button>
          </div>
        )}
      </div>
    </header>
  );
};

export default Header;
