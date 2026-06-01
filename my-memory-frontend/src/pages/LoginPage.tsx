import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../api/authApi';

interface LoginPageProps {
  setToken: (token: string | null) => void;
  setCurrentUser: (user: { email: string; nickname: string } | null) => void;
}

const LoginPage: React.FC<LoginPageProps> = ({ setToken, setCurrentUser }) => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [alertMessage, setAlertMessage] = useState<string | null>(null);

  const showAlert = (msg: string) => {
    setAlertMessage(msg);
    setTimeout(() => setAlertMessage(null), 3000);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email.trim() || !password.trim()) {
      alert('이메일과 비밀번호를 입력해주세요.');
      return;
    }

    setLoading(true);
    try {
      const response = await authApi.login({ email, password });
      setToken(response.token);
      setCurrentUser({ email: response.email, nickname: response.nickname });
      localStorage.setItem('token', response.token);
      localStorage.setItem('currentUser', JSON.stringify({ email: response.email, nickname: response.nickname }));
      navigate('/');
    } catch (err: any) {
      showAlert(err.message || '로그인 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-[60vh]">
      {alertMessage && (
        <div className="fixed top-20 right-8 z-50 px-5 py-3 rounded-xl border border-red-200 shadow-sm flex items-center gap-2.5 bg-white text-gray-900">
          <span className="text-xs">✗</span>
          <span className="font-medium text-xs">{alertMessage}</span>
        </div>
      )}

      <div className="bg-white border border-gray-200/80 rounded-2xl p-8 shadow-sm w-full max-w-md">
        <h2 className="text-2xl font-semibold text-gray-950 mb-6 text-center tracking-tight">
          로그인
        </h2>
        
        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="block text-[11px] font-semibold text-gray-500 uppercase tracking-wider mb-1.5">
              이메일
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="example@email.com"
              required
              className="w-full bg-white border border-gray-200 focus:border-black rounded-xl px-4 py-2.5 text-gray-900 placeholder-gray-400 transition-colors outline-none text-sm"
            />
          </div>

          <div>
            <label className="block text-[11px] font-semibold text-gray-500 uppercase tracking-wider mb-1.5">
              비밀번호
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
              className="w-full bg-white border border-gray-200 focus:border-black rounded-xl px-4 py-2.5 text-gray-900 placeholder-gray-400 transition-colors outline-none text-sm"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-black hover:bg-neutral-800 text-white font-medium py-2.5 px-4 rounded-xl transition-colors active:scale-[0.98] disabled:opacity-50 mt-4 text-sm cursor-pointer"
          >
            {loading ? '로그인 중...' : '로그인'}
          </button>
        </form>

        <div className="mt-6 text-center text-xs text-gray-500">
          아직 계정이 없으신가요?
          <Link
            to="/register"
            className="ml-2 text-black font-semibold hover:underline transition-all cursor-pointer"
          >
            회원가입하기
          </Link>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
