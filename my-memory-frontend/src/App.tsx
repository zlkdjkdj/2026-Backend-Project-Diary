import { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import MainPage from './pages/MainPage';
import './App.css';

// 프론트엔드 애플리케이션 최상위 컴포넌트
// 전역 인증 상태 관리 및 React Router 기반 페이지 접근 제어
// return: React 애플리케이션 최상위 JSX 노드
function App() {
  // 인증 토큰 및 현재 사용자 정보를 저장하는 전역 상태 (초기값은 LocalStorage에서 로드)
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
  const [currentUser, setCurrentUser] = useState<{ email: string; nickname: string } | null>(() => {
    const saved = localStorage.getItem('currentUser');
    return saved ? JSON.parse(saved) : null;
  });

  // 사용자 로그아웃 처리 콜백 함수
  // 전역 상태 및 로컬 스토리지 인증 정보 파기
  const handleLogout = () => {
    setToken(null);
    setCurrentUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
  };

  return (
    <Router>
      <Routes>
        <Route
          path="/login"
          element={
            !token ? (
              <LoginPage
                setToken={setToken}
                setCurrentUser={setCurrentUser}
              />
            ) : (
              <Navigate to="/" replace />
            )
          }
        />
        <Route
          path="/register"
          element={
            !token ? (
              <RegisterPage />
            ) : (
              <Navigate to="/" replace />
            )
          }
        />
        <Route
          path="/"
          element={
            token ? (
              <MainPage
                token={token}
                currentUser={currentUser}
                onLogout={handleLogout}
              />
            ) : (
              <Navigate to="/login" replace />
            )
          }
        />
        {/* 매칭되지 않는 모든 경로는 루트(/)로 리다이렉트 (Fallback Route) */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
