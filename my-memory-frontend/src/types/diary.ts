export interface Diary {
  diaryId?: string;
  authorEmail: string;
  diaryTitle: string;
  diaryContent: string;
  attachedPhotoUrl?: string;
  writtenDate?: string; // YYYY-MM-DD
}



export interface LoginRequest {
  userEmail: string;
  rawPassword?: string;
}

export interface RegisterRequest {
  userEmail: string;
  rawPassword?: string;
  userNickname: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken?: string;
  userEmail: string;
  userNickname: string;
}
