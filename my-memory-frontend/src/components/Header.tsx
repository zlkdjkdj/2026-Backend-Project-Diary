import React from 'react';

// Header Props 정의
interface HeaderProps {
  currentUser: { email: string; nickname: string } | null;
  onLogout: () => void;
}

// 헤더 컴포넌트
const Header: React.FC<HeaderProps> = ({
  currentUser,
  onLogout,
}) => {
  return (
    <header className="border-b border-gray-200/80 bg-white/80 dark:border-neutral-800/80 dark:bg-neutral-900/80 backdrop-blur-md sticky top-0 z-50 transition-all duration-300">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div>
            <h1 className="text-lg font-semibold tracking-tight text-gray-950 dark:text-white">
              My Memory
            </h1>
            <p className="text-[10px] text-gray-400 dark:text-neutral-500 font-medium">MongoDB Atlas & Spring Boot Diary</p>
          </div>
        </div>
        <div className="flex items-center gap-2">


          {currentUser && (
            <>
              <span className="text-xs font-medium text-gray-600 dark:text-neutral-400 mr-2">
                <span className="text-black dark:text-neutral-100 font-semibold">{currentUser.nickname}</span>님
              </span>
              <button
                onClick={onLogout}
                className="px-3 py-1.5 text-xs rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-800 dark:bg-neutral-800 dark:hover:bg-neutral-700 dark:text-neutral-200 transition-colors border border-transparent font-medium cursor-pointer"
              >
                로그아웃
              </button>
            </>
          )}
        </div>
      </div>
    </header>
  );
};

export default Header;
