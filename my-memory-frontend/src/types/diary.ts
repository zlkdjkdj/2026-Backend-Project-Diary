export interface Diary {
  id?: string;
  userId: string;
  title: string;
  content: string;
  emotion: string;
  imageUrl?: string;
  createdAt?: string; // YYYY-MM-DD
}

export const EMOTIONS = [
  { name: 'Happy', emoji: '😊', color: 'emerald', label: '행복함' },
  { name: 'Excited', emoji: '😆', color: 'amber', label: '신남' },
  { name: 'Calm', emoji: '😌', color: 'sky', label: '평온함' },
  { name: 'Tired', emoji: '😴', color: 'indigo', label: '피곤함' },
  { name: 'Sad', emoji: '😢', color: 'blue', label: '슬픔' },
  { name: 'Angry', emoji: '😡', color: 'rose', label: '화남' },
];

export interface LoginRequest {
  email: string;
  password?: string;
}

export interface RegisterRequest {
  email: string;
  password?: string;
  nickname: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  nickname: string;
}
