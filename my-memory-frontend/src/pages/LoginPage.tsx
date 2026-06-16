import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../api/authApi';

// 로그인 속성
interface LoginPageProps {
  setToken: (token: string | null) => void;
  setCurrentUser: (user: { email: string; nickname: string } | null) => void;
}

// 로그인 페이지
const LoginPage: React.FC<LoginPageProps> = ({
  setToken,
  setCurrentUser,
}) => {
  const navigate = useNavigate();

  // 상태 관리
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [alertMessage, setAlertMessage] = useState<string | null>(null);

  // 알림 설정
  const showAlert = (msg: string) => {
    setAlertMessage(msg);
    setTimeout(() => setAlertMessage(null), 3000);
  };

  // 폼 제출 처리
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email.trim() || !password.trim()) {
      alert('이메일과 비밀번호를 입력해주세요.');
      return;
    }

    setLoading(true);
    try {
      //로그인 API 호출
      const response = await authApi.login({ userEmail: email, rawPassword: password });
      setToken(response.accessToken);
      setCurrentUser({ email: response.userEmail, nickname: response.userNickname });
      localStorage.setItem('token', response.accessToken);
      localStorage.setItem('currentUser', JSON.stringify({ email: response.userEmail, nickname: response.userNickname }));
      navigate('/');
    } catch (err: any) {
      showAlert(err.message || '로그인 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#fbfbfd] dark:bg-[#0b0b0c] text-[#1d1d1f] dark:text-[#f5f5f7] flex items-center justify-center p-4 lg:p-12 relative transition-colors duration-300">

      {alertMessage && (
        <div className="fixed top-20 right-8 z-50 px-5 py-3 rounded-xl border border-red-200 dark:border-red-900/50 shadow-sm flex items-center gap-2.5 bg-white dark:bg-neutral-900 text-gray-900 dark:text-neutral-100 animate-in fade-in slide-in-from-top-2 duration-200">
          <span className="text-xs">✗</span>
          <span className="font-medium text-xs">{alertMessage}</span>
        </div>
      )}

      {/* Main Container: Wide Layout */}
      <div className="max-w-6xl w-full grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-16 items-center py-8 lg:py-12">

        {/* Left Column: Service Introduction */}
        <div className="lg:col-span-7 space-y-6 animate-in fade-in slide-in-from-left-4 duration-500 text-left">
          <div className="space-y-3">
            <span className="text-[10px] font-extrabold text-blue-600 dark:text-blue-400 uppercase tracking-widest bg-blue-50 dark:bg-blue-950/40 px-3 py-1 rounded-full inline-block">
              My Memory Cloud Diary
            </span>
            <h1 className="text-3xl lg:text-4xl font-extrabold text-gray-950 dark:text-white tracking-tight leading-tight">
              당신의 소중한 순간들을 <br />
              가장 안전하게 기록하는 공간
            </h1>
            <p className="text-xs lg:text-sm text-gray-500 dark:text-neutral-400 leading-relaxed max-w-lg">
              기록은 삶의 흔적을 남기는 가장 아름다운 방법입니다.
              My Memory는 직관적인 디자인과 Spring Boot + MongoDB 기반의 고성능 서버 아키텍처, 그리고 AWS S3 클라우드 스토리지를 통해 당신의 소중한 일상과 일기들이 유실 없이 안전하게 평생 보존될 수 있도록 돕습니다.
            </p>
          </div>

          {/* 핵심 기능 */}
          <div className="grid grid-cols-3 gap-3">
            <div className="p-4 bg-white dark:bg-neutral-900 border border-gray-200/50 dark:border-neutral-800 rounded-2xl shadow-xs">
              <span className="block text-xl mb-1.5">☁️</span>
              <span className="block text-xs font-bold text-gray-900 dark:text-white">Spring Boot & MongoDB</span>
              <span className="block text-[9px] text-gray-400 dark:text-neutral-500 mt-1.5 leading-normal">NoSQL 연동을 통한 유연하고 빠른 데이터 관리</span>
            </div>
            <div className="p-4 bg-white dark:bg-neutral-900 border border-gray-200/50 dark:border-neutral-800 rounded-2xl shadow-xs">
              <span className="block text-xl mb-1.5">🔒</span>
              <span className="block text-xs font-bold text-gray-900 dark:text-white">JWT & Spring Security</span>
              <span className="block text-[9px] text-gray-400 dark:text-neutral-500 mt-1.5 leading-normal">무상태 인증 체계 구축 및 철저한 API 접근 제어</span>
            </div>
            <div className="p-4 bg-white dark:bg-neutral-900 border border-gray-200/50 dark:border-neutral-800 rounded-2xl shadow-xs">
              <span className="block text-xl mb-1.5">📁</span>
              <span className="block text-xs font-bold text-gray-900 dark:text-white">AWS S3 Cloud</span>
              <span className="block text-[9px] text-gray-400 dark:text-neutral-500 mt-1.5 leading-normal">S3 연동을 통한 대용량 이미지의 격리 보관 및 최적화</span>
            </div>
          </div>

          {/* 백업 및 복원 가이드 */}
          <div className="bg-white dark:bg-neutral-900 border border-gray-200/60 dark:border-neutral-800/80 rounded-3xl p-6 space-y-4 shadow-xs">
            <h4 className="text-xs lg:text-sm font-bold text-gray-950 dark:text-white flex items-center gap-2">
              📂 구글 드라이브 활용 백업 & 복원 가이드
            </h4>
            <div className="space-y-3.5 text-[11px] lg:text-xs text-gray-600 dark:text-neutral-300 leading-relaxed">
              <div className="flex gap-2.5">
                <span className="flex-shrink-0 w-5 h-5 rounded-full bg-neutral-100 dark:bg-neutral-800 text-gray-900 dark:text-white font-bold flex items-center justify-center text-[10px]">1</span>
                <p>
                  <strong className="text-gray-900 dark:text-white font-semibold">데이터 백업:</strong> 로그인 후 메인 화면 상단의 <span className="bg-neutral-100 dark:bg-neutral-800 px-1 py-0.5 rounded font-mono text-[10px] dark:text-white">백업</span> 버튼을 클릭하여 일기와 업로드했던 이미지 전체가 안전하게 압축된 <span className="underline decoration-blue-500 decoration-1">zip 백업 파일</span>을 다운로드합니다.
                </p>
              </div>
              <div className="flex gap-2.5">
                <span className="flex-shrink-0 w-5 h-5 rounded-full bg-neutral-100 dark:bg-neutral-800 text-gray-900 dark:text-white font-bold flex items-center justify-center text-[10px]">2</span>
                <p>
                  <strong className="text-gray-900 dark:text-white font-semibold">구글 드라이브 보관:</strong> 제공되는 구글 드라이브 이동 링크나 위젯을 활용해 다운로드한 zip 파일을 본인의 구글 드라이브 클라우드 공간에 업로드하여 이중으로 안전하게 보관합니다.
                </p>
              </div>
              <div className="flex gap-2.5">
                <span className="flex-shrink-0 w-5 h-5 rounded-full bg-neutral-100 dark:bg-neutral-800 text-gray-900 dark:text-white font-bold flex items-center justify-center text-[10px]">3</span>
                <p>
                  <strong className="text-gray-900 dark:text-white font-semibold">데이터 복원:</strong> 새 기기 접속이나 복구가 필요할 때, 메인 화면의 <span className="bg-neutral-100 dark:bg-neutral-800 px-1 py-0.5 rounded font-mono text-[10px] dark:text-white">복원</span> 버튼을 누르고 구글 드라이브에 올렸던 zip 파일을 선택하면 기존 모든 데이터가 완벽히 되살아납니다.
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* 로그인 폼 */}
        <div className="lg:col-span-5 w-full flex justify-center lg:justify-end animate-in fade-in slide-in-from-right-4 duration-500">
          <div className="bg-white dark:bg-neutral-900 border border-gray-250/70 dark:border-neutral-800 rounded-3xl p-8 lg:p-12 shadow-lg w-full max-w-xl">
            <h2 className="text-2xl lg:text-3xl font-extrabold text-gray-950 dark:text-white mb-8 text-center tracking-tight">
              로그인
            </h2>

            <form onSubmit={handleSubmit} className="space-y-6">
              <div>
                <label className="block text-[11px] font-bold text-gray-500 dark:text-neutral-400 uppercase tracking-wider mb-2">
                  이메일 주소
                </label>
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="name@example.com"
                  required
                  className="w-full bg-white dark:bg-neutral-800 border border-gray-200 dark:border-neutral-700 focus:border-black dark:focus:border-white rounded-xl px-4 py-3 text-base text-gray-900 dark:text-white placeholder-gray-400 transition-colors outline-none shadow-xs"
                />
              </div>

              <div>
                <label className="block text-[11px] font-bold text-gray-500 dark:text-neutral-400 uppercase tracking-wider mb-2">
                  비밀번호
                </label>
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  required
                  className="w-full bg-white dark:bg-neutral-800 border border-gray-200 dark:border-neutral-700 focus:border-black dark:focus:border-white rounded-xl px-4 py-3 text-base text-gray-900 dark:text-white placeholder-gray-400 transition-colors outline-none shadow-xs"
                />
              </div>

              <button
                type="submit"
                disabled={loading}
                className="w-full bg-black dark:bg-white hover:bg-neutral-800 dark:hover:bg-neutral-100 text-white dark:text-black font-semibold py-3 px-4 rounded-xl transition-all active:scale-[0.98] disabled:opacity-50 mt-8 text-base cursor-pointer shadow-md shadow-neutral-900/5 dark:shadow-none"
              >
                {loading ? '로그인 중...' : '로그인'}
              </button>
            </form>

            <div className="mt-8 text-center text-sm text-gray-500 dark:text-neutral-400 border-t border-gray-150 dark:border-neutral-800 pt-6">
              아직 계정이 없으신가요?
              <Link
                to="/register"
                className="ml-2 text-black dark:text-white font-bold hover:underline transition-all cursor-pointer text-sm"
              >
                회원가입하기
              </Link>
            </div>
          </div>
        </div>

      </div>
    </div>
  );
};

export default LoginPage;
