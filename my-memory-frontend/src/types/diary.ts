export interface Diary {
  id?: string;
  userId: string;
  title: string;
  content: string;
  emotion: string;
  imageUrl?: string;
  createdAt?: string; // YYYY-MM-DD
}



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
  refreshToken?: string;
  email: string;
  nickname: string;
}
