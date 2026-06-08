import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../api/authApi';

const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [nickname, setNickname] = useState('');
  const [loading, setLoading] = useState(false);
  const [alertMessage, setAlertMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  const showAlert = (text: string, type: 'success' | 'error' = 'success') => {
    setAlertMessage({ type, text });
    setTimeout(() => setAlertMessage(null), 3000);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email.trim() || !password.trim() || !nickname.trim()) {
      alert('모든 필드를 채워주세요.');
      return;
    }

    setLoading(true);
    try {
      const msg = await authApi.register({ userEmail: email, rawPassword: password, userNickname: nickname });
      showAlert(msg, 'success');
      setTimeout(() => {
        navigate('/login');
      }, 1000);
    } catch (err: any) {
      showAlert(err.message || '회원가입 중 오류가 발생했습니다.', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-[#fbfbfd] dark:bg-[#0b0b0c] text-[#1d1d1f] dark:text-[#f5f5f7] p-4 relative">


      {alertMessage && (
        <div className={`fixed top-20 right-8 z-50 px-5 py-3 rounded-xl border shadow-sm flex items-center gap-2.5 bg-white dark:bg-neutral-900 text-gray-900 dark:text-neutral-100 ${
          alertMessage.type === 'success' ? 'border-gray-200 dark:border-neutral-800' : 'border-red-200 dark:border-red-900/50'
        }`}>
          <span className="text-xs">{alertMessage.type === 'success' ? '✓' : '✗'}</span>
          <span className="font-medium text-xs">{alertMessage.text}</span>
        </div>
      )}

      <div className="bg-white dark:bg-neutral-900 border border-gray-200/80 dark:border-neutral-800 rounded-2xl p-8 shadow-sm w-full max-w-md">
        <h2 className="text-2xl font-semibold text-gray-950 dark:text-white mb-6 text-center tracking-tight">
          회원가입
        </h2>
        
        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="block text-[11px] font-semibold text-gray-500 dark:text-neutral-400 uppercase tracking-wider mb-1.5">
              이메일
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="example@email.com"
              required
              className="w-full bg-white dark:bg-neutral-800 border border-gray-200 dark:border-neutral-700 focus:border-black dark:focus:border-white rounded-xl px-4 py-2.5 text-gray-900 dark:text-white placeholder-gray-400 transition-colors outline-none text-sm"
            />
          </div>

          <div>
            <label className="block text-[11px] font-semibold text-gray-500 dark:text-neutral-400 uppercase tracking-wider mb-1.5">
              비밀번호
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
              className="w-full bg-white dark:bg-neutral-800 border border-gray-200 dark:border-neutral-700 focus:border-black dark:focus:border-white rounded-xl px-4 py-2.5 text-gray-900 dark:text-white placeholder-gray-400 transition-colors outline-none text-sm"
            />
          </div>

          <div>
            <label className="block text-[11px] font-semibold text-gray-500 dark:text-neutral-400 uppercase tracking-wider mb-1.5">
              닉네임
            </label>
            <input
              type="text"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              placeholder="사용할 닉네임"
              required
              className="w-full bg-white dark:bg-neutral-800 border border-gray-200 dark:border-neutral-700 focus:border-black dark:focus:border-white rounded-xl px-4 py-2.5 text-gray-900 dark:text-white placeholder-gray-400 transition-colors outline-none text-sm"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-black dark:bg-white hover:bg-neutral-800 dark:hover:bg-neutral-100 text-white dark:text-black font-medium py-2.5 px-4 rounded-xl transition-colors active:scale-[0.98] disabled:opacity-50 mt-4 text-sm cursor-pointer"
          >
            {loading ? '가입 중...' : '회원가입'}
          </button>
        </form>

        <div className="mt-6 text-center text-xs text-gray-500 dark:text-neutral-400">
          이미 계정이 있으신가요?
          <Link
            to="/login"
            className="ml-2 text-black dark:text-white font-semibold hover:underline transition-all cursor-pointer"
          >
            로그인하기
          </Link>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
