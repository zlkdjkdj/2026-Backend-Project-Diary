package com.jaehyun.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 인증 정보 DTO
public class AuthForm {

    // 로그인 폼
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginForm {
        private String userEmail;
        private String rawPassword;
    }

    // 회원가입 폼
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterForm {
        private String userEmail;
        private String rawPassword;
        private String userNickname;
    }

    // 토큰 응답
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String userEmail;
        private String userNickname;
    }
}
