import { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import MainPage from './pages/MainPage';
import './App.css';

// 앱 루트
function App() {
  // 인증 토큰 및 유저 정보 상태 관리 (로컬스토리지 연동)
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
  const [currentUser, setCurrentUser] = useState<{ email: string; nickname: string } | null>(() => {
    const saved = localStorage.getItem('currentUser');
    return saved ? JSON.parse(saved) : null;
  });

  // 로그아웃 처리
  const handleLogout = () => {
    setToken(null);
    setCurrentUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
  };

  return (
    <Router>
      <Routes>
        {/* 로그인 페이지 라우트 (인증된 유저는 메인으로 리다이렉트) */}
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
        {/* 회원가입 페이지 라우트 */}
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
        {/* 메인 페이지 라우트 (비인증 유저는 로그인으로 리다이렉트) */}
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
        {/* 알 수 없는 경로는 메인으로 이동 */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
