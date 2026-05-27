import React, { useState } from 'react';
import type { LoginRequest, RegisterRequest } from '../types/diary';

interface AuthFormProps {
  onLogin: (request: LoginRequest) => void;
  onRegister: (request: RegisterRequest) => void;
  isLoading: boolean;
}

const AuthForm: React.FC<AuthFormProps> = ({ onLogin, onRegister, isLoading }) => {
  const [isLoginMode, setIsLoginMode] = useState(true);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [nickname, setNickname] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!email.trim() || !password.trim()) {
      alert('이메일과 비밀번호를 입력해주세요.');
      return;
    }
    if (isLoginMode) {
      onLogin({ email, password });
    } else {
      if (!nickname.trim()) {
        alert('닉네임을 입력해주세요.');
        return;
      }
      onRegister({ email, password, nickname });
    }
  };

  return (
    <div className="flex items-center justify-center min-h-[60vh]">
      <div className="bg-white border border-gray-200/80 rounded-2xl p-8 shadow-sm w-full max-w-md">
        <h2 className="text-2xl font-semibold text-gray-950 mb-6 text-center tracking-tight">
          {isLoginMode ? '로그인' : '회원가입'}
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

          {!isLoginMode && (
            <div>
              <label className="block text-[11px] font-semibold text-gray-500 uppercase tracking-wider mb-1.5">
                닉네임
              </label>
              <input
                type="text"
                value={nickname}
                onChange={(e) => setNickname(e.target.value)}
                placeholder="사용할 닉네임"
                required
                className="w-full bg-white border border-gray-200 focus:border-black rounded-xl px-4 py-2.5 text-gray-900 placeholder-gray-400 transition-colors outline-none text-sm"
              />
            </div>
          )}

          <button
            type="submit"
            disabled={isLoading}
            className="w-full bg-black hover:bg-neutral-800 text-white font-medium py-2.5 px-4 rounded-xl transition-colors active:scale-[0.98] disabled:opacity-50 mt-4 text-sm cursor-pointer"
          >
            {isLoginMode ? '로그인' : '회원가입'}
          </button>
        </form>

        <div className="mt-6 text-center text-xs text-gray-500">
          {isLoginMode ? '아직 계정이 없으신가요?' : '이미 계정이 있으신가요?'}
          <button
            type="button"
            onClick={() => setIsLoginMode(!isLoginMode)}
            className="ml-2 text-black font-semibold hover:underline transition-all cursor-pointer"
          >
            {isLoginMode ? '회원가입하기' : '로그인하기'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default AuthForm;
