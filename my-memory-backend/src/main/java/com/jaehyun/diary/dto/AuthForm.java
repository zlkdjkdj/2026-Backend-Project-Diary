package com.jaehyun.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthForm {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginForm {
        private String userEmail;
        private String rawPassword;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterForm {
        private String userEmail;
        private String rawPassword;
        private String userNickname;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String userEmail;
        private String userNickname;
    }
}
